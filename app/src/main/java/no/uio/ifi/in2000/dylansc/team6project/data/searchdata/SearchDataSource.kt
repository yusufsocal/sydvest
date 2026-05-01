package no.uio.ifi.in2000.dylansc.team6project.data.searchdata

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants
import no.uio.ifi.in2000.dylansc.team6project.data.HttpClientProvider

interface SearchDataSource {
    suspend fun fetchSearchSuggestions(query: String): List<SearchResult>
}

class SearchDataSourceImpl : SearchDataSource {
    private val client = HttpClientProvider.client
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchSearchSuggestions(query: String): List<SearchResult> {
        return try {
            val url = "${ApiConstants.SEARCH_BASE_URL}?q=${query}&limit=10&lang=en"
            val response = client.get(url).bodyAsText()
            val jsonElement = json.parseToJsonElement(response)
            val features = jsonElement.jsonObject["features"]?.jsonArray ?: return emptyList()

            features.map { feature ->
                val prop = feature.jsonObject["properties"]?.jsonObject
                val geom = feature.jsonObject["geometry"]?.jsonObject
                val coords = geom?.get("coordinates")?.jsonArray

                SearchResult(
                    name = listOfNotNull(
                        prop?.get("name")?.jsonPrimitive?.content,
                        prop?.get("city")?.jsonPrimitive?.content,
                        prop?.get("country")?.jsonPrimitive?.content
                    ).joinToString(", "),
                    lon = coords?.get(0)?.jsonPrimitive?.double ?: 0.0,
                    lat = coords?.get(1)?.jsonPrimitive?.double ?: 0.0
                )
            }.distinctBy { it.name }
        } catch (e: Exception) {
            Log.e("SearchDataSource", "Feil ved søk: ${e.message}")
            emptyList()
        }
    }
}