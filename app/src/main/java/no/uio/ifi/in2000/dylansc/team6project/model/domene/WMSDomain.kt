//Domain layer
//Logic: If one chooses a datatype within NORDIC or ARCTIC in a time interval
//over 60 hours, the app should then automatically change the map-type to EC (whole of WORLD)
//Cause: Map over NORDIC and ARCTIC have only data for up to 60 hours.

package no.uio.ifi.in2000.dylansc.team6project.model.domene

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

/**
 * Domain rules for picking which weather model area to use.
 *
 * NORDIC and ARCTIC only have data for the next 60 hours, so the app falls
 * back to WORLD (ECMWF) for longer forecasts.
 */
class WMSDomain {
    /**
     * Returns the area to actually use given the user's [originalArea] and how
     * many [hoursAhead] the forecast looks. Switches to [AreaData.WORLD] when
     * NORDIC/ARCTIC would run out of data.
     */
    fun resolveArea(originalArea: AreaData?, hoursAhead: Long): AreaData? {
        return if (
            (originalArea == AreaData.NORDIC || originalArea == AreaData.ARCTIC) && hoursAhead > 60
        ) {
            AreaData.WORLD
        } else {
            originalArea
        }
    }

    /**
     * Maps the Norwegian area name shown in the UI to an [AreaData] value.
     * Falls back to [originalArea] if the name is unknown.
     */
    fun changeArea(area: String, originalArea: AreaData): AreaData {
        return when (area) {
            "Norden" ->  AreaData.NORDIC
            "Arktis" ->  AreaData.ARCTIC
            "Verden" ->  AreaData.WORLD
            else ->  originalArea
        }


    }

}