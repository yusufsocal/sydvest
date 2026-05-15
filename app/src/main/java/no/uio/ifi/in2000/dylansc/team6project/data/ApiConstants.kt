
package no.uio.ifi.in2000.dylansc.team6project.data

/**
 * // Collects all API base-URLs in one place for easy maintainability and change of environment in the app.
 * */
object ApiConstants {
    const val WMS_BASE_URL = "https://public-victoria.met.no/wms?"
    const val ALERTS_BASE_URL = "https://in2000.api.met.no/weatherapi/metalerts/2.0/current.json"
    const val SEARCH_BASE_URL = "https://photon.komoot.io/api/"

    const val LOCATIONFORECAST_BASE_URL = "https://in2000.api.met.no/weatherapi/locationforecast/2.0/compact"

    const val PHOTON_REVERSECOORDINATES_URL = "https://photon.komoot.io/reverse"
}
