package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.precipitationLegend
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.temperatureLegend
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.windLegend

/**
 * Full legend dialog explaining the colors of the active map layer.
 *
 * Picks the matching legend (temperature / precipitation / wind) based on
 * [layerDisplayName]. Renders nothing if the name doesn't match a known layer.
 *
 * @param layerDisplayName Display name of the active layer.
 * @param onDismiss Called when the user closes the dialog.
 */
@Composable
fun MapWeatherInfoBox(
    layerDisplayName: String,
    onDismiss: () -> Unit
) {
    Log.d("LEGEND", "layerDisplayName: $layerDisplayName")
    val (title, entries) = when {
        layerDisplayName.contains("Temperatur", ignoreCase = true) ->
            "Temperatur" to temperatureLegend

        layerDisplayName.contains("Nedbør", ignoreCase = true) ||
                layerDisplayName.contains("Precipitation", ignoreCase = true) ->
            "Nedbør" to precipitationLegend

        layerDisplayName.contains("Vind", ignoreCase = true) ->
            "Vind" to windLegend

        else -> return // No legend if the layers are not recognised
    }

    Log.d("LEGEND", "layerDisplayName: $layerDisplayName")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
            ) {

                item {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            entries.forEach { entry ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 16.dp, height = 27.dp)
                                        .background(entry.color)
                                        .graphicsLayer {
                                            translationY = -8.dp.toPx()
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        Column() {
                            entries.forEach { entry ->
                                Text(
                                    text = entry.description,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                    if (layerDisplayName.contains("Vind", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilene viser vindretning.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Lukk")
                    }
                }
            }
        }
    }
}