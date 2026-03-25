package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import com.google.android.gms.common.Feature
import kotlinx.serialization.Serializable
import java.util.Properties

@Serializable
data class MetAlertsResponse(
    val type: String,
    val feature: List<Feature> = emptyList()
)

@Serializable
data class Feature(
    val type: String,
    val geometry: Geometry? = null,
    val properties: Properties
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: Any
)

@Serializable
data class Properties(
    val id:String?,
    val area:String?,
    val event:String?,
    val severity:String?,
    val certainty:String?,
    val urgency:String?,
    val description:String?,
    val instruction:String?,
    val riskMatrixColor:String?

)
