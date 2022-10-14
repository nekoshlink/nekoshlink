package org.nekosoft.shlink.service.impl

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import mu.KotlinLogging
import org.nekosoft.shlink.entity.support.LocationStatus.*
import org.nekosoft.shlink.entity.support.VisitLocation
import org.nekosoft.shlink.service.VisitLocationResolver
import org.springframework.beans.factory.annotation.Value
import java.io.File
import java.net.InetAddress
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

private val kLogger = KotlinLogging.logger {}

class MaxMindGeoIPLocationResolver : VisitLocationResolver {

    @Value("\${nekoshlink.visitlocation.maxmind.dbpath:}")
    private lateinit var dbPath: String

    @Value("\${nekoshlink.visitlocation.maxmind.key:}")
    private lateinit var licenseKey: String

    // Allow testing and use outside a Spring context
    var dbReader: DatabaseReader? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("Cannot change an existing DB Reader instance")
            else
                field = value
        }

    private val ipHeaders = arrayOf(
        "X-Forwarded-For",
        "X-Real-IP",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
    )

    @PostConstruct
    fun initDb() {
        if (dbPath.isNotBlank() && licenseKey.isNotBlank()) {
            kLogger.trace("Looking for MaxMind GeoIP configuration at {}", dbPath)
            val database = File(dbPath)
            dbReader = DatabaseReader.Builder(database).withCache(CHMCache()).build()
            kLogger.debug("Found MaxMind GeoIP configuration with database at {} and license key", dbPath)
        }
    }

    private fun getRequestIP(request: HttpServletRequest): String? {
        val header = ipHeaders
            .firstOrNull { h -> request.getHeader(h).let {
                kLogger.trace("MaxMind GeoIP looking at header {} with value {}", h, it)
                it != null && it.isNotBlank()
            } }
        val value = request.getHeader(header)
            ?: request.remoteAddr
            ?: request.remoteHost
        kLogger.trace("Trying to determine IP Address location with MaxMind GeoIP for {}", value)
        return if (value.isNullOrBlank())
            null
        else
            value.split("\\s*,\\s*".toRegex(), 1)[0]
    }

    override fun extractLocationInformation(request: HttpServletRequest): VisitLocation {
        return if (dbReader == null) {
            kLogger.trace("No MaxMind GeoIP configuration available")
            VisitLocation(status = NO_CONFIG)
        } else {
            val ipName = getRequestIP(request)
            val ipAddress = try {
                InetAddress.getByName(ipName)
            } catch (e: Exception) {
                kLogger.trace("Could not resolve hostname {}", ipName, e)
                return VisitLocation(status = ERROR)
            }
            val response = try {
                dbReader?.city(ipAddress)
            } catch (e: AddressNotFoundException) {
                kLogger.trace("No location found for {}", ipAddress, e)
                return VisitLocation(status = NOT_FOUND)
            }
            kLogger.trace("MaxMind GeoIP for {} returned {}", ipAddress, response)
            return try {
                VisitLocation(
                    status = FOUND,
                    ipAddress = ipName,
                    countryCode = response?.country?.isoCode ?: throw IllegalArgumentException("Invalid response object - no country information"),
                    regionName = if (response.subdivisions != null && response.subdivisions.size > 0) { response.subdivisions[0].name } else { null },
                    postalCode = response.postal?.code,
                    cityName = response.city?.name,
                    latitude = response.location?.latitude,
                    longitude = response.location?.longitude,
                    timezone = response.location?.timeZone,
                )
            } catch (e: Exception) {
                kLogger.trace("Invalid response {} for {}", response, ipAddress, e)
                VisitLocation(status = ERROR)
            }
        }
    }

}
