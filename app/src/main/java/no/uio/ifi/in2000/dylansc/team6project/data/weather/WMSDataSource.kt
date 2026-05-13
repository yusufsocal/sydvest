package no.uio.ifi.in2000.dylansc.team6project.data.weather

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import nl.adaptivity.xmlutil.serialization.XML
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants

/**
 * Data source for the MET WMS service.
 *
 * Fetches the GetCapabilities document that lists available map layers.
 */
interface WMSDataSource {
    /** Returns the WMS capabilities for [model], or `null` if [model] is null or the request fails. */
    suspend fun fetchWmsCapabilities(model: AreaData?): WMSCapabilities?
}

/**
 * Default [WMSDataSource] backed by a Ktor [HttpClient].
 *
 * The response is XML, so it is parsed with `xmlutil` instead of kotlinx.serialization JSON.
 */
class WMSDataSourceImpl(
    private val client: HttpClient
) : WMSDataSource {

    private val xmlParser = XML {
        defaultPolicy {
            ignoreUnknownChildren()
        }
        repairNamespaces = true
    }

    /**
     * Builds the GetCapabilities URL for [model], fetches the XML, and decodes
     * it into a [WMSCapabilities]. Returns `null` on error.
     */
    override suspend fun fetchWmsCapabilities(model: AreaData?): WMSCapabilities? {
        if (model == null) return null
        val url = "${ApiConstants.WMS_BASE_URL}service=WMS&version=1.3.0&request=GetCapabilities&model=${model.area}"

        return try {
            val response = client.get(url)
            val xmlString = response.bodyAsText() // Raw XML

            // Parse the text manually to the object
            xmlParser.decodeFromString(WMSCapabilities.serializer(), xmlString)
        } catch (e: Exception) {
            Log.e("WMSDataSource", "Error fetching WMS capabilities: ${e.message}")
            null
        }
    }
}