package no.uio.ifi.in2000.dylansc.team6project.data.locationforecastdata

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants

interface LocationforecastDataSource {
    suspend fun getForecast(lat: Double, lon: Double): LocationforecastResponse?
}

class LocationforecastDataSourceImpl(
    private val client: HttpClient
) : LocationforecastDataSource {

    override suspend fun getForecast(lat: Double, lon: Double): LocationforecastResponse? {
        return try {
            val response: LocationforecastResponse = client.get(ApiConstants.LOCATIONFORECAST_BASE_URL) {
                parameter("lat", lat)
                parameter("lon", lon)
                header("User-Agent", "team6-IN2000 github.com/IN2000-V26/team-6")
            }.body()
            Log.d(
                "LocationforecastDataSource",
                "Suksess! Hentet ${response.properties.timeseries.size} tidspunkter."
            )
            response
        } catch (e: Exception) {
            Log.e("LocationforecastDataSource", "Feil ved henting eller parsing: ${e.message}")
            null
        }
    }
}