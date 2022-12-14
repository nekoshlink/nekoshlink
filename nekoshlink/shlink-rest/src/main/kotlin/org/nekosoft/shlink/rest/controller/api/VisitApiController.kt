package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.rest.ShlinkRestApiServer.Companion.VERSION_STRING
import org.nekosoft.utils.rest.pagination.PaginationData
import org.nekosoft.utils.rest.pagination.PaginationData.Companion.paginationToPageable
import org.nekosoft.utils.rest.pagination.RestResult
import org.nekosoft.shlink.vo.*
import org.nekosoft.utils.rest.pagination.PaginationOptions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// https://www.marcobehler.com/guides/spring-security

// Role-based access is defined in the core VisitDataAccess class

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v$VERSION_STRING/visits") // this short mapping guarantees it cannot be a short code (min five characters)
class VisitApiController(
    private val visits: VisitDataAccess,
) {

    @GetMapping
    fun visits(options: VisitListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<Visit>> {
        val results = visits.getVisits(options, paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

}
