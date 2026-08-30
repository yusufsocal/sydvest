package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Compact chip showing just the active layer's unit (e.g. "°C"). Tapping it
 * opens the full [LegendDialog] with the color scale and descriptions,
 * instead of the color scale sitting permanently on the map.
 */
@Composable
fun LegendSidePanel (
    layerDisplayName: String
){
    var showLegend by remember { mutableStateOf(false) }
    if (showLegend) {
        LegendDialog(
            layerDisplayName,
            onDismiss = { showLegend = false}
        )
    }

    val unit = when {
        layerDisplayName.contains("Temperatur", ignoreCase = true) -> "°C"
        layerDisplayName.contains("Nedbør", ignoreCase = true) ||
            layerDisplayName.contains("Precipitation", ignoreCase = true) -> "mm"
        layerDisplayName.contains("Vind", ignoreCase = true) -> "m/s"
        else -> return
    }

    Card(
        onClick = { showLegend = true },
        modifier = Modifier.padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(text = unit, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.show_colorscale),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}