package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LegendEntry(val color: Color, val label: String)

val temperatureLegend = listOf(
    LegendEntry(Color(0xFF6A0DAD), "<−20"),
    LegendEntry(Color(0xFF1565C0), "−20-0"),
    LegendEntry(Color(0xFF43A047), "0-10"),
    LegendEntry(Color(0xFFFDD835), "10-20"),
    LegendEntry(Color(0xFFFB8C00), "20-30"),
    LegendEntry(Color(0xFFB71C1C), "> 30"),
)

val precipitationLegend = listOf(
    LegendEntry(Color(0x00FFFFFF), "0"),
    LegendEntry(Color(0xFFB2DFDB), "0–1"),
    LegendEntry(Color(0xFF43A047), "1–5"),
    LegendEntry(Color(0xFF00796B), "5–10"),
    LegendEntry(Color(0xFF1565C0), "10–20"),
    LegendEntry(Color(0xFF4A148C), ">20)"),
)

// TODO: sjekke om disse fargene stemmer?
val windLegend = listOf(
    LegendEntry(Color(0xFFB2EBF2), "0–5"),
    LegendEntry(Color(0xFFFDD835), "5–10"),
    LegendEntry(Color(0xFFFB8C00), "10–15"),
    LegendEntry(Color(0xFFE53935), "15–20"),
    LegendEntry(Color(0xFF6A0DAD), ">20"),
)

@Composable
fun MapWeatherInfoSide(
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

        else -> return // No legend if the layers are not recognised
    }

    Log.d("LEGEND", "layerDisplayName: $layerDisplayName")


    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp
            )

            entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(entry.color)
                            .border(1.dp, Color.Gray, CircleShape)
                    )
                    Text(text = entry.label, fontSize = 8.sp)
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
        }
    }
}