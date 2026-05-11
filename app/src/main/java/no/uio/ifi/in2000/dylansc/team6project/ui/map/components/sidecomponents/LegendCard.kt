package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.data.weather.precipitationLegend
import no.uio.ifi.in2000.dylansc.team6project.data.weather.temperatureLegend
import no.uio.ifi.in2000.dylansc.team6project.data.weather.windLegend

/**
 * Compact legend card shown next to the map, with just the color swatches and
 * short value labels (e.g. "°C", "mm", "m/s") for the active layer.
 *
 * Renders nothing if [layerDisplayName] doesn't match a known layer.
 */
@Composable
fun LegendCard(
    layerDisplayName: String,
) {
    Log.d("LEGEND", "layerDisplayName: $layerDisplayName")
    val (title, entries) = when {
        layerDisplayName.contains("Temperatur", ignoreCase = true) ->
            "°C" to temperatureLegend

        layerDisplayName.contains("Nedbør", ignoreCase = true) ||
                layerDisplayName.contains("Precipitation", ignoreCase = true) ->
            "mm" to precipitationLegend

        layerDisplayName.contains("Vind", ignoreCase = true) ->
            "m/s" to windLegend

        else -> return // No legend if the layers are not recognized
    }

    Log.d("LEGEND", "layerDisplayName: $layerDisplayName")


    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.padding(8.dp),
        ) {

            item {
                Text(
                    text = title,
                    fontSize = 10.sp,
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
                                    .size(width = 10.dp, height = 27.dp)
                                    .background(entry.color)
                                    .graphicsLayer {
                                        translationY = -8.dp.toPx()
                                    }
                            )
                        }


                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                    Column() {
                        entries.forEach { entry ->
                            Text(
                                text = entry.label,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }

            }
        }
    }
}