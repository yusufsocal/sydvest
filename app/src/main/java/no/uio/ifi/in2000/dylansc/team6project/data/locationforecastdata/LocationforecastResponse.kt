package no.uio.ifi.in2000.dylansc.team6project.data.locationforecastdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LocationforecastResponse(
    val properties: LocationforecastProperties
)

@Serializable
data class LocationforecastProperties(
    val timeseries: List<LocationforecastTimeSeriesEntry>
)

@Serializable
data class LocationforecastTimeSeriesEntry(
    val time: String,
    val data: LocationforecastData
)

@Serializable
data class LocationforecastData(
    val instant: LocationforecastInstant,
    @SerialName("next_1_hours") val nextOneHours: LocationforecastNextHours? = null
)

@Serializable
data class LocationforecastInstant(
    val details: LocationforecastInstantDetails
)

@Serializable
data class LocationforecastInstantDetails(
    @SerialName("air_temperature") val airTemperature: Double,
    @SerialName("wind_speed") val windSpeed: Double
)

@Serializable
data class LocationforecastNextHours(
    val details: LocationforecastNextHoursDetails
)

@Serializable
data class LocationforecastNextHoursDetails(
    @SerialName("precipitation_amount") val precipitationAmount: Double
)
