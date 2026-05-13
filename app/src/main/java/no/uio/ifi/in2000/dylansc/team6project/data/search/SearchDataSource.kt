package no.uio.ifi.in2000.dylansc.team6project.data.search

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants

/**
 * Data source for the Photon search API.
 *
 * Provides place search and reverse geocoding.
 */
interface SearchDataSource {
    /** Returns up to 10 search suggestions for [query]. Empty list on failure. */
    suspend fun fetchSearchSuggestions(query: String): List<SearchResult>

    /** Returns the place name at the given coordinate, or `null` if none is found. */
    suspend fun findplaceNameFromCoordinates(lat: Double, lon: Double): String?
}

/**
 * Default [SearchDataSource] backed by a Ktor [HttpClient].
 *
 * Parses the Photon GeoJSON response manually and maps it into [SearchResult]s.
 */
class SearchDataSourceImpl(
    private val client: HttpClient
) : SearchDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Calls the Photon search endpoint and extracts name + coordinates from
     * each feature. Duplicates by display name are removed. Returns an empty
     * list on any error.
     */
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
                    lon = coords?.getOrNull(0)?.jsonPrimitive?.double ?: 0.0,
                    lat = coords?.getOrNull(1)?.jsonPrimitive?.double ?: 0.0
                )
            }.distinctBy { it.name }
        } catch (e: Exception) {
            Log.e("SearchDataSource", "Error fetching search suggestions: ${e.message}")
            emptyList()
        }
    }

    /**
     * Calls the Photon reverse-geocoding endpoint and joins the resulting
     * name/city/country into a single string. Returns `null` on error or
     * if no usable name is found.
     */
    override suspend fun findplaceNameFromCoordinates(lat: Double, lon: Double): String? {
        return try {
            val url = "${ApiConstants.PHOTON_REVERSECOORDINATES_URL}?lon=${lon}&lat=${lat}&lang=en"
            val response = client.get(url).bodyAsText()
            val jsonElement = json.parseToJsonElement(response)
            val features = jsonElement.jsonObject["features"]?.jsonArray ?: return null
            val first = features.firstOrNull() ?: return null
            val prop = first.jsonObject["properties"]?.jsonObject

            val name = listOfNotNull(
                prop?.get("name")?.jsonPrimitive?.content,
                prop?.get("city")?.jsonPrimitive?.content,
                prop?.get("country")?.jsonPrimitive?.content
            ).joinToString(", ")

            name.ifBlank { null }
        } catch (e: Exception) {
            Log.e("SearchDataSource", "Error during reverse geocoding: ${e.message}")
            null
        }
    }
}