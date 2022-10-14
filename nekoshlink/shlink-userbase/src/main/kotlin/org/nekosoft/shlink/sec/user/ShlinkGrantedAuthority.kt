package org.nekosoft.shlink.sec.user

import org.springframework.security.core.GrantedAuthority

class ShlinkGrantedAuthority : GrantedAuthority {
    val privilege: ShlinkPrivilege
    val realm: ShlinkRealm

    constructor(privilege: ShlinkPrivilege, realm: ShlinkRealm) {
        this.privilege = privilege
        this.realm = realm

    }

    constructor(authority: String) {
        val split = authority.split(":").map { it.trim() }
        privilege = ShlinkPrivilege.valueOf(split[0])
        realm = ShlinkRealm.valueOf(split[1]);
    }

    override fun getAuthority(): String =
        "ROLE_${realm}:${privilege}"

    override fun toString(): String {
        return "${realm}:${privilege}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShlinkGrantedAuthority

        if (privilege != other.privilege) return false
        if (realm != other.realm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privilege.hashCode()
        result = 31 * result + realm.hashCode()
        return result
    }

}