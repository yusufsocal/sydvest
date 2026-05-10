package no.uio.ifi.in2000.dylansc.team6project.data.searchdata

/**
 * One place suggestion from the search API.
 *
 * @property name Display name (e.g. "Oslo, Norway").
 * @property lat Latitude in decimal degrees.
 * @property lon Longitude in decimal degrees.
 */
data class SearchResult (
    val name: String,
    val lat: Double,
    val lon: Double
)
