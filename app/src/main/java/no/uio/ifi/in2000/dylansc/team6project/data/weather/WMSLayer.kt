package no.uio.ifi.in2000.dylansc.team6project.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue




//@SerialName makes sure that one can get data from XML-file with captial letter, but also
//makes sure that the data initialises to a variable with lowercase initial letter, so it follows correct coding practices.

/** Root of the WMS GetCapabilities XML response. */
@Serializable
@XmlSerialName("WMS_Capabilities", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSCapabilities(
    @SerialName("version")
    val version: String = "",
    @XmlSerialName("Capability", namespace = "http://www.opengis.net/wms", prefix = "")
    val capability: Capability
)

/** Wrapper for the root layer in the capabilities document. */
@Serializable
@XmlSerialName("Capability", namespace = "http://www.opengis.net/wms", prefix = "")
data class Capability(
    @XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
    val rootLayer: ParentLayer // "Victoria WMS" root layer
)

/**
 * Top-level layer that contains all the actual map layers as children.
 *
 * @property wmsListe Child layers available for display.
 */
@Serializable
@XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
data class ParentLayer(
    @XmlElement(true) @SerialName("Title") val title: String = "",
    @XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
    val wmsListe: List<WMSLayer> = emptyList()
)

/**
 * One displayable WMS layer (e.g. "Air temperature 2m").
 *
 * @property name Layer identifier used in tile URLs.
 * @property title Human-readable layer name.
 * @property dimensions Available dimensions (time, reference_time, …).
 */
@Serializable
@XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSLayer(
    @XmlElement(true) @SerialName("Name") val name: String = "",
    @XmlElement(true) @SerialName("Title") val title: String = "",
    val dimensions: List<WMSDimension> = emptyList()
) {
    /**
     * Time-axis values for the layer, preferring the "time" dimension over
     * "reference_time" so the time slider uses the correct step (PT3H vs PT12H
     * for the world model).
     */
    val dimension: String?
        get() = dimensions.firstOrNull { it.name == "time" }?.value
            ?: dimensions.firstOrNull()?.value
}

/**
 * One dimension entry for a WMS layer.
 *
 * @property name Dimension name (e.g. "time").
 * @property value Comma-separated list of valid values.
 */
@Serializable
@XmlSerialName("Dimension", namespace = "http://www.opengis.net/wms", prefix = "")
data class WMSDimension(
    @SerialName("name") val name: String = "",
    @XmlValue(true) val value: String = ""
)