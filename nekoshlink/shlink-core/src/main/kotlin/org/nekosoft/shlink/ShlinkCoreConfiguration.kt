package org.nekosoft.shlink

import org.nekosoft.shlink.dao.DomainDataAccess
import org.nekosoft.shlink.dao.ShortUrlDataAccess
import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.service.*
import org.nekosoft.shlink.service.impl.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.SearchStrategy
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@SpringBootApplication
class ShlinkCoreConfiguration {

    enum class ShortenerImpl { NATIVE, HASHIDS }

    // validates value of property
    @Value("\${nekoshlink.shortener:NATIVE}")
    private lateinit var shortenerImpl: ShortenerImpl

    @Bean
    @ConditionalOnMissingBean(ShortUrlManager::class)
    fun defaultShortUrlManager(
        dao: ShortUrlDataAccess,
        shortener: UrlShortener,
        shortCodeValidator: ShortCodeValidator,
        urlValidator: UrlValidator,
        domains: DomainDataAccess,
        visits: VisitDataAccess,
        tags: TagDataAccess,
        tracker: VisitTracker,
    ) = DefaultShortUrlManager(dao, shortener, shortCodeValidator, urlValidator, domains, visits, tags, tracker)

    @Bean(name = ["defaultUrlShortener"])
    @ConditionalOnMissingBean(UrlShortener::class)
    @ConditionalOnProperty("nekoshlink.shortener", havingValue = "NATIVE", matchIfMissing = true)
    fun default1UrlShortener() = DefaultUrlShortener()

    @Bean(name = ["defaultUrlShortener"])
    @ConditionalOnMissingBean(UrlShortener::class)
    @ConditionalOnProperty("nekoshlink.shortener", havingValue = "HASHIDS", matchIfMissing = false)
    fun default2UrlShortener() = HashidsUrlShortener()

    @Bean
    @ConditionalOnMissingBean(ShortCodeValidator::class)
    fun defaultShortCodeValidator() = DefaultShortCodeValidator()

    @Bean
    @ConditionalOnMissingBean(UrlValidator::class)
    fun defaultUrlValidator() = DefaultUrlValidator()

    @Bean
    @ConditionalOnMissingBean(VisitTracker::class)
    fun defaultVisitTracker(
        visits: VisitDataAccess,
    ) = DefaultVisitTracker(visits)

    @Bean
    @ConditionalOnMissingBean(VisitLocationResolver::class)
    fun defaultLocationResolver() = MaxMindGeoIPLocationResolver()

}
