package org.nekosoft.shlink.rest.controller

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.entity.ShortUrl.Companion.URL_SEGMENT_REGEX
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.ShortUrlManager
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.service.VisitLocationResolver
import org.nekosoft.shlink.service.exception.ShortUrlNotFoundException
import org.nekosoft.shlink.vo.ResolveMeta
import org.nekosoft.utils.CrawlerDetect
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("{shortCode:$URL_SEGMENT_REGEX}")
class RedirectController(
    private val shortUrls: ShortUrlManager,
    private val domains: DomainDataAccess,
    locator: VisitLocationResolver,
    crawlerDetector: CrawlerDetect,
) : AbstractResolveController<String>(locator, crawlerDetector) {

    override fun getSource(): VisitSource = VisitSource.REST

    override fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<String> {
        val (shortUrl, uriComponents) = shortUrls.resolve(meta, enricher)
        return if (shortUrl == null || uriComponents == null) {
            throw ShortUrlNotFoundException(meta.shortCode!!, meta.domain)
        } else {
            ResponseEntity.status(HttpStatus.FOUND).location(uriComponents.toUri()).build()
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, r: WebRequest): ResponseEntity<String> {
        return ShlinkExceptionHandler.handleResolutionIssue(e, r, domains)
    }

}
