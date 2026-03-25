package no.uio.ifi.in2000.dylansc.team6project.data.warningdata

import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss")
data class RSS (
    @SerialName("version")
    val version: String = "",
    @XmlSerialName("channel", prefix = "")
    val channel: Channel
)

@Serializable
@XmlSerialName("channel")
data class Channel (
    @XmlElement(true) @SerialName("Title") val title: String = "",
    @XmlSerialName("item", prefix = "")
    val item: Item
)

@Serializable
@XmlSerialName("item")
data class Item (
    @XmlElement (true) @SerialName("title") val title: String = "",
    @XmlElement (true) @SerialName("link") val link: String = ""
)
