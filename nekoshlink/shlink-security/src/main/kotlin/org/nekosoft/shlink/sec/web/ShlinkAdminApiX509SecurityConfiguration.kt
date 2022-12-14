package org.nekosoft.shlink.sec.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.NullRequestCache


@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty("nekoshlink.security.userbase", havingValue = "X509", matchIfMissing = false)
class ShlinkAdminApiX509SecurityConfiguration {

    @Autowired
    private lateinit var users: UserDetailsService

    @Bean
    @Order(120) // order is important so that more specific matches come before more general ones
    fun filterChainX509AdminApi(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {  }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            requestCache { requestCache = NullRequestCache() }
            authorizeRequests { authorize(anyRequest, authenticated) }
            x509 { userDetailsService = users }
        }
        return http.build()
    }

}