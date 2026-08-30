package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.weather.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSLayer

/**
 * Repository for WMS map layers.
 *
 * Fetches the available layers for a given area, keeps only the ones the
 * app shows on the map, and caches the result per area.
 */
class LocationRepository(
    private val wmsDataSource: WMSDataSource
) {
    private val cache = java.util.concurrent.ConcurrentHashMap<AreaData, List<WMSLayer>>()

    /** Layer titles (normalized) that the app actually displays. */
    private val allowedTitles = setOf(
        "Air temperature 2m",
        "Precipitation amount 1h",
        "Precipitation amount 3h",
        "Wind 10m speed",
        "Wind 10m vector"
    )

    /**
     * Removes the trailing " in <model name>" (e.g. "in MEPS", "in AROME Arctic",
     * "in ECMWF SFC") so titles can be matched against [allowedTitles] regardless
     * of how MET names the model — the exact wording has changed before
     * (e.g. "in MEPS VDIV" became "in MEPS").
     */
    private fun normalizeTitle(title: String): String =
        title.substringBefore(" in ").trim()

    /**
     * Returns the filtered WMS layers for [area], or `null` if the request fails.
     * Results are cached so repeated lookups don't hit the network.
     */
    suspend fun getArea(area: AreaData?): List<WMSLayer>? {
        cache[area]?.let { return it }

        val layers = wmsDataSource.fetchWmsCapabilities(area)
            ?.capability?.rootLayer?.wmsList
            ?.filter { normalizeTitle(it.title) in allowedTitles }
            ?: return null

        cache[area as AreaData] = layers
        return layers
    }
}