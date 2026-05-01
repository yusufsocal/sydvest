package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import nl.adaptivity.xmlutil.serialization.XML
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants
import no.uio.ifi.in2000.dylansc.team6project.data.HttpClientProvider

interface WMSDataSource {
    suspend fun fetchWmsCapabilities(model: AreaData?): WMSCapabilities?
}

class WMSDataSourceImpl: WMSDataSource {
    private val client = HttpClientProvider.client

    private val xmlParser = XML {
        defaultPolicy {
            ignoreUnknownChildren()
        }
        repairNamespaces = true
    }

    override suspend fun fetchWmsCapabilities(model: AreaData): WMSCapabilities? {
        val url = "${ApiConstants.WMS_BASE_URL}service=WMS&version=1.3.0&request=GetCapabilities&model=${model.area}"

        return try {
            val response = client.get(url)
            val xmlString = response.bodyAsText() // Rå XML

            // Parser teksten manuelt til objektet
            xmlParser.decodeFromString(WMSCapabilities.serializer(), xmlString)
        } catch (e: Exception) {
            Log.e("WMSDataSource", "Feil ved henting av WMS capabilities: ${e.message}")
            null
        }
    }
}