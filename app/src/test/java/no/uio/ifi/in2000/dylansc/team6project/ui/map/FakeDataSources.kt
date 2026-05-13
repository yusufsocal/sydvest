package no.uio.ifi.in2000.dylansc.team6project.ui.map

import no.uio.ifi.in2000.dylansc.team6project.data.locationforecast.LocationforecastDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.search.SearchDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.search.SearchResult
import no.uio.ifi.in2000.dylansc.team6project.data.warning.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.warning.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weather.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSCapabilities
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSDataSource

class FakeAlertDataSource(
    private val result: List<AlertFeature>?
) : AlertDataSource {
    override suspend fun alertDataSource(): List<AlertFeature>? = result
}

class FakeWMSDataSource(
    private val result: WMSCapabilities?
) : WMSDataSource {
    override suspend fun fetchWmsCapabilities(model: AreaData?): WMSCapabilities? = result
}

class FakeSearchDataSource(
    private val result: List<SearchResult> = emptyList()
) : SearchDataSource {
    override suspend fun fetchSearchSuggestions(query: String): List<SearchResult> = result
    override suspend fun findplaceNameFromCoordinates(
        lat: Double,
        lon: Double
    ): String {
        TODO("Not yet implemented")
    }
}

class FakeLocationforecastDataSource : LocationforecastDataSource {
    override suspend fun getForecast(lat: Double, lon: Double) = null
}