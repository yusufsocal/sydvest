package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLayerDropdown(
    selectedLayerDisplayName: String,
    areaLabel: String,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showLegend by remember { mutableStateOf(false) }

    if (showLegend) {
        MapLegend(
            layerDisplayName = selectedLayerDisplayName,
            onDismiss = { showLegend = false }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selectedLayerDisplayName,
                    onValueChange = {},
                    label = { Text("Velg værlag (${areaLabel.lowercase()})") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color(0xFFF7FCFE),
                        unfocusedContainerColor = Color(0xFFF7FCFE)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (displayLayers.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Laster lag...") },
                            onClick = { expanded = false }
                        )
                    } else {
                        displayLayers.forEach { (layer, displayName) ->
                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    expanded = false
                                    onLayerSelected(layer)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }

        // (i) knappen vises bare hvis et værlag er valgt
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
