package org.nekosoft.shlink.sec.user.impl

import mu.KotlinLogging
import org.nekosoft.shlink.sec.user.*
import org.nekosoft.shlink.sec.user.rest.UserListOptions
import org.nekosoft.utils.text.generateRandomText
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.UUID
import javax.annotation.PostConstruct

private val kLogger = KotlinLogging.logger {}

@Service
@ConditionalOnExpression("'\${nekoshlink.security.userbase}'=='NATIVE' || '\${nekoshlink.security.userbase}' == 'X509'")
class UserDetailsServiceImpl(
    val repo: UserRepository,
    val roles: RoleRepository,
    val pwdEncoder: PasswordEncoder,
) : UserDataAccess {

    @PostConstruct
    fun initAdminUser() {
        if (repo.count() == 0L) {
            val pwd = generateRandomText()
            kLogger.warn("New root user was created with username 'nekoadm' and password '$pwd'")
            val rootUser = User(
                    username = "nekoadm",
                    password = pwd,
                    roles = mutableSetOf(
                        Role(privilege = ShlinkPrivilege.Admin, realm = ShlinkRealm.Everything),
                        // Role hierarchy will guarantee that this user can also act as Editor and Viewer on all realms
                    ),
            )
            createUser(rootUser)
        }
    }

    override fun loadUserByUsername(username: String): NekoShlinkUserDetails {
        val user = repo.findByUsernameAndEnabledIsTrue(username)
            ?: throw UsernameNotFoundException("User [$username] does not exist")
        return NekoShlinkUserDetails(user)
    }

    override fun loadUserByApiKey(apiKey: String): NekoShlinkUserDetails {
        val user = repo.findByLegacyApiKeyAndEnabledIsTrue(apiKey)
            ?: throw UsernameNotFoundException("User with given key does not exist")
        return NekoShlinkUserDetails(user)
    }

    override fun loadAllUsers(options: UserListOptions, pageable: Pageable?): Page<NekoShlinkUserDetails> {
        val users = repo.findAll(pageable ?: Pageable.unpaged())
        return users.map { NekoShlinkUserDetails(it) }
    }

    /**
     * Create a new user with the supplied details.
     */
    @Transactional
    override fun createUser(user: User): User {
        if (user.password == null) {
            throw IllegalArgumentException("Password must be set when creating a new user")
        }
        user.password = pwdEncoder.encode(user.password)
        user.roles.forEach { it.user = user }
        return repo.save(user)
    }

    /**
     * Update the specified user. It will not change the password, or the user ID.
     */
    @Transactional
    override fun updateUser(user: User): User {
        user.roles.forEach { it.user = user }
        val curUser = repo.getById(user.id!!)
        curUser.lastModifiedDate = LocalDateTime.now()
        curUser.username = user.username
        curUser.enabled = user.enabled
        curUser.description = user.description
        curUser.firstName = user.firstName
        curUser.lastName = user.lastName
        curUser.legacyApiKey = user.legacyApiKey
        resolveRoleRelationship(user.roles, curUser)
        return repo.save(curUser)
    }

    /**
     * Remove the user with the given login name from the system.
     */
    @Transactional
    override fun deleteUser(username: String) {
        repo.deleteByUsername(username)
    }

    /**
     * Modify the current user's password. This should change the user's password in the
     * persistent user repository (datbase, LDAP etc).
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Transactional
    override fun changePassword(username: String, oldPassword: String, newPassword: String) {
        val user = repo.findByUsernameAndPasswordAndEnabledIsTrue(username, pwdEncoder.encode(oldPassword))
            ?: throw UsernameNotFoundException("User [$username] could not be found with the given password")
        user.password = pwdEncoder.encode(newPassword)
        repo.save(user)
    }

    /**
     * Add a legacy API key to the user
     */
    override fun addApiKey(username: String, key: String?) {
        if (key != null && repo.findByLegacyApiKeyAndEnabledIsTrue(key) != null) {
            throw IllegalArgumentException("Key already exists")
        }
        val user = repo.findByUsernameAndEnabledIsTrue(username)
            ?: throw UsernameNotFoundException("User [$username] could not be found")
        user.legacyApiKey = key ?: UUID.randomUUID().toString()
        repo.save(user)
    }

    /**
     * Remove the legacy API key from the user
     */
    override fun removeApiKey(username: String) {
        val user = repo.findByUsernameAndEnabledIsTrue(username)
            ?: throw UsernameNotFoundException("User [$username] could not be found")
        user.legacyApiKey = null
        repo.save(user)
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    override fun userExists(username: String): Boolean =
        repo.findByUsernameAndEnabledIsTrue(username) != null

    private fun resolveRoleRelationship(newRoles: Set<Role>, user: User) {
        val roleNames = newRoles.map { it.privilege.name }
        user.roles.retainAll {
            if (it.privilege.name in roleNames) {
                true
            } else {
                it.user = null
                false
            }
        }
        newRoles.forEach {
            if (user.roles.find { t -> t.privilege == it.privilege } == null) {
                val r = roles.findByPrivilegeAndRealmAndUser(it.privilege, it.realm, user) ?: Role(user = user, privilege = it.privilege, realm = it.realm)
                user.roles.add(r)
            }
        }
    }

}
