// Samler alle API base-URL-er på ett sted for enkel vedlikehold og miljøbytte i appen

package no.uio.ifi.in2000.dylansc.team6project.data


object ApiConstants {
    const val WMS_BASE_URL = "https://public-victoria.met.no/wms?"
    const val ALERTS_BASE_URL = "https://api.met.no/weatherapi/metalerts/2.0/current.json"
    const val SEARCH_BASE_URL = "https://photon.komoot.io/api/"

    const val LOCATIONFORECAST_BASE_URL = "https://api.met.no/weatherapi/locationforecast/2.0/compact"

    const val PHOTON_REVERSECOORDINATES_URL = "https://photon.komoot.io/reverse"
}
