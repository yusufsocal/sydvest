package no.uio.ifi.in2000.dylansc.team6project.data

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText // Henter XML som String
import nl.adaptivity.xmlutil.serialization.XML
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WmsCapabilities

class WMSDataSource {
    private val client = HttpClient(CIO)

    // Vi lager XML-parseren her i stedet for inne i Ktor
    private val xmlParser = XML {
        defaultPolicy {
            ignoreUnknownChildren()
        }
        repairNamespaces = true
    }

    suspend fun fetchWmsCapabilities(model: AreaData): WmsCapabilities? {
        val url = "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetCapabilities&model=${model.area}"

        return try {
            val response = client.get(url)
            val xmlString = response.bodyAsText() // Her får vi rå-XML-en

            // Vi parser teksten manuelt til objektet ditt
            xmlParser.decodeFromString(WmsCapabilities.serializer(), xmlString)
        } catch (e: Exception) {
            e.printStackTrace() // Nyttig for feilsøking i Logcat
            null
        }
    }
}