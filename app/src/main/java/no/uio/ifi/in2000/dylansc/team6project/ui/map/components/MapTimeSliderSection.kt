package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun MapTimeSliderSection(
    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit
) {
    Slider(
        value = sliderPosition,
        onValueChange = onSliderChange,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        steps = 239,
        valueRange = 0f..240f,
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = sliderPosition.toInt().toString(),
            modifier = Modifier
                .padding(8.dp)
                .background(color = ComposeColor.White, shape = RoundedCornerShape(12.dp))
                .border(width = 2.dp, color = ComposeColor.Black, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        )

        OutlinedButton(
            onClick = onAnimateToggle,
            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
        ) {
            Text(
                text = if (isAnimating) "Stop" else "Animer",
                color = ComposeColor.Black
            )
        }
    }
}
