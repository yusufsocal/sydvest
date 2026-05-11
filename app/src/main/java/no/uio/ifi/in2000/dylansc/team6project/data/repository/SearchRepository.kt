package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.search.SearchDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.search.SearchResult

/**
 * Repository for place search and reverse geocoding.
 */
class SearchRepository(private val dataSource: SearchDataSource) {
    /** Returns search suggestions for [query], or an empty list if [query] is blank. */
    suspend fun getSuggestions(query: String): List<SearchResult> {
        if (query.isEmpty()) return emptyList()
        return dataSource.fetchSearchSuggestions(query)
    }

    /** Returns the place name at the given coordinate, or `null` if none is found. */
    suspend fun reverseGeocode(lat: Double, lon: Double): String? {
        return dataSource.findplaceNameFromCoordinates(lat, lon)
    }
}