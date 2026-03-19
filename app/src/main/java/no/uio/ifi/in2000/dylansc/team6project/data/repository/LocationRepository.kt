//Håndterer brukernes posisjon og stedsnavnsøk
package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.WMSDataSource

class LocationRepository (
    private val wmsDataSource: WMSDataSource
)