//Håndterer brukernes posisjon og stedsnavnsøk
package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WmsLayer

class LocationRepository (
    private val wmsDataSource: WMSDataSource
) {
    suspend fun getArea(area: AreaData): List<WmsLayer>? {
        return wmsDataSource.fetchWmsCapabilities(area)?.capability?.rootLayer?.wmsListe
    }
}