package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.rest.ShlinkRestApiServer.Companion.VERSION_STRING
import org.nekosoft.shlink.vo.*
import org.nekosoft.utils.rest.pagination.PaginationData
import org.nekosoft.utils.rest.pagination.PaginationData.Companion.paginationToPageable
import org.nekosoft.utils.rest.pagination.PaginationOptions
import org.nekosoft.utils.rest.pagination.RestResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// https://www.marcobehler.com/guides/spring-security

// Role-based access is defined in the core VisitDataAccess class

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v$VERSION_STRING/domains") // this short mapping guarantees it cannot be a short code (min five characters)
class DomainApiController(
    private val domains: DomainDataAccess,
) {

    @GetMapping
    fun domains(options: DomainListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<Domain>> {
        val results = domains.list(paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

    @PostMapping
    fun createDomain(@RequestBody meta: DomainCreateMeta): ResponseEntity<Domain> {
        val domain = domains.create(Domain(
            authority = meta.authority,
            scheme = meta.scheme,
            baseUrlRedirect = meta.baseUrlRedirect,
            requestErrorRedirect = meta.requestErrorRedirect,
            passwordFormRedirect = meta.passwordFormRedirect,
            isDefault = false,
        ))
        return ResponseEntity.status(HttpStatus.OK).body(domain)
    }

    @PatchMapping("default")
    fun makeDefault(@RequestBody meta: DomainDefaultMeta): ResponseEntity<Void> {
        domains.makeDefault(meta.authority)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PutMapping("{authority}")
    fun editDomain(@PathVariable("authority") authority: String, @RequestBody meta: DomainEditMeta): ResponseEntity<Void> {
        val domain = Domain(
            authority = authority,
            scheme = meta.scheme,
            baseUrlRedirect = meta.baseUrlRedirect,
            requestErrorRedirect = meta.requestErrorRedirect,
            passwordFormRedirect = meta.passwordFormRedirect,
        )
        domains.update(domain)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @DeleteMapping("{authority}")
    fun editDomain(@PathVariable("authority") authority: String): ResponseEntity<Void> {
        domains.remove(authority)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

}
