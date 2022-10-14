package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.service.exception.DomainDoesNotExistException
import org.nekosoft.utils.sec.delegation.annotation.RunAs
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.context.request.WebRequest
import java.net.URI
import javax.servlet.http.HttpServletRequest

@Controller
class BaseUrlController(
    private val domains: DomainDataAccess,
) {

    @GetMapping("/")
    @RunAs("Domains:Viewer", allowAnonymous = true)
    fun baseUrlRedirect(request: HttpServletRequest): ResponseEntity<String> {
        val authority = request.getHeader(HttpHeaders.HOST)
        val domain = domains.findByAuthority(authority) ?: throw DomainDoesNotExistException(authority)
        return if (domain.baseUrlRedirect != null) {
            val uri = URI.create(domain.baseUrlRedirect!!)
            ResponseEntity.status(HttpStatus.FOUND).location(uri).build()
        } else {
            ResponseEntity.status(HttpStatus.FOUND).location(domains.getDefaultBaseUrlRedirect()).build()
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, r: WebRequest): ResponseEntity<String> {
        return ShlinkExceptionHandler.handleResolutionIssue(e, r, domains)
    }

}