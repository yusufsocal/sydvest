package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import kotlinx.serialization.Serializable

@Serializable
data class WmsLayer(
    val Name: String, //datanavn på datatype
    val Title: String, //lesbart navn på datatype
    val Dimension: String, //Tidsintervall
    val Style: String, //Stil på formatering av data
)