package org.nekosoft.shlink.sec.user.impl

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.sec.user.User
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
@ConditionalOnExpression("'\${nekoshlink.security.userbase}'=='NATIVE' || '\${nekoshlink.security.userbase}' == 'X509'")
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsernameAndEnabledIsTrue(username: String): User?

    fun findByLegacyApiKeyAndEnabledIsTrue(key: String): User?

    fun findByUsernameAndPasswordAndEnabledIsTrue(username: String, password: String): User?

    fun deleteByUsername(username: String)

}