package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.locationforecast.LocationforecastDataSource
import no.uio.ifi.in2000.dylansc.team6project.model.domain.CurrentWeather

/**
 * Repository for current weather.
 *
 * Maps the Locationforecast response into the app's [CurrentWeather] domain model.
 */
class WeatherRepository(
    private val weatherDataSource: LocationforecastDataSource
) {
    /**
     * Returns the current weather at [lat]/[lon], or `null` if the forecast
     * is unavailable. Rainfall defaults to 0.0 when the API omits it.
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather? {
        val response = weatherDataSource.getForecast(lat, lon) ?: return null
        val now = response.properties.timeseries.firstOrNull() ?: return null
        return CurrentWeather(
            latitude = lat,
            longitude = lon,
            temperature = now.data.instant.details.airTemperature,
            windSpeed = now.data.instant.details.windSpeed,
            rainfall = now.data.nextOneHours?.details?.precipitationAmount ?: 0.0
        )
    }
}
