package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName


//@SerialName sørger for at man kan hente data fra XML-fil med stor forbokstav, men gjør
//at dataen initialiseres til en variabel med liten forbokstav, slik at man følger riktig kodepraksis.

@Serializable
@XmlSerialName("WMS_Capabilities") // Roten
data class WmsCapabilities(
    @SerialName("Capability") val capability: Capability
) {
    companion object
}

@Serializable
data class Capability(
    @SerialName("Layer") val rootLayer: ParentLayer // Går inn i det første Layer-nivået
)

@Serializable
data class ParentLayer(
    @SerialName("Layer") val wmsListe: List<WmsLayer> // Henter listen med de faktiske lagene
)

@Serializable
data class WmsLayer(
    @SerialName("Name") val name: String, //datanavn på datatype
    @SerialName("Title") val title: String, //lesbart navn på datatype
    @SerialName("Dimension") val dimension: String? = null //Tidsintervall
)