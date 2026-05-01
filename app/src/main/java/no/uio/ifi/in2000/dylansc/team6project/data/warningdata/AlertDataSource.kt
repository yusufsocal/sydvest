package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants
import no.uio.ifi.in2000.dylansc.team6project.data.HttpClientProvider

interface AlertDataSource {
    suspend fun alertDataSource(): List<AlertFeature>?
}

class AlertDataSourceImpl : AlertDataSource {

    private val client = HttpClientProvider.jsonClient

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