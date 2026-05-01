//Domenelag
//Logikk: Dersom man velger en datatype innenfor NORDIC eller ARCTIC på et tidsintervall
//over 60 timer, skal appen automatisk endre karttype til EC (hele WORLD)
//Årsak: Kart over NORDIC og ARCTIC har kun data for opp til 60 timer.

package no.uio.ifi.in2000.dylansc.team6project.model.domene

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

class WMSDomain {
    fun resolveArea(originalArea: AreaData, hoursAhead: Long): AreaData {
        return if (
            (originalArea == AreaData.NORDIC || originalArea == AreaData.ARCTIC) && hoursAhead > 60
        ) {
            AreaData.WORLD
        } else {
            originalArea
        }
    }
}