package org.nekosoft.shlink.rest.controller

import mu.KotlinLogging
import org.nekosoft.shlink.service.ShortUrlManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/*
 *  https://technicalseo.com/tools/robots-txt/
 */

private val kLogger = KotlinLogging.logger {}

@RestController
class RobotsController(private val shortUrls: ShortUrlManager) {

    @GetMapping("robots.txt", produces = ["text/plain"])
    fun listCrawlable(): ResponseEntity<String> {
        val prefix = """
        # For more information about the robots.txt standard, see:
        # https://www.robotstxt.org/orig.html

        User-agent: *

        """.trimIndent()

        val allowed = try {
            shortUrls.listCrawlableURLs().map { "Allow: /$it" }.joinToString("\n")
        } catch (e: Exception) {
            kLogger.warn("Error generating the robots.txt file", e)
            ""
        }

        val suffix = """
        
            Disallow: /
        """.trimIndent()

        return ResponseEntity.ok("$prefix\n$allowed\n$suffix\n")
    }

}
