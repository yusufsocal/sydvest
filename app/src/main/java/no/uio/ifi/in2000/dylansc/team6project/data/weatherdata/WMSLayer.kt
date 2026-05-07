package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue




//@SerialName makes sure that one can get data from XML-file with captial letter, but also
//makes sure that the data initialises to a variable with lowercase initial letter, so it follows correct coding practices.

@Serializable
@XmlSerialName("WMS_Capabilities", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSCapabilities(
    @SerialName("version")
    val version: String = "",
    @XmlSerialName("Capability", namespace = "http://www.opengis.net/wms", prefix = "")
    val capability: Capability
)

@Serializable
@XmlSerialName("Capability", namespace = "http://www.opengis.net/wms", prefix = "")
data class Capability(
    @XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
    val rootLayer: ParentLayer // Dette er "Victoria WMS" root-laget
)

@Serializable
@XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
data class ParentLayer(
    @XmlElement(true) @SerialName("Title") val title: String = "",
    @XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
    val wmsListe: List<WMSLayer> = emptyList()
)

@Serializable
@XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSLayer(
    @XmlElement(true) @SerialName("Name") val name: String = "",
    @XmlElement(true) @SerialName("Title") val title: String = "",
    val dimensions: List<WMSDimension> = emptyList()
) {
    // Choose "time"-dimension, not "reference_time" — or else we will getPT12H instead of
    // PT3H for ECMWF/"Verden"/The world, and the slider/animation jumps the wrong interval.
    val dimension: String?
        get() = dimensions.firstOrNull { it.name == "time" }?.value
            ?: dimensions.firstOrNull()?.value
}

@Serializable
@XmlSerialName("Dimension", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSDimension(
    @SerialName("name") val name: String = "",
    @XmlValue(true) val value: String = ""
)