package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.warninghint

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon

/**
 * Dialog showing details for a single weather alert: type, severity, area,
 * description, and a small map highlighting the affected polygon in the
 * MET risk-matrix color (yellow / orange / red).
 *
 * @param feature The alert to display, or `null` to render nothing meaningful.
 * @param onDismiss Called when the user closes the dialog.
 */
@Composable
fun MapDangerWarningInfo(
    feature: AlertFeature?, onDismiss: () -> Unit
) {
    val properties = feature?.properties


    val (emoji, norsk) = when (properties?.event?.lowercase()) {
        "blowingsnow" -> "🌨️" to stringResource(R.string.blowing_snow)
        "forestfire" -> "🔥" to stringResource(R.string.wildfire)
        "gale" -> "💨" to stringResource(R.string.gale)
        "ice" -> "🧊" to stringResource(R.string.ice)
        "icing" -> "🧊" to stringResource(R.string.icing)
        "lightning" -> "⚡" to stringResource(R.string.lightning)
        "polarlow" -> "🌀" to stringResource(R.string.polar_low)
        "rain" -> "🌧️" to stringResource(R.string.rain)
        "rainflood" -> "🌊" to stringResource(R.string.rainflood)
        "snow" -> "❄️" to stringResource(R.string.snow)
        "stormsurge" -> "🌊" to stringResource(R.string.storm_surge)
        "wind" -> "💨" to stringResource(R.string.wind)
        else -> "⚠️" to (properties?.event?.replaceFirstChar { it.uppercase() } ?: "")
    }

    val severity = when (properties?.severity?.lowercase()) {
        "extreme" -> stringResource(R.string.extreme)
        "severe" -> stringResource(R.string.severe)
        "moderate" -> stringResource(R.string.moderate)
        "minor" -> stringResource(R.string.minor)
        else -> "⚠️ " + (properties?.severity?.replaceFirstChar { it.uppercase() } ?: "")
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
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
                            val coords = geometry.coordinates.jsonArray

                            val points = when {
                                geometry.type.equals("Polygon", true) ->
                                    coords.firstOrNull()?.jsonArray?.mapNotNull {
                                        val pair = it.jsonArray
                                        if (pair.size >= 2) GeoPoint(
                                            pair[1].jsonPrimitive.double,
                                            pair[0].jsonPrimitive.double
                                        )
                                        else null
                                    }
                                geometry.type.equals("MultiPolygon", true) ->
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
                                fillPaint.color = "#80$hex".toColorInt()
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