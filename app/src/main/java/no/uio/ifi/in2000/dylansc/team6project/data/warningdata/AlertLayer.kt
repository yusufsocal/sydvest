package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

@Serializable
data class MetAlertsResponse(
    @SerialName("type") val type: String,
    @SerialName("features") val features: List<AlertFeature> = emptyList()
)

@Serializable
data class AlertFeature(
    val geometry: AlertGeometry?,
    val properties: AlertProperties?
)

@Serializable
data class AlertGeometry(
    val type: String, // Enten "Polygon" eller "MultiPolygon"
    var coordinates: JsonElement // GeoJSON-format for polygoner
)

@Serializable
data class AlertProperties(
    val area: String?,
    val description: String?,
    val event: String?,
    val riskMatrixColor: String?,
    val severity: String?,
    val title: String?,
)
