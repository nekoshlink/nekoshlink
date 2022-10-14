package org.nekosoft.shlink.sec.user.impl

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.sec.user.Role
import org.nekosoft.shlink.sec.user.ShlinkPrivilege
import org.nekosoft.shlink.sec.user.ShlinkRealm
import org.nekosoft.shlink.sec.user.User
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
@ConditionalOnExpression("'\${nekoshlink.security.userbase}'=='NATIVE' || '\${nekoshlink.security.userbase}' == 'X509'")
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByPrivilegeAndRealmAndUser(privilege: ShlinkPrivilege, realm: ShlinkRealm, user: User): Role?

}