package org.nekosoft.shlink.dao.impl

import mu.KotlinLogging
import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.*
import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import org.nekosoft.shlink.sec.roles.IsDomainAdmin
import org.nekosoft.shlink.sec.roles.IsDomainEditor
import org.nekosoft.shlink.sec.roles.IsDomainViewer
import org.nekosoft.shlink.service.exception.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import javax.annotation.PostConstruct
import javax.transaction.Transactional

private val kLogger = KotlinLogging.logger {  }

@Service
class JpaDomainDataAccessImpl(private val repo: DomainRepository): DomainDataAccess {

    @Value("\${nekoshlink.initial-default-domain}")
    private lateinit var _defaultDomain: String

    @Value("\${nekoshlink.default-baseurl-redirect:https://shlink.nekosoft.org}")
    private lateinit var defaultBaseUrlRedirect: String
    private lateinit var defaultBaseUrlRedirectUri: URI

    @Value("\${nekoshlink.default-request-error-redirect:#{null}}")
    private var defaultRequestErrorRedirect: String? = null
    private var defaultRequestErrorRedirectUri: URI? = null

    @Value("\${nekoshlink.default-password-form-redirect:#{null}}")
    private var defaultPasswordFormRedirect: String? = null
    private var defaultPasswordFormRedirectUri: URI? = null

    private var _defaultAuthority: String = ""

    @PostConstruct
    fun init() {
        // Let these fail on start-up if not properly configured
        try {
            defaultBaseUrlRedirectUri = URI(defaultBaseUrlRedirect)
            defaultRequestErrorRedirect?.let {
                defaultRequestErrorRedirectUri = URI(it)
            }
            defaultPasswordFormRedirect?.let {
                defaultPasswordFormRedirectUri = URI(it)
            }
        } catch (e: URISyntaxException) {
            throw InvalidDomainDefaultsException(e.input)
        }
        val defaultUrl = try {
            URL(_defaultDomain)
        } catch (_: MalformedURLException) {
            throw InvalidDefaultDomainException(_defaultDomain)
        }

        val currentDefaultDomain = try {
            // return the current default domain, if there is one
            getDefault()
        } catch (_: DefaultDomainDoesNotExistsException) {
            null
        }
        val defaultDomain = if (currentDefaultDomain == null) {
            // if there is no current default domain
            findByAuthority(defaultUrl.authority) ?: create(Domain(scheme = defaultUrl.protocol, authority = defaultUrl.authority))
            makeDefault(defaultUrl.authority)
        } else {
            if (currentDefaultDomain.authority != defaultUrl.authority) {
                kLogger.warn("Your configured default domain [${defaultUrl.authority}] was not set because [${currentDefaultDomain.authority}] is already set as the default")
            }
            currentDefaultDomain
        }
        _defaultAuthority = defaultDomain.authority
    }

    @PreAuthorize("permitAll()")
    override fun getDefaultAuthority(): String =
        _defaultAuthority

    @PreAuthorize("permitAll()")
    override fun getDefaultBaseUrlRedirect(): URI =
        defaultBaseUrlRedirectUri

    @PreAuthorize("permitAll()")
    override fun getDefaultPasswordFormRedirect(): URI? =
        defaultPasswordFormRedirectUri

    @PreAuthorize("permitAll()")
    override fun getDefaultRequestErrorRedirect(): URI? =
        defaultRequestErrorRedirectUri

    @IsDomainEditor
    @Transactional
    override fun create(domain: Domain): Domain {
        if (domain.authority == DEFAULT_DOMAIN || domain.isDefault) {
            throw DefaultDomainAlreadyExistsException(domain.authority)
        }
        if (repo.findByAuthority(domain.authority) != null) {
            throw DomainAlreadyExistsException(domain.authority)
        }
        return repo.save(domain)
    }

    @IsDomainEditor
    @Transactional
    override fun update(domain: Domain): Domain {
        val authority = resolveDefaultAuthority(domain.authority)
        val updateDomain = repo.findByAuthority(authority) ?: throw DomainDoesNotExistException(authority)
        updateDomain.scheme = domain.scheme
        updateDomain.baseUrlRedirect = domain.baseUrlRedirect
        updateDomain.requestErrorRedirect = domain.requestErrorRedirect
        updateDomain.passwordFormRedirect = domain.passwordFormRedirect
        return repo.saveAndFlush(updateDomain)
    }

    @IsDomainAdmin
    @Transactional
    override fun remove(authority: String) {
        val resolvedAuthority = resolveDefaultAuthority(authority)
        val removeDomain = repo.findByAuthority(resolvedAuthority) ?: throw DomainDoesNotExistException(resolvedAuthority)
        repo.delete(removeDomain)
    }

    @IsDomainViewer
    override fun list(pageable: Pageable?): Page<Domain> {
        return repo.findAll(pageable ?: Pageable.unpaged())
    }

    @IsDomainViewer
    override fun findByAuthority(authority: String?): Domain? {
        if (authority == null || authority == DEFAULT_DOMAIN) {
            return getDefault()
        }
        return repo.findByAuthority(authority)
    }

    @IsDomainViewer
    override fun getDefault(): Domain {
        try {
            return repo.findByIsDefaultTrue()
        } catch (_: EmptyResultDataAccessException) {
            throw DefaultDomainDoesNotExistsException()
        }
    }

    @IsDomainAdmin
    @Transactional
    override fun makeDefault(authority: String): Domain {
        val resolvedAuthority = resolveDefaultAuthority(authority)
        try {
            val currentDefault = repo.findByIsDefaultTrue()
            if (currentDefault.authority == resolvedAuthority) {
                return currentDefault
            }
        } catch (_: EmptyResultDataAccessException) {
            // proceed as normal if there is not a default domain yet
        }
        repo.findByAuthority(resolvedAuthority) ?: throw DomainDoesNotExistException(resolvedAuthority)
        repo.prepareToSetDefault()
        repo.setDefaultDomain(resolvedAuthority)
        _defaultAuthority = resolvedAuthority
        return getDefault()
    }

    @PreAuthorize("permitAll()")
    override fun resolveDefaultAuthority(authority: String?): String =
        if (authority == null || authority == DEFAULT_DOMAIN) {
            getDefaultAuthority()
        } else {
            authority
        }

}
