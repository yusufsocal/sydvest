package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSLayer

/**
 * Row of toggleable buttons for picking which weather layer is shown on the map.
 *
 * Tapping the active layer again deselects it (passes `null` to [onLayerSelected]).
 *
 * @param selectedLayerDisplayName Display name of the active layer (currently unused).
 * @param selectedLayer The currently active layer, or `null` if none.
 * @param displayLayers Available layers paired with their display names.
 * @param onLayerSelected Called with the new layer (or `null` to deselect).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectWeatherLayer(
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer?) -> Unit,
) {
    var textSize by remember { mutableIntStateOf(14) }
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
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    else ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface

                    )
                ) {
                    Text(
                        text = displayName,
                        fontSize = textSize.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
