// Builds HttpClient-instances with standard configurations.
// Ownership (life cycle + close) lays with App, not here.

package no.uio.ifi.in2000.dylansc.team6project.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientProvider {
    fun createDefaultClient(): HttpClient = HttpClient(CIO)

    fun createJsonClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
}