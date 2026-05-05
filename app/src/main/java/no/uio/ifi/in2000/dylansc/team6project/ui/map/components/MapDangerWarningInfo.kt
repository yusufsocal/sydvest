package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertProperties

@Composable
fun MapDangerWarningInfo(
    properties: AlertProperties?, onDismiss: () -> Unit
) {
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

                // Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$emoji $norsk",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Lukk")
                    }
                }

                // Severity chip-like text
                Text(
                    text = severity,
                    style = MaterialTheme.typography.labelLarge,
                )

                // Area — small and muted
                properties?.area?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Description — main content, readable size
                properties?.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }
        }
    }
}