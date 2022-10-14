package org.nekosoft.shlink.entity.support

import javax.persistence.Column
import javax.persistence.Embeddable

enum class LocationStatus {
    NO_CONFIG, NOT_FOUND, FOUND, ERROR
}

@Embeddable
data class VisitLocation(
    @Column(length = 64)
    var ipAddress: String? = null,
    @Column(length = 16)
    var countryCode: String? = null,
    @Column(length = 1024)
    var regionName: String? = null,
    @Column(length = 16)
    var postalCode: String? = null,
    @Column(length = 1024)
    var cityName: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    @Column(length = 32)
    var timezone: String? = null,
    var status: LocationStatus = LocationStatus.NO_CONFIG,
)
