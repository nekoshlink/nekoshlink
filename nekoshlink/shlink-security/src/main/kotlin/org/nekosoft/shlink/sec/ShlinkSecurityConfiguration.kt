package org.nekosoft.shlink.sec

import mu.KotlinLogging
import org.nekosoft.shlink.sec.roles.ShlinkRoleHierarchyImpl
import org.nekosoft.utils.sec.apikey.ApiKeyAuthenticationProvider
import org.nekosoft.utils.sec.apikey.ApiKeyAuthenticationResolver
import org.nekosoft.utils.sec.delegation.RunAs
import org.nekosoft.utils.sec.delegation.RunAsDelegationAspect
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import java.util.*
import javax.annotation.PostConstruct

private val kLogger = KotlinLogging.logger {}

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class ShlinkSecurityConfiguration {

    @Value("\${nekoshlink.security.delegation.anonymous-key:nekoshlink-runas-role-key}")
    private lateinit var runasAnonymousKey: String

    @Value("\${nekoshlink.security.api-key.passcode:#{null}}")
    private var serverApiKey: String? = null

    @Value("\${nekoshlink.security.api-key.role:ROLE_API}")
    private lateinit var serverApiRole: String

    @Value("\${nekoshlink.security.api-key.username:apiuser}")
    private lateinit var serverApiUsername: String

    @Value("\${nekoshlink.security.userbase:NATIVE}")
    private lateinit var userBase: ShlinkUserBase

    @PostConstruct
    fun init() {
        RunAs.anonymousKey = runasAnonymousKey
        kLogger.info("Using $userBase as a user base")
    }

    @Bean
    fun delegationAspect(): RunAsDelegationAspect =
        RunAsDelegationAspect()

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun apikeyAuthenticationProvider(resolver: Optional<ApiKeyAuthenticationResolver>) =
        ApiKeyAuthenticationProvider(resolver, serverApiKey, serverApiRole, serverApiUsername)

    @Bean
    @ConditionalOnNotWebApplication
    @ConditionalOnProperty("nekoshlink.security.userbase", havingValue = "OIDC", matchIfMissing = false)
    fun jwtAuthProvider(
        @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") issuerUri: String,
        converter: JwtAuthenticationConverter,
    ): JwtAuthenticationProvider {
        val provider = JwtAuthenticationProvider(
            JwtDecoders.fromIssuerLocation(issuerUri)
        )
        provider.setJwtAuthenticationConverter(converter)
        return provider
    }

    @Bean
    @ConditionalOnProperty("nekoshlink.security.userbase", havingValue = "OIDC", matchIfMissing = false)
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtGrantedAuthConverter = JwtShlinkGrantedAuthoritiesConverter()
        jwtGrantedAuthConverter.jwtConverter.setAuthoritiesClaimName("nkshlink-roles")
        jwtGrantedAuthConverter.jwtConverter.setAuthorityPrefix("ROLE_")
        val jwtAuthConverter = JwtAuthenticationConverter()
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthConverter)
        return jwtAuthConverter
    }

    @Bean
    @ConditionalOnExpression("'\${nekoshlink.security.userbase}'=='NATIVE' || '\${nekoshlink.security.userbase}' == 'X509'")
    fun userProvider(userService: UserDetailsService, pwdEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val userProvider = DaoAuthenticationProvider()
        userProvider.setUserDetailsService(userService)
        userProvider.setPasswordEncoder(pwdEncoder)
        return userProvider
    }

    @Bean
    @ConditionalOnNotWebApplication
    fun authManager(
        apiKeyProvider: ApiKeyAuthenticationProvider,
        userProvider: AuthenticationProvider,
    ): AuthenticationManager = ProviderManager(
        apiKeyProvider,
        userProvider,
        AnonymousAuthenticationProvider("NekoShlink")
    )

    companion object {

        @Bean
        fun roleHierarchy(): RoleHierarchy {
            val hierarchy = ShlinkRoleHierarchyImpl()
            hierarchy.setHierarchy("""
            Admin > Editor
            Editor > Viewer
            StatsViewer > Viewer
            Everything > Domains
            Everything > ShortUrls
            Everything > Tags
            Everything > Visits
            Everything > Users
            Everything > Management
        """.trimIndent())
            return hierarchy
        }

        @Bean
        fun methodSecurityExpressionHandler(roleHierarchy: RoleHierarchy) : MethodSecurityExpressionHandler {
            val handler = DefaultMethodSecurityExpressionHandler();
            handler.setRoleHierarchy(roleHierarchy)
            return handler;
        }

        const val VERSION_STRING = "1"
    }

}