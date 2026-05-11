package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.R


/**
 * Small "Now showing: …" label under the map. Renders nothing when no layer is selected.
 *
 * @param selectedLayerDisplayName Display name shown after "Nå vises:".
 * @param selectedLayer Active layer; if `null`, the label is hidden.
 */
@Composable
fun MapSelectedLayer(
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
) {
    if (selectedLayer != null) {
        Row() {
            Text(stringResource (R.string.now_showing))
            Text(
                text = "$selectedLayerDisplayName",
                fontWeight = FontWeight.Bold,
            )
        }

    } else {
        Text("")
    }
}
