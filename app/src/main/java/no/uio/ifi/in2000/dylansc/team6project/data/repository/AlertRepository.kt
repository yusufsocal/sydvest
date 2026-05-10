//Gets JSON/GeoJSON from MeteoGate or MetAlerts.
package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature


/**
 * Repository for MET weather alerts.
 *
 * Fetches alerts from [AlertDataSource] and returns them sorted by area.
 */
class AlertRepository (
    private val warningDataSource: AlertDataSource
) {
    /**
     * Returns all current alerts sorted alphabetically by area.
     * Alerts without an area are placed last.
     */
    suspend fun getAlertList(): List<AlertFeature> {
        val list = warningDataSource.alertDataSource() ?: emptyList()

        // Sorts alphabetically by area
        return list.sortedBy { it.properties?.area ?: "Ååå" }
    }
}
