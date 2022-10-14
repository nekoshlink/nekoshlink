package org.nekosoft.shlink.rest.controller

import mu.KotlinLogging
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.service.VisitLocationResolver
import org.nekosoft.shlink.vo.ResolveMeta
import org.nekosoft.utils.CrawlerDetect
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import javax.servlet.http.HttpServletRequest

private val kLogger = KotlinLogging.logger {}

abstract class AbstractResolveController<R>(
    private val locator: VisitLocationResolver,
    private val crawlerDetector: CrawlerDetect,
) {

    abstract fun getSource(): VisitSource
    abstract fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<R>

    @GetMapping
    fun resolveShortUrl(@PathVariable("shortCode") shortCode: String, @RequestHeader headers: Map<String, String>, request: HttpServletRequest): ResponseEntity<R> {
        return commonResolver(shortCode, null, request, headers)
    }

    @GetMapping("{*pathTrail}")
    fun resolveShortUrl(@PathVariable("shortCode") shortCode: String, @PathVariable("pathTrail") pathTrail: String, @RequestHeader headers: Map<String, String>, request: HttpServletRequest): ResponseEntity<R> {
        return commonResolver(shortCode, pathTrail, request, headers)
    }

    protected open fun commonResolver(shortCode: String, pathTrail: String?, request: HttpServletRequest, headers: Map<String, String>): ResponseEntity<R> {

        val meta = ResolveMeta(
            shortCode = shortCode,
            domain = request.getHeader(HttpHeaders.HOST),
            password = request.getParameter(SHLINK_PASSWORD_PARAMETER),
            pathTrail = pathTrail,
            queryParams = request.parameterMap.filter { it.key != SHLINK_PASSWORD_PARAMETER },
        )

        val enricher: VisitDataEnricher = {
            Visit(
                source = getSource(),
                referrer = request.getHeader(HttpHeaders.REFERER),
                remoteAddr = request.remoteAddr,
                pathTrail = meta.pathTrail,
                queryString = request.queryString,
                userAgent = request.getHeader(HttpHeaders.USER_AGENT),
                visitLocation = locator.extractLocationInformation(request),
                potentialBot = try { crawlerDetector.isCrawler(headers) } catch (e: IllegalStateException) { null }
            )
        }

        return getResponse(meta, enricher, request)

    }

    companion object {
        const val SHLINK_PASSWORD_PARAMETER = "__nekoshlink_password"
    }

}
