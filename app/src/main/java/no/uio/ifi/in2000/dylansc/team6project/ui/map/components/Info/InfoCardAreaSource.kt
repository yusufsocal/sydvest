package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.Info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MapAreaDataCard(
    label: String,
    metadata: String,
    bulletList: List<String>,
    onCardClick: () -> Unit,
    selectedArea: String,
    modifier: Modifier = Modifier,
) {
    val selected = (selectedArea == label)

    Card(
        onClick = {
            onCardClick()
                  },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        colors = if (selected) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer)
            else CardDefaults.cardColors()
        ,

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            Text(
                text = metadata,
                style = MaterialTheme.typography.labelSmall,
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                bulletList.forEach { bullet ->
                    Row {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
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