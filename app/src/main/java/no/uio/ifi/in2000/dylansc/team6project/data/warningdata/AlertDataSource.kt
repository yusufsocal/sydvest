package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface AlertDataSource {
    suspend fun alertDataSource(): List<AlertFeature>?
}

class AlertDataSourceImpl : AlertDataSource {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun alertDataSource(): List<AlertFeature>? {
        return try {
            val url = "https://api.met.no/weatherapi/metalerts/2.0/current.json"
            val response: MetAlertsResponse = client.get(url).body()
            println("Suksess! Hentet ${response.features.size} varsler.")
            response.features
        } catch (e: Exception) {
            println("Feil ved henting eller parsing: ${e.message}")
            null
        }
    }
}