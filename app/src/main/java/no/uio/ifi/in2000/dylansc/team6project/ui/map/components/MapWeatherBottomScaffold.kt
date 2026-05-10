package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold.MapChangeAreaButton
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold.MapSelectWeatherLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold.MapSelectedLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold.MapTimeSliderSection

/**
 * Bottom-sheet controls for the weather map.
 *
 * Hosts the time slider, weather-layer picker, danger-alert toggle, and
 * the area selector. When a weather layer is selected the sheet expands
 * to show the slider plus a "selected layer" header; with no layer selected
 * it shrinks to a compact picker. Slider drags update a local state
 * immediately and are debounced (300 ms) before being forwarded through
 * [onSliderChange] so the ViewModel isn't spammed on every frame.
 *
 * Two floating controls are drawn above the sheet and follow the sheet's
 * vertical offset: a "Select layer" button (when a layer is active) and
 * the area-picker button (when [areaChange] is true).
 *
 * @param areaChange whether the area picker should be shown above the sheet.
 * @param changed invoked when the area picker should be dismissed or when
 *   the floating "Select layer" button is tapped.
 * @param sliderPosition current slider position from the ViewModel,
 *   mapped to the available time range.
 * @param isAnimating whether the timeline is auto-playing.
 * @param onSliderChange called with the debounced final slider value
 *   after the user stops dragging.
 * @param onAnimateToggle toggles the auto-play animation on/off.
 * @param stepHours hour increment between slider steps, used by
 *   [MapTimeSliderSection] for tick marks.
 * @param sliderState formatted label shown next to the slider, typically
 *   the time the current step represents.
 * @param selectedLayerDisplayName user-visible name of the active layer.
 * @param selectedLayer currently active [WMSLayer], or null if none.
 * @param displayLayers all selectable layers paired with their display names.
 * @param onLayerSelected called when the user picks or deselects a layer.
 * @param onDangerAlertToggle toggles the danger-alert overlay on/off.
 * @param isdangerAlertActive whether the danger-alert overlay is currently on.
 * @param area current map area (Nordic / Arctic / World), drives the
 *   area-button label.
 * @param changeArea called with the new area key when the user selects one.
 * @param onShowAreaChange opens the area picker.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapWeatherBottomScaffold(

    areaChange: Boolean,
    changed: () -> Unit,
    //Variabler for slider

    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,
    stepHours: Int,
    sliderState: String,

    //Variables for WMSLayers
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer?) -> Unit,

    //Variables for "dangerAlert"
    onDangerAlertToggle: () -> Unit,
    isdangerAlertActive: Boolean,

    //Variables for MapChangeArea
    area: AreaData?,
    changeArea: (String) -> Unit,
    onShowAreaChange: () -> Unit

) {
    val peekVal = if (selectedLayer != null) 80 else 85
    val maxHeightVal = if (selectedLayer != null) 400 else 150

    var localSliderPosition by remember(sliderPosition) { mutableFloatStateOf(sliderPosition) }
    LaunchedEffect(localSliderPosition) {

        if (localSliderPosition == sliderPosition && !isAnimating) return@LaunchedEffect

        delay(300)


        onSliderChange(localSliderPosition)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    var isObjectVisible by remember { mutableStateOf(true) }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        isObjectVisible = scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
    }

    Box {
        if (areaChange) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .zIndex(1f)
                    .graphicsLayer {
                        val offset = try {
                            scaffoldState.bottomSheetState.requireOffset()
                        } catch (_: Exception) {
                            0f // Fallback if it is not ready
                        }

                        if (offset > 0f) {
                            translationY = offset - 295.dp.toPx()
                        }
                    }) {
                MapChangeAreaButton(
                    changeArea,
                    changed,
                    onShowAreaChange
                )
            }
        }

        if (selectedLayer != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {

                Button(
                    onClick = {
                        changed()
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer {
                            val offset = try {
                                scaffoldState.bottomSheetState.requireOffset()
                            } catch (_: Exception) {
                                0f // Fallback if it is not ready
                            }

                            if (offset > 0f) {
                                translationY = offset - 65.dp.toPx()
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp),
                        contentDescription = null
                    )
                }


            }

        }

        BottomSheetScaffold(
            sheetPeekHeight = peekVal.dp,
            scaffoldState = scaffoldState,
            containerColor = Color.Transparent,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                    scaffoldState.bottomSheetState.expand()
                                } else {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxSize()
                    )
                }
            },
            sheetContent = {
                Column(modifier = Modifier.heightIn(max = maxHeightVal.dp)) {
                    if (isObjectVisible && selectedLayer != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MapSelectedLayer(
                                selectedLayerDisplayName,
                                selectedLayer,
                            )
                        }
                    }
                    if (isObjectVisible && selectedLayer == null) {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.værlag),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.graphicsLayer {
                                        translationY = -8.dp.toPx()
                                    }
                                )
                            }
                        }
                    }
                    if (!isObjectVisible) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .heightIn(max = maxHeightVal.dp)
                                .graphicsLayer {
                                    translationY = -8.dp.toPx()
                                },
                        ) {
                            if (selectedLayer != null) {
                                //Slider
                                MapTimeSliderSection(
                                    sliderPosition = localSliderPosition,
                                    isAnimating,
                                    onSliderChange = { newValue ->
                                        localSliderPosition =
                                            newValue
                                    },
                                    onAnimateToggle,
                                    stepHours,
                                    sliderState,
                                )
                            }
                            Text(
                                text = stringResource(R.string.velg_værlag),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            //WMSLayer and weather Warning
                            MapSelectWeatherLayer(
                                selectedLayerDisplayName,
                                selectedLayer,
                                displayLayers,
                                onLayerSelected,

                                onDangerAlertToggle,
                                isdangerAlertActive
                            )

                            Spacer(modifier = Modifier.padding(16.dp))

                        }
                    }

                }

            }
        ) { }
    }

}
