package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTimeSliderSection(
    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,
    stepHours: Int,
    sliderState: String,
) {
    val hourStep = stepHours.coerceAtLeast(1)
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

    val font_size = 12


    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (current.format(formatterCompare) == hoursAhead.format(formatterCompare)) {
                Text(
                    text = "I DAG · $timeOfDay",
                    fontSize = font_size.sp
                )
            } else {
                Text(
                    text = "${weekday.uppercase()} ${hoursAhead.format(formatterDate)}$month · $timeOfDay",
                    fontSize = font_size.sp
                )
            }

            if (sliderState == "timer") {
                Text(
                    text = "+${sliderPosition.toInt()}T",
                    fontSize = font_size.sp
                )
            } else {
                Text(
                    text = "+${sliderPosition.toInt()/24}D",
                    fontSize = font_size.sp
                )
            }

        }


        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            IconButton(
                onClick = { onAnimateToggle() },
                shape = CircleShape,

            ) {
                Icon(
                    imageVector = if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            val range: ClosedRange<Float> = 0f..240f
            val highlightStep: Int = 24

            Slider(
                value = sliderPosition,
                onValueChange = onSliderChange,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 239,
                valueRange = range as ClosedFloatingPointRange<Float>,
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(8.dp),
                        // We are overriding how the track is drawn
                        drawStopIndicator = null, // Removes standard stop indicator
                        thumbTrackGapSize = 0.dp,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                        )
                    )

                    // Here we are drawing our own markings on top of the track
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    ) {
                        val totalSteps = range.endInclusive.toInt()
                        for (i in 0..totalSteps step highlightStep) {
                            val fraction = i / range.endInclusive
                            val xPos = size.width * fraction

                            drawCircle(
                                color = if (sliderPosition >= i) Color.White else Color.Gray,
                                radius = if (i % highlightStep == 0) 2.dp.toPx() else 2.dp.toPx(),
                                center = Offset(xPos, size.height / 2)
                            )
                        }
                    }
                }
            )

        }
        Spacer(modifier = Modifier.padding(8.dp))

    }
}



