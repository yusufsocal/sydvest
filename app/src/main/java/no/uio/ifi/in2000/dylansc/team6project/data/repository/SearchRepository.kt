package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult

class SearchRepository(private val dataSource: SearchDataSource) {
    suspend fun getSuggestions(query: String): List<SearchResult> {
        if (query.isEmpty()) return emptyList()
        return dataSource.fetchSearchSuggestions(query)
    }
}