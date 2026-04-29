package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Row
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MapSelectedLayer (
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
) {
    if (selectedLayer != null) Text("Nå vises: ${selectedLayerDisplayName}") else Text("")
}
