package org.nekosoft.shlink.sec

import org.nekosoft.shlink.sec.user.ShlinkGrantedAuthority
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class JwtShlinkGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    val jwtConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): Collection<ShlinkGrantedAuthority>? =
        jwtConverter.convert(jwt)?.map { ShlinkGrantedAuthority(it.authority) }?.toMutableSet()

}