package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Top-level GeoJSON response from the MET Alerts API.
 */
@Serializable
data class MetAlertsResponse(
    @SerialName("type") val type: String,
    @SerialName("features") val features: List<AlertFeature> = emptyList()
)

/**
 * One alert in the response, with its shape and metadata.
 */
@Serializable
data class AlertFeature(
    val geometry: AlertGeometry?,
    val properties: AlertProperties?
)

/**
 * Polygon geometry describing the area an alert covers.
 *
 * @property type Either "Polygon" or "MultiPolygon".
 * @property coordinates Raw GeoJSON coordinates, parsed lazily by the caller.
 */
@Serializable
data class AlertGeometry(
    val type: String,
    var coordinates: JsonElement
)

/**
 * Metadata for a single alert.
 *
 * @property area Name of the affected area (e.g. "Oslo").
 * @property description Detailed description of the alert.
 * @property event Type of event (e.g. "Wind", "Rain").
 * @property riskMatrixColor Color code for severity (yellow / orange / red).
 * @property severity Severity level (e.g. "Moderate", "Severe").
 * @property title Short headline.
 */
@Serializable
data class AlertProperties(
    val area: String?,
    val description: String?,
    val event: String?,
    val riskMatrixColor: String?,
    val severity: String?,
    val title: String?,
)
