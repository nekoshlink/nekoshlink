package org.nekosoft.shlink.service.impl

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.maxmind.geoip2.model.CityResponse
import com.maxmind.geoip2.record.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.nekosoft.shlink.entity.support.LocationStatus
import org.nekosoft.shlink.entity.support.VisitLocation
import org.springframework.mock.web.MockHttpServletRequest
import java.net.InetAddress

class MaxMindGeoIPLocationResolverTests {

    private val dbReader = mock<DatabaseReader>()
    private val validResponse = CityResponse(
        City(listOf("en", "it"), 25, 54321, mapOf("en" to "Turin", "it" to "Torino")),
        Continent(listOf("en", "it"), "EU", 123456, mapOf("en" to "Europe", "it" to "Europa")),
        Country(listOf("it", "en"),25, 543222, true, "IT", mapOf("en" to "Italy", "it" to "Italia")),
        Location(20, 128321, 45.0704900, 7.6868200, 807, 300, "Europe/Rome"),
        MaxMind(10000),
        Postal("10100", 25),
        Country(listOf("en", "it"),25, 543222, true, "IT", mapOf("en" to "Italy", "it" to "Italia")),
        RepresentedCountry(),
        arrayListOf(Subdivision(listOf("en", "it"), 25, 127451, "Piedmont", mapOf("en" to "Piedmont", "it" to "Piemonte"))),
        Traits(),
    )
    private val invalidResponse = CityResponse(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    @Test
    fun `if no database, resolve as NO_CONFIG`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "92.40.176.238"
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = null
        val result = resolver.extractLocationInformation(request)
        assertEquals(LocationStatus.NO_CONFIG, result.status)
    }

    @Test
    fun `if invalid value, resolve as ERROR`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "Not A Valid Address"
        whenever(dbReader.city(any())).thenReturn(validResponse)
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = dbReader
        val result = resolver.extractLocationInformation(request)
        assertEquals(LocationStatus.ERROR, result.status)
    }

   @Test
    fun `if invalid response, resolve as ERROR`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "92.40.176.238"
        whenever(dbReader.city(any())).thenReturn(invalidResponse)
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = dbReader
        val result = resolver.extractLocationInformation(request)
        assertEquals(LocationStatus.ERROR, result.status)
    }

    @Test
    fun `if valid value, resolve as FOUND`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "92.40.176.238"
        whenever(dbReader.city(any())).thenReturn(validResponse)
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = dbReader
        val result = resolver.extractLocationInformation(request)
        assertEquals(VisitLocation("92.40.176.238","IT","Piedmont", "10100", "Turin", 45.0704900, 7.6868200, "Europe/Rome", LocationStatus.FOUND), result)
    }

    @Test
    fun `if address not found, resolve as NOT_FOUND`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "127.0.0.1"
        whenever(dbReader.city(any())).thenThrow(AddressNotFoundException("127.0.0.1"))
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = dbReader
        val result = resolver.extractLocationInformation(request)
        assertEquals(LocationStatus.NOT_FOUND, result.status)
    }

    @Test
    fun `if extra headers, resolve them first as FOUND`() {
        val request = MockHttpServletRequest()
        request.remoteAddr = "127.0.0.1"
        request.addHeader("X-Forwarded-For", "92.40.176.238")
        whenever(dbReader.city(any())).thenReturn(validResponse)
        val resolver = MaxMindGeoIPLocationResolver()
        resolver.dbReader = dbReader
        val result = resolver.extractLocationInformation(request)
        verify(dbReader).city(InetAddress.getByName("92.40.176.238"))
        assertEquals(VisitLocation("92.40.176.238", "IT","Piedmont", "10100", "Turin", 45.0704900, 7.6868200, "Europe/Rome", LocationStatus.FOUND), result)
    }

}