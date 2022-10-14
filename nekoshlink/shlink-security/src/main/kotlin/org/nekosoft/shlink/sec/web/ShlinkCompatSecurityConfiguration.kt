package org.nekosoft.shlink.sec.web

import org.nekosoft.utils.sec.apikey.ApiKeyAuthenticationFilter
import org.nekosoft.utils.sec.apikey.ApiKeyAuthenticationProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.savedrequest.NullRequestCache

@Configuration
@ConditionalOnWebApplication
class ShlinkCompatSecurityConfiguration {

    @Value("\${nekoshlink.security.api-key.header:X-Api-Key}")
    private lateinit var apikeyHeader: String

    @Bean
    @Order(110) // order is important so that more specific matches come before more general ones
    fun filterChainShlinkCompat(http: HttpSecurity, apikeyAuthProvider: ApiKeyAuthenticationProvider): SecurityFilterChain {
        http {
            securityMatcher(
                "/rest/v2",
                "/rest/v2/**",
            )
            cors {  }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            requestCache { requestCache = NullRequestCache() }
            authorizeRequests { authorize(anyRequest, authenticated) }
            authenticationManager = ProviderManager(apikeyAuthProvider)
            addFilterBefore<AnonymousAuthenticationFilter>(ApiKeyAuthenticationFilter(apikeyHeader))
        }
        return http.build()
    }

}
