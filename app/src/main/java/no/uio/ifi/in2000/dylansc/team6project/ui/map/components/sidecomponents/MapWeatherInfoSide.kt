package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R

@Suppress("AssignedValueIsNeverRead") // Added to remove false positive warning known in Andorid studio
@Composable
fun MapWeatherInfoSide (
    layerDisplayName: String
){
    var showLegend by remember { mutableStateOf(false) }
    if (showLegend) {
        MapWeatherInfoBox(
            layerDisplayName,
            onDismiss = { showLegend = false}
        )
    }

    Card(
        modifier = Modifier.padding(top = 16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ){

        Column {
        MapWeatherInfoCard(layerDisplayName)

        IconButton(
            onClick = { showLegend = true },
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(32.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.show_colorscale),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        }
    }
}