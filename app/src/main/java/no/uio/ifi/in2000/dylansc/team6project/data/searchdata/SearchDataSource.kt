package no.uio.ifi.in2000.dylansc.team6project.data.searchdata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SearchDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchSearchSuggestions(query: String): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://photon.komoot.io/api/?q=${query}&limit=5&lang=en"
                val response = java.net.URL(url).readText()
                val jsonElement = json.parseToJsonElement(response)
                val features = jsonElement.jsonObject["features"]?.jsonArray ?: return@withContext emptyList()

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
                emptyList()
            }
        }
    }
}