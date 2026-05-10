package no.uio.ifi.in2000.dylansc.team6project.ui.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Selectable card describing one weather model area (NORDIC / ARCTIC / WORLD)
 * with its coverage info and a few bullet points.
 *
 * @param label Display name shown in the card and used to detect selection.
 * @param metadataArea Short description of the geographic coverage.
 * @param metadataRange Forecast range info (e.g. "60 hours ahead").
 * @param bulletList Extra info lines shown under the header.
 * @param onCardClick Called when the user taps the card.
 * @param selectedArea Label of the currently selected card.
 */
@Composable
fun InfoCardAreaSource(
    label: String,
    metadataArea: String,
    metadataRange: String,
    bulletList: List<String>,
    onCardClick: () -> Unit,
    selectedArea: String,

    ) {
    val selected = (selectedArea == label)

    Card(
        onClick = {
            onCardClick()
        },
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),

        ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = metadataArea,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

            }
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
            ) {

            }



            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                bulletList.forEach { bullet ->
                    Row {
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}