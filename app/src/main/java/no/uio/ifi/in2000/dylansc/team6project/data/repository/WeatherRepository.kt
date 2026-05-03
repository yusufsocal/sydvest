package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.locationforecastdata.LocationforecastDataSource
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather

class WeatherRepository(
    private val weatherDataSource: LocationforecastDataSource
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather? {
        val response = weatherDataSource.getForecast(lat, lon) ?: return null
        val now = response.properties.timeseries.firstOrNull() ?: return null
        return CurrentWeather(
            temperature = now.data.instant.details.airTemperature,
            windSpeed = now.data.instant.details.windSpeed,
            rainfall = now.data.nextOneHours?.details?.precipitationAmount ?: 0.0
        )
    }
}
