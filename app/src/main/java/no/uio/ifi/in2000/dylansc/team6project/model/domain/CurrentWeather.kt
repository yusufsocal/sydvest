package no.uio.ifi.in2000.dylansc.team6project.model.domain

/**
 * Current weather at a single point.
 *
 * @property latitude Latitude in decimal degrees.
 * @property longitude Longitude in decimal degrees.
 * @property temperature Air temperature in degrees Celsius.
 * @property windSpeed Wind speed in metres per second.
 * @property rainfall Expected precipitation for the next hour, in millimetres.
 */
data class CurrentWeather (
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val windSpeed: Double,
    val rainfall: Double
)
