package org.nekosoft.shlink.sec.roles

import mu.KotlinLogging
import org.nekosoft.shlink.sec.user.ShlinkGrantedAuthority
import org.nekosoft.shlink.sec.user.ShlinkPrivilege
import org.nekosoft.shlink.sec.user.ShlinkRealm
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

private val kLogger = KotlinLogging.logger {}

class ShlinkRoleHierarchyImpl : RoleHierarchyImpl() {

    override fun getReachableGrantedAuthorities(authorities: MutableCollection<out GrantedAuthority>?): MutableCollection<GrantedAuthority>? {
        if (authorities == null) {
            return null
        }
        val result = mutableSetOf<GrantedAuthority>()
        for (authority in authorities) {
            val (realmString, privilegeString) = if (authority !is ShlinkGrantedAuthority) {
                val splits = authority.authority.split(":", limit = 2)
                if (splits.size == 1) {
                    val roles = super.getReachableGrantedAuthorities(mutableSetOf(authority))
                    result.addAll(roles)
                    continue
                } else {
                    Pair(splits.component1().replaceFirst("ROLE_", ""), splits.component2().replaceFirst("ROLE_", ""))
                }
            } else {
                Pair(authority.realm.toString(), authority.privilege.toString())
            }
            val privileges = super.getReachableGrantedAuthorities(mutableSetOf(SimpleGrantedAuthority(privilegeString)))
            val realms = super.getReachableGrantedAuthorities(mutableSetOf(SimpleGrantedAuthority(realmString)))
            for (privilege in privileges) {
                for (realm in realms) {
                    result.add(ShlinkGrantedAuthority(ShlinkPrivilege.valueOf(privilege.authority), ShlinkRealm.valueOf(realm.authority)))
                }
            }
            kLogger.debug("getReachableGrantedAuthorities() - From the roles $authorities one can reach $result in zero or more steps.")
        }
        return result
    }

}