package no.uio.ifi.in2000.dylansc.team6project.data.locationforecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants

/**
 * Data source for the MET Locationforecast API.
 *
 * Fetches the weather forecast for a given coordinate.
 */
interface LocationforecastDataSource {
    /**
     * Returns the forecast for the given [lat]/[lon], or `null` if the request fails.
     */
    suspend fun getForecast(lat: Double, lon: Double): LocationforecastResponse?
}

/**
 * Default [LocationforecastDataSource] backed by a Ktor [HttpClient].
 *
 * Performs the HTTP GET against [ApiConstants.LOCATIONFORECAST_BASE_URL] and
 * deserializes the JSON response into a [LocationforecastResponse].
 */
class LocationforecastDataSourceImpl(
    private val client: HttpClient
) : LocationforecastDataSource {

    /**
     * Calls the Locationforecast endpoint with `lat` and `lon` query parameters
     * and the required `User-Agent` header. Catches and logs any exception,
     * returning `null` on failure so callers can handle it gracefully.
     */
    override suspend fun getForecast(lat: Double, lon: Double): LocationforecastResponse? {
        return try {
            val response: LocationforecastResponse = client.get(ApiConstants.LOCATIONFORECAST_BASE_URL) {
                parameter("lat", lat)
                parameter("lon", lon)
                header("User-Agent", "team6-IN2000 github.com/IN2000-V26/team-6")
            }.body()
            Log.d(
                "LocationforecastDataSource",
                "Success! Fetched ${response.properties.timeseries.size} times."
            )
            response
        } catch (e: Exception) {
            Log.e("LocationforecastDataSource", "Error with fetching or parsing: ${e.message}")
            null
        }
    }
}