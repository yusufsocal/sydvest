package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer

class LocationRepository(
    private val wmsDataSource: WMSDataSource
) {
    private val cache = java.util.concurrent.ConcurrentHashMap<AreaData, List<WMSLayer>>()

    private val allowedTitles = setOf(
        "Air temperature 2m",
        "Precipitation amount 1h",
        "Precipitation amount 3h",
        "Wind 10m speed",
        "Wind 10m vector"
    )

    private val areaSuffixes = listOf(" in MEPS VDIV", " in Arctic VDIV", " in ECMWF SFC")

    private fun normalizeTitle(title: String): String =
        areaSuffixes.fold(title) { t, suffix -> t.removeSuffix(suffix) }.trim()

    suspend fun getArea(area: AreaData): List<WMSLayer>? {
        cache[area]?.let { return it }

        val layers = wmsDataSource.fetchWmsCapabilities(area)
            ?.capability?.rootLayer?.wmsListe
            ?.filter { normalizeTitle(it.title) in allowedTitles }
            ?: return null

        cache[area] = layers
        return layers
    }
}