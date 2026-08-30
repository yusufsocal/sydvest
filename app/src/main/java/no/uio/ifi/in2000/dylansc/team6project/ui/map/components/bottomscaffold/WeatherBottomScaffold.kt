@file:Suppress("KDocUnresolvedReference")

package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSLayer

/**
 * Bottom-sheet controls for the weather map.
 *
 * Hosts the time slider and weather-layer picker. When a weather layer is
 * selected the sheet expands to show the slider plus a "selected layer"
 * header; with no layer selected it shrinks to a compact picker. Slider
 * drags update a local state immediately and are debounced (300 ms) before
 * being forwarded through [onSliderChange] so the ViewModel isn't spammed
 * on every frame.
 *
 * The danger-alert toggle and area picker used to live here as floating
 * controls above the sheet; they've moved into the "more options" menu in
 * [no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents.MapSideControls]
 * so this sheet only covers what it's actually for — the active layer and
 * its timeline.
 *
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
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapWeatherBottomScaffold(

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

) {
    val peekVal = if (selectedLayer != null) 100 else 85
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
        BottomSheetScaffold(
            sheetPeekHeight = peekVal.dp,
            scaffoldState = scaffoldState,
            containerColor = Color.Transparent,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            sheetDragHandle = {
                val isPartiallyExpanded =
                    scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                val handleLabel =
                    if (isPartiallyExpanded) stringResource(R.string.expand_panel)
                    else stringResource(R.string.collapse_panel)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clickable(
                            onClickLabel = handleLabel,
                            role = Role.Button
                        ) {
                            scope.launch {
                                if (isPartiallyExpanded) {
                                    scaffoldState.bottomSheetState.expand()
                                } else {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (isPartiallyExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
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
                                    text = stringResource(R.string.weather_layer),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.graphicsLayer {
                                        translationY = -28.dp.toPx()
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
                                text = stringResource(R.string.select_weather_layer),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            //WMSLayer picker
                            MapSelectWeatherLayer(
                                selectedLayerDisplayName,
                                selectedLayer,
                                displayLayers,
                                onLayerSelected,
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }

                }

            }
        ) { }
    }

}
