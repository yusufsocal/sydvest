// Deler en enkelt HttpClient-instans på tvers av hele appen
// client - brukes for enkle HTTP-kall (feks. WMS/XML)
// jsonClient - brukes der JSON-parsing er nødvendig (feks. varsler)


package no.uio.ifi.in2000.dylansc.team6project.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientProvider {
    val client = HttpClient(CIO)

    val jsonClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
}