//Domenelag
//Logikk: Dersom man velger en datatype innenfor norden eller arktis på et tidsintervall
//over 60 timer, skal appen automatisk endre karttype til EC (hele verden)
//Årsak: Kart over norden og arktis har kun data for opp til 60 timer.

package no.uio.ifi.in2000.dylansc.team6project.model.domene

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

class WMSDomain {
    fun resolveArea(originalArea: AreaData?, hoursAhead: Long): AreaData? {
        return if (
            (originalArea == AreaData.NORDEN || originalArea == AreaData.ARKTIS) && hoursAhead > 60
        ) {
            AreaData.VERDEN
        } else {
            originalArea
        }
    }

    // Funksjon for å endre område manuelt
    fun changeArea(area: String, originalArea: AreaData): AreaData {
        when (area) {
            "Norden" -> return AreaData.NORDEN
            "Arktis" -> return AreaData.ARKTIS
            "Verden" -> return AreaData.VERDEN
            else -> return originalArea
        }


    }

}