package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.R.attr.text
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTimeSliderSection(
    mapViewModel: MapViewModel,

    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,

    selectedLayer: WMSLayer?

) {
    val current = LocalDateTime.now()
    val hoursAhead = current.plusHours(sliderPosition.toInt().toLong())
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    val formatterCompare = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val formatterDate = DateTimeFormatter.ofPattern("dd.")

    val timeOfDay = hoursAhead.format(formatterTime)

    val dayOfWeek = hoursAhead.dayOfWeek
    val weekday = dayOfWeek.getDisplayName(TextStyle.FULL, LocalLocale.current.platformLocale)

    val monthOfYear = hoursAhead.month
    val month =
        monthOfYear.getDisplayName(TextStyle.FULL_STANDALONE, LocalLocale.current.platformLocale)


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (current.format(formatterCompare) == hoursAhead.format(formatterCompare)) {
            Text(
                text = "I DAG · $timeOfDay",
                fontSize = 15.sp
            )
        } else {
            Text(
                text = "${weekday.uppercase()} ${hoursAhead.format(formatterDate)}$month · $timeOfDay",
                fontSize = 15.sp
            )
        }

        Text(text = "+${sliderPosition.toInt()}T")
    }
    Row() {
        Button(
            onClick = { onAnimateToggle() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.clip(CircleShape)
        ) {
            Image(
                painter = if (isAnimating) painterResource(id = R.drawable.pause) else painterResource(
                    id = R.drawable.play
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )
        }
        Slider(
            value = sliderPosition,
            onValueChange = onSliderChange,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 239,
            valueRange = 0f..240f,
        )
    }
}


