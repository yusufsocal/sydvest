//Håndterer URL-bygging for Victoria WMS

package no.uio.ifi.in2000.dylansc.team6project.data;

import kotlinx.serialization.*
import kotlinx.serialization.xml.*
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WmsLayer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

class WMSDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun wmsLayer(model: AreaData): WmsLayer {
        val url = "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetCapabilities&format=image%2Fpng&transparent=true&model=${model.area}"
        val data:String = client.get(url).body()

    }
}

