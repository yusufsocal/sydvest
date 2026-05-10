package no.uio.ifi.in2000.dylansc.team6project.data.locationforecastdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Top-level response from the MET Locationforecast API.
 *
 * Only the fields the app actually uses are modeled; everything else in
 * the JSON payload is ignored during deserialization.
 */
@Serializable
data class LocationforecastResponse(
    val properties: LocationforecastProperties
)

/**
 * Wrapper around the list of forecast time steps.
 */
@Serializable
data class LocationforecastProperties(
    val timeseries: List<LocationforecastTimeSeriesEntry>
)

/**
 * A single forecast entry for a specific point in time.
 *
 * @property time ISO 8601 timestamp for this entry.
 * @property data Weather values for that timestamp.
 */
@Serializable
data class LocationforecastTimeSeriesEntry(
    val time: String,
    val data: LocationforecastData
)

/**
 * Weather data for one time step.
 *
 * @property instant Values measured at the timestamp itself (e.g. temperature, wind).
 * @property nextOneHours Forecast for the hour following the timestamp,
 *                        such as expected precipitation. May be `null` for
 *                        entries far enough into the future.
 */
@Serializable
data class LocationforecastData(
    val instant: LocationforecastInstant,
    @SerialName("next_1_hours") val nextOneHours: LocationforecastNextHours? = null
)

/** Container for instantaneous weather details. */
@Serializable
data class LocationforecastInstant(
    val details: LocationforecastInstantDetails
)

/**
 * Instantaneous weather values.
 *
 * @property airTemperature Air temperature in degrees Celsius.
 * @property windSpeed Wind speed in metres per second.
 */

@Serializable
data class LocationforecastInstantDetails(
    @SerialName("air_temperature") val airTemperature: Double,
    @SerialName("wind_speed") val windSpeed: Double
)

/** Container for forecast values covering the next hour. */
@Serializable
data class LocationforecastNextHours(
    val details: LocationforecastNextHoursDetails
)

/**
 * Forecast values for the next hour.
 *
 * @property precipitationAmount Expected precipitation in millimetres.
 */
@Serializable
data class LocationforecastNextHoursDetails(
    @SerialName("precipitation_amount") val precipitationAmount: Double
)
