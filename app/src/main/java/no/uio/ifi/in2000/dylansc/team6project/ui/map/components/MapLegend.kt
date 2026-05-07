package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class LegendEntry(val color: Color, val label: String)

val temperatureLegend = listOf(
    LegendEntry(Color(0xFF6A0DAD), "Under −20°C"),
    LegendEntry(Color(0xFF1565C0), "−20 til 0°C"),
    LegendEntry(Color(0xFF43A047), "0 til 10°C"),
    LegendEntry(Color(0xFFFDD835), "10 til 20°C"),
    LegendEntry(Color(0xFFFB8C00), "20 til 30°C"),
    LegendEntry(Color(0xFFB71C1C), "Over 30°C"),
)

val precipitationLegend = listOf(
    LegendEntry(Color(0x00FFFFFF), "Ingen nedbør"),
    LegendEntry(Color(0xFFB2DFDB), "Lett (0–1 mm)"),
    LegendEntry(Color(0xFF43A047), "Moderat (1–5 mm)"),
    LegendEntry(Color(0xFF00796B), "Kraftig (5–10 mm)"),
    LegendEntry(Color(0xFF1565C0), "Svært kraftig (10–20 mm)"),
    LegendEntry(Color(0xFF4A148C), "Ekstrem (20+ mm)"),
)
// TODO: sjekke om disse fargene stemmer?
val windLegend = listOf(
    LegendEntry(Color(0xFFB2EBF2), "Stille (0–5 m/s)"),
    LegendEntry(Color(0xFFFDD835), "Moderat (5–10 m/s)"),
    LegendEntry(Color(0xFFFB8C00), "Sterk (10–15 m/s)"),
    LegendEntry(Color(0xFFE53935), "Svært sterk (15–20 m/s)"),
    LegendEntry(Color(0xFF6A0DAD), "Storm (20+ m/s)"),
)

@Composable
fun MapLegend(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Fargeskala – $title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
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
                        Text(text = entry.label, fontSize = 15.sp)
                    }
                }

                if (layerDisplayName.contains("Wind", ignoreCase = true)) {
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