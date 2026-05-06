package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.R.attr.font
import android.R.attr.text
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,
    stepHours: Int,
    sliderState: String,
    changeSliderState: (String) -> Unit
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

    val width = 70
    val height = 40
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

            Row {
                OutlinedButton(
                    onClick = {
                        changeSliderState("timer")
                    },
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (sliderState == "timer")
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    else
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                    modifier = Modifier
                        .width(width.dp)
                        .height(height.dp)
                ) {
                    Text(
                        text = "24 timer",
                        fontSize = font_size.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.zIndex(1f)
                    )
                }

                Spacer(Modifier.width(16.dp))

                OutlinedButton(
                    onClick = { changeSliderState("døgn") },
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = if (sliderState == "døgn")
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    else
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                    modifier = Modifier
                        .width(width.dp)
                        .height(height.dp)
                ) {
                    Text(
                        text = "10 døgn",
                        fontSize = font_size.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.zIndex(1f)
                    )
                }
            }

            Text(
                text = "+${sliderPosition.toInt()}T",
                fontSize = font_size.sp
            )
        }



        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = { onAnimateToggle() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.clip(CircleShape)
            ) {
                Image(
                    painter = if (isAnimating) painterResource(id = R.drawable.pause_blue) else painterResource(
                        id = R.drawable.play_blue
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
            }

            if (sliderState == "timer") {
                val range: ClosedRange<Float> = 0f..24f
                val highlightStep: Int = hourStep
                // Antall diskrete posisjoner mellom endepunktene = (24 / step) - 1
                val sliderSteps = ((24 / hourStep) - 1).coerceAtLeast(0)

                Slider(
                    value = sliderPosition,
                    onValueChange = onSliderChange,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = sliderSteps,
                    valueRange = range as ClosedFloatingPointRange<Float>,
                    track = { sliderState ->
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            modifier = Modifier.height(8.dp),
                            // Vi overstyrer hvordan tracken tegnes
                            drawStopIndicator = null, // Fjerner standard stopp-indikator
                            thumbTrackGapSize = 0.dp,
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                        )

                        // Her tegner vi våre egne markeringer oppå tracken
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
            } else if (sliderState == "døgn") {
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
                            // Vi overstyrer hvordan tracken tegnes
                            drawStopIndicator = null, // Fjerner standard stopp-indikator
                            thumbTrackGapSize = 0.dp,
                            colors = SliderDefaults.colors(
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent,
                            )
                        )

                        // Her tegner vi våre egne markeringer oppå tracken
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
        }
        Spacer(modifier = Modifier.padding(8.dp))

    }
}



