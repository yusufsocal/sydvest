//Domain layer
//Logic: If one chooses a datatype within NORDIC or ARCTIC in a time interval
//over 60 hours, the app should then automatically change the map-type to EC (whole of WORLD)
//Cause: Map over NORDIC and ARCTIC have only data for up to 60 hours.

package no.uio.ifi.in2000.dylansc.team6project.model.domene

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

class WMSDomain {
    fun resolveArea(originalArea: AreaData?, hoursAhead: Long): AreaData? {
        return if (
            (originalArea == AreaData.NORDIC || originalArea == AreaData.ARCTIC) && hoursAhead > 60
        ) {
            AreaData.WORLD
        } else {
            originalArea
        }
    }

    // Function for changing the area manually.
    fun changeArea(area: String, originalArea: AreaData): AreaData {
        return when (area) {
            "Norden" ->  AreaData.NORDIC
            "Arktis" ->  AreaData.ARCTIC
            "Verden" ->  AreaData.WORLD
            else ->  originalArea
        }


    }

}