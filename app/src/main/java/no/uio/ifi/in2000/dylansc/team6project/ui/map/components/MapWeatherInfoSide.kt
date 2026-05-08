package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.unit.dp

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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ){
        MapWeatherInfoCard(layerDisplayName)

        Spacer(modifier = Modifier.padding(8.dp))

        IconButton(
            onClick = { showLegend = true },
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Vis fargeskala",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

    }
}