//Gets JSON/GeoJSON from MeteoGate or MetAlerts.
package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource


class AlertRepository (
    private val warningDataSource: AlertDataSource
) {
    suspend fun getAlertList(): List<AlertFeature> {
        val list = warningDataSource.alertDataSource() ?: emptyList()

        // Sorts alphabetically by area
        return list.sortedBy { it.properties?.area ?: "Ååå" }
    }
}
