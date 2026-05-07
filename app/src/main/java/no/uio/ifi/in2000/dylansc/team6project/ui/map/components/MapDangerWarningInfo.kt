package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import org.osmdroid.util.GeoPoint
import android.graphics.Color as AndroidColor

@Composable
fun MapDangerWarningInfo(
    feature: AlertFeature?, onDismiss: () -> Unit
) {
    val properties = feature?.properties

    val (emoji, norsk) = when (properties?.event?.lowercase()) {
        "blowingsnow" -> "🌨️" to "Snøfokk"
        "forestfire" -> "🔥" to "Skogbrann"
        "gale" -> "💨" to "Kuling"
        "ice" -> "🧊" to "Is"
        "icing" -> "🧊" to "Ising"
        "lightning" -> "⚡" to "Lyn"
        "polarlow" -> "🌀" to "Polare lavtrykk"
        "rain" -> "🌧️" to "Regn"
        "rainflood" -> "🌊" to "Styrtflom"
        "snow" -> "❄️" to "Snø"
        "stormsurge" -> "🌊" to "Stormflo"
        "wind" -> "💨" to "Vind"
        else -> "⚠️" to (properties?.event?.replaceFirstChar { it.uppercase() } ?: "")
    }

    val severity = when (properties?.severity?.lowercase()) {
        "extreme" -> "🔴 Ekstrem"
        "severe" -> "🟠 Alvorlig"
        "moderate" -> "🟡 Moderat"
        "minor" -> "🟢 Liten"
        else -> "⚠️ " + (properties?.severity?.replaceFirstChar { it.uppercase() } ?: "")
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type of warning
                    Text(
                        text = "$emoji $norsk",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Lukk")
                    }
                }

                // Severity
                Text(
                    text = severity,
                    style = MaterialTheme.typography.labelLarge,
                )

                // Area
                properties?.area?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Description
                properties?.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }

            feature?.geometry?.let { geometry ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(pass = PointerEventPass.Initial)
                                        .changes.forEach { it.consume() }
                                }
                            }
                        }
                ) {
                    // Map with warning
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(false)
                                isClickable = false
                                isFocusable = false
                                setScrollableAreaLimitLatitude(85.0, -85.0, 0)
                                overlayManager.tilesOverlay.isEnabled = true
                            }
                        },
                        update = { mapView ->
                            val coords = geometry.coordinates?.jsonArray ?: return@AndroidView

                            val points = when {
                                geometry.type?.equals("Polygon", true) == true ->
                                    coords.firstOrNull()?.jsonArray?.mapNotNull {
                                        val pair = it.jsonArray
                                        if (pair.size >= 2) GeoPoint(
                                            pair[1].jsonPrimitive.double,
                                            pair[0].jsonPrimitive.double
                                        )
                                        else null
                                    }
                                geometry.type?.equals("MultiPolygon", true) == true ->
                                    coords.firstOrNull()?.jsonArray?.firstOrNull()?.jsonArray?.mapNotNull {
                                        val pair = it.jsonArray
                                        if (pair.size >= 2) GeoPoint(pair[1].jsonPrimitive.double, pair[0].jsonPrimitive.double)
                                        else null
                                    }
                                else -> null
                            } ?: return@AndroidView

                            val polygon = Polygon(mapView).apply {
                                this.points = points.toMutableList()
                                val hex = when (feature.properties?.riskMatrixColor) {
                                    "Yellow" -> "FFFF00"
                                    "Orange" -> "FFA500"
                                    "Red" -> "FF0000"
                                    else -> "FFFFFF"
                                }
                                fillPaint.color = AndroidColor.parseColor("#80$hex")
                            }
                            mapView.overlays.clear()
                            mapView.overlays.add(polygon)

                            // Zoom to fit the polygon
                            val boundingBox = BoundingBox.fromGeoPoints(points)
                            mapView.post {
                                mapView.zoomToBoundingBox(boundingBox, true, 32)
                            }
                            mapView.invalidate()
                        }
                    )
                }
            }
        }
    }
}