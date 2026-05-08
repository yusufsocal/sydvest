package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer

@Composable
fun MapSelectedLayer(
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
) {
    if (selectedLayer != null) {
        Row() {
            Text("Nå vises: ")
            Text(
                text = "$selectedLayerDisplayName",
                fontWeight = FontWeight.Bold,
            )
        }

    } else {
        Text("")
    }
}
