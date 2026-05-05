package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue




//@SerialName sørger for at man kan hente data fra XML-fil med stor forbokstav, men gjør
//at dataen initialiseres til en variabel med liten forbokstav, slik at man følger riktig kodepraksis.

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
    // Velg "time"-dimensjonen, ikke "reference_time" — ellers får vi PT12H i stedet for
    // PT3H for ECMWF/Verden, og slider/animasjon hopper feil intervall.
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