package no.uio.ifi.in2000.dylansc.team6project.data

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.* // VIKTIG for Charset i Ktor 3
import io.ktor.utils.io.core.readText
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WmsCapabilities

class WMSDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            register(ContentType.Application.Xml, object : ContentConverter {
                val xml = XML {
                    // Dette er den moderne måten å si "ignorer alt jeg ikke har laget dataklasser for"
                    defaultPolicy {
                        ignoreUnknownChildren()
                    }
                }

                // I Ktor 3 må denne returnere null eller OutgoingContent
                override suspend fun serialize(
                    contentType: ContentType,
                    charset: Charset,
                    typeInfo: TypeInfo,
                    value: Any?
                ): OutgoingContent? {
                    return null
                }

                // I Ktor 3 tar denne 'content: Any' som kan castes til ByteReadChannel
                override suspend fun deserialize(
                    charset: Charset,
                    typeInfo: TypeInfo,
                    content: ByteReadChannel,
                ): Any? {
                    if (content !is ByteReadChannel) return null

                    val serializer = serializer(typeInfo.kotlinType!!)
                    // Vi leser alt innholdet som tekst
                    val stringContent = content.readRemaining().readText(charset)
                    return xml.decodeFromString(serializer, stringContent)
                }
            })
        }
    }

    suspend fun wmsLayer(model: AreaData): WmsCapabilities {
        val url = "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetCapabilities&model=${model.area}"
        return client.get(url).body()
    }
}