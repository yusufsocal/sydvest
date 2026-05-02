package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants

interface AlertDataSource {
    suspend fun alertDataSource(): List<AlertFeature>?
}

class AlertDataSourceImpl(
    private val client: HttpClient
) : AlertDataSource {

    override suspend fun alertDataSource(): List<AlertFeature>? {
        return try {
            val url = ApiConstants.ALERTS_BASE_URL
            val response: MetAlertsResponse = client.get(url).body()
            Log.d("AlertDataSource", "Suksess! Hentet ${response.features.size} varsler.")
            response.features
        } catch (e: Exception) {
            Log.e("AlertDataSource", "Feil ved henting eller parsing: ${e.message}")
            null
        }
    }
}