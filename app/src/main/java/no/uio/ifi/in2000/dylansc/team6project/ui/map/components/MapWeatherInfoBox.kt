package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import kotlin.collections.forEach

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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