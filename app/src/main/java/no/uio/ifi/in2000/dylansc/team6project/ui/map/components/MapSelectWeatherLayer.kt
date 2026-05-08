package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectWeatherLayer(
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer?) -> Unit,

    onFareVarselToggle: () -> Unit,
    isFareVarselActive: Boolean
) {
    var showLegend by remember { mutableStateOf(false) }
    var textSize by remember { mutableStateOf(12) }

    if (showLegend) {
        MapWeatherInfoBox(
            selectedLayerDisplayName,
            onDismiss = {}
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            displayLayers.forEach { (layer, displayName) ->
                val isSelected = selectedLayer?.name == layer.name
                OutlinedButton(
                    onClick = {
                        if (isSelected) onLayerSelected(null) else onLayerSelected(layer)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (isSelected)
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    else ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black

                    )
                ) {
                    Text(
                        text = displayName,
                        fontSize = textSize.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            OutlinedButton(
                onClick = onFareVarselToggle,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(0.dp),
                colors = if (isFareVarselActive)
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                else ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black

                )
            ) {
                Text(
                    text = "Farevarsler",
                    fontSize = textSize.sp,
                    textAlign = TextAlign.Center,
                )
            }

        }
        // (i) the button is only visible if a weather layer is chosen
        if (selectedLayerDisplayName != "Velg værlag...") {
            IconButton(onClick = { showLegend = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Vis fargeskala",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
