package org.nekosoft.shlink.rest.controller

import mu.KotlinLogging
import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.service.exception.*
import org.nekosoft.utils.sec.delegation.RunAs
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.util.ResourceUtils
import java.net.URI
import java.time.LocalDateTime

private val kLogger = KotlinLogging.logger {}

data class ErrorPageData (
    val title: String,
    val header: String,
    val text: String,
    val subtext: String,
    val baseUrl: String,
    val status: HttpStatus,
)

@ControllerAdvice
class ShlinkExceptionHandler {

    @ExceptionHandler(NekoShlinkException::class)
    fun nekoshlinkException(ex: NekoShlinkException, request: WebRequest?): ResponseEntity<String> {
        return ResponseEntity.status(ex.status).body("{\"error\":\"${ex.javaClass.simpleName}\",\"message\":\"${ex.message}\",\"timestamp\":\"${LocalDateTime.now()}\"}")
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun accessDenied(ex: RuntimeException, request: WebRequest?): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\":\"Access\",\"timestamp\":\"${LocalDateTime.now()}\"}")
    }

    @ExceptionHandler(Exception::class)
    fun anyException(ex: Exception, request: WebRequest?): ResponseEntity<String> {
        kLogger.error { ex }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Internal\",\"timestamp\":\"${LocalDateTime.now()}\"}")
    }

    companion object {

        /*
        * I tried providing this functionality to the AbstractResolverController and BaseUrlController
        * via an AbstractHtmlController that had an @ExceptionHandler method to deal with all the
        * Exception instances for the HTML-producing controller, but the annotation meant that the base
        * class would be proxied and somehow in the proxying it would lose access to the Domains
        * dependency that is necessary to get the configuration of the domains.
        * TODO investigate why and whether there is a solution
        */

        fun handleResolutionIssue(ex: Exception, request: WebRequest, domains: DomainDataAccess): ResponseEntity<String> {
            val host = request.getHeader(HttpHeaders.HOST)
            val domain = RunAs.anonymousWithRoles("Domains:Viewer").use {
                host?.let {
                    domains.findByAuthority(it)
                }
            }
            return if (domain == null || ex is DomainDoesNotExistException) {
                kLogger.error("Handling request from invalid domain {}", host)
                serveInternalPage("oops.html", domains.getDefaultBaseUrlRedirect().toString(), HttpStatus.FORBIDDEN)
            } else {
                val baseUrl = if (domain.baseUrlRedirect != null) {
                    domain.baseUrlRedirect!!
                } else {
                    domains.getDefaultBaseUrlRedirect().toString()
                }
                if (domain.requestErrorRedirect != null) {
                    val uri = URI.create(domain.requestErrorRedirect!!)
                    ResponseEntity.status(HttpStatus.FOUND).location(uri).build()
                } else {
                    if (domains.getDefaultRequestErrorRedirect() != null) {
                        ResponseEntity.status(HttpStatus.FOUND).location(domains.getDefaultRequestErrorRedirect()!!).build()
                    } else {

                        if (ex is ProtectedShortUrlResolutionException) {
                            if (domain.passwordFormRedirect != null) {
                                val uri = URI.create(domain.passwordFormRedirect!!)
                                ResponseEntity.status(HttpStatus.FOUND).location(uri).build()
                            } else if (domains.getDefaultPasswordFormRedirect() != null) {
                                ResponseEntity.status(HttpStatus.FOUND).location(domains.getDefaultPasswordFormRedirect()!!)
                                    .build()
                            } else {
                                serveInternalPage(
                                    "passwordForm.html",
                                    baseUrl,
                                    HttpStatus.UNAUTHORIZED
                                )
                            }
                        } else {
                            val errorPageData = when (ex) {
                                is ShortUrlHasExpiredException ->
                                    ErrorPageData(
                                        "Expired",
                                        "Expired",
                                        "We are sorry, this Short URL has expired!",
                                        "This Short URL had to be used by a certain date that has passed...",
                                        baseUrl,
                                        ex.status,
                                    )

                                is ShortUrlNotEnabledYetException ->
                                    ErrorPageData(
                                        "Inactive",
                                        "Inactive",
                                        "We are sorry, this Short URL is not active yet!",
                                        "This Short URL has to be used after a certain date that has not passed yet...",
                                        baseUrl,
                                        ex.status,
                                    )

                                is MaxVisitLimitReachedException ->
                                    ErrorPageData(
                                        "Max Visits",
                                        "Visits",
                                        "We are sorry, this Short URL has been used too many times!",
                                        "This Short URL has a limit on the number of times it can be used, and the limit has been reached...",
                                        baseUrl,
                                        ex.status,
                                    )

                                is PathTrailNotAllowedException ->
                                    ErrorPageData(
                                        "No Path Trail",
                                        "Path",
                                        "We are sorry, path trails are not allowed for this URL!",
                                        "Check the short code that you were given and try again...",
                                        baseUrl,
                                        ex.status,
                                    )

                                is QueryParamsNotAllowedException ->
                                    ErrorPageData(
                                        "No Query Params",
                                        "Params",
                                        "We are sorry, query parameters are not allowed for this URL!",
                                        "Check the short code that you were given and try again...",
                                        baseUrl,
                                        ex.status,
                                    )

                                is ShortUrlNotFoundException, is MissingShortUrlException ->
                                    ErrorPageData(
                                        "Invalid Short Code",
                                        "Invalid",
                                        "We are sorry, this Short URL is not valid!",
                                        "Check the short code that you were given and try again...",
                                        baseUrl,
                                        HttpStatus.NOT_FOUND,
                                    )

                                is AccessDeniedException ->
                                    ErrorPageData(
                                        "Access Denied",
                                        "Denied",
                                        "We are sorry, this resource is not available to you!",
                                        "If you think you should have access to it, contact the administrator of this site...",
                                        baseUrl,
                                        HttpStatus.FORBIDDEN,
                                    )

                                else -> {
                                    kLogger.error("An internal server error has occurred", ex)
                                    ErrorPageData(
                                        "Internal Server Error",
                                        "500",
                                        "Something did not go quite as planned!",
                                        "You may want to wait a little and try again later...",
                                        baseUrl,
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                    )
                                }
                            }
                            serveInternalErrorPage(errorPageData)
                        }
                    }
                }
            }
        }

        private fun serveInternalErrorPage(data: ErrorPageData): ResponseEntity<String> {
            val htmlPage =
                ResourceUtils.getFile("classpath:pages/error.html")
                    .readText()
                    .replace("[#Title#]", data.title)
                    .replace("[#Header#]", data.header)
                    .replace("[#Text#]", data.text)
                    .replace("[#Subtext#]", data.subtext)
                    .replace("[#BaseUrl#]", data.baseUrl)
                    .replace("[#Timestamp#]", LocalDateTime.now().toString())
            return ResponseEntity.status(data.status).body(htmlPage)
        }

        private fun serveInternalPage(page: String, baseUrl: String, status: HttpStatus): ResponseEntity<String> {
            val htmlPage =
                ResourceUtils.getFile("classpath:pages/$page")
                    .readText()
                    .replace("[#BaseUrl#]", baseUrl)
                    .replace("[#Timestamp#]", LocalDateTime.now().toString())
            return ResponseEntity.status(status).body(htmlPage)
        }

    }

}