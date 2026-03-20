package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName



//@SerialName sørger for at man kan hente data fra XML-fil med stor forbokstav, men gjør
//at dataen initialiseres til en variabel med liten forbokstav, slik at man følger riktig kodepraksis.

@Serializable
@XmlSerialName("WMS_Capabilities", namespace = "http://www.opengis.net/wms", prefix = "")
data class WmsCapabilities(
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
    @SerialName("Title") val title: String,
    // Her sier vi at alle <Layer> tagger inni dette laget skal i listen
    @XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
    val wmsListe: List<WmsLayer> = emptyList()
)

@Serializable
@XmlSerialName("Layer", namespace = "http://www.opengis.net/wms", prefix = "")
data class WmsLayer(
    @SerialName("Name") val name: String = "",
    @SerialName("Title") val title: String = "",
    // Dimension er ofte et element med tekstinnhold, ikke bare en attributt
    @SerialName("Dimension") val dimension: String? = null
)