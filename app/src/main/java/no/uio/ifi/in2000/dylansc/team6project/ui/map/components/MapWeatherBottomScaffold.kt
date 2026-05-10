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
    ondangerAlertToggle: () -> Unit,
    isdangerAlertActive: Boolean,

    //Variables for MapChangeArea
    area: AreaData?,
    changeArea: (String) -> Unit,
    onShowAreaChange: () -> Unit

) {
    var peekVal = 0
    var maxHeightVal = 0

    if (selectedLayer != null) {
        peekVal = 80
        maxHeightVal = 400
    } else {
        peekVal = 85
        maxHeightVal = 150
    }

    var localSliderPosition by remember(sliderPosition) { mutableStateOf(sliderPosition) }
    LaunchedEffect(localSliderPosition) {

        // Hvis posisjonen er den samme som allerede er lagret i ViewModel, gjør vi ingenting
        if (localSliderPosition == sliderPosition && !isAnimating) return@LaunchedEffect

        // Vent i 150 millisekunder før vi sender verdien videre
        delay(300)


        // Sends the final position to ViewModel after the user has stopped moving the slider
        onSliderChange(localSliderPosition)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    var isObjectVisible by remember { mutableStateOf(true) }
    var areaButtonText by remember { mutableStateOf("Norden") }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            isObjectVisible = true
        } else {
            isObjectVisible = false
        }
    }

    when (area) {
        AreaData.NORDIC -> areaButtonText = "Norden"
        AreaData.ARCTIC -> areaButtonText = "Arktis"
        AreaData.WORLD -> areaButtonText = "Verden"
        else -> areaButtonText
    }

    Box() {
        if (areaChange) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .zIndex(1f)
                    .graphicsLayer {
                        val offset = try {
                            scaffoldState.bottomSheetState.requireOffset()
                        } catch (e: Exception) {
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
                            } catch (e: Exception) {
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
                                    sliderPosition = localSliderPosition, // Use the local value here
                                    isAnimating,
                                    onSliderChange = { newValue ->
                                        localSliderPosition =
                                            newValue // Update only locally while sliding
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

                                ondangerAlertToggle,
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
