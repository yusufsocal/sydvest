//Henter JSON/GeoJSON fra MeteoGate eller MetAlerts.
package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource

class AlertRepository (
    private val warningDataSource: AlertDataSource
) {
    suspend fun getAlertList(): List<AlertFeature>? {
        val list = warningDataSource.alertDataSource() ?: emptyList()

        // Sorterer alfabetisk etter område (Area)
        return list.sortedBy { it.properties?.area ?: "Ååå" }
    }
}
