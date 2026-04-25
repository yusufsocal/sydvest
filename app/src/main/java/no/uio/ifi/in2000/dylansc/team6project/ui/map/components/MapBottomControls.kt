package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun MapBottomControls(
    onCenterClick: () -> Unit,
    onFareVarselToggle: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onCenterClick,
            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
        ) {
            Text(text = "Sentrer", color = ComposeColor.Black)
        }

        OutlinedButton(
            onClick = onFareVarselToggle,
            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
        ) {
            Text(text = "Farevarsler", color = ComposeColor.Black)
        }
    }
}
