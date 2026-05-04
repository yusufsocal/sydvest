package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import org.osmdroid.views.MapView

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapWeatherBottomScaffold(
    //Variabler for slider
    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,

    //Variabler for WMSLayers
    selectedLayerDisplayName: String,
    selectedLayer: WMSLayer?,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer?) -> Unit,

    //Variabler for "Farevarsel"
    onFareVarselToggle: () -> Unit,
    isFareVarselActive: Boolean,

    //Variabler for MapChangeArea
    area: AreaData?,
    changeArea: (String) -> Unit,
    mapViewRef: MapView?,
) {
    var peekVal = 0
    var maxHeightVal = 0

    if (selectedLayer != null) {
        peekVal = 100
        maxHeightVal = 400
    } else {
        peekVal = 100
        maxHeightVal = 150
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    var isObjectVisible by remember { mutableStateOf(true) }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            isObjectVisible = true
        } else {
            isObjectVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (selectedLayer != null) {
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        val offset = try {
                            scaffoldState.bottomSheetState.requireOffset()
                        } catch (e: Exception) {
                            0f // Fallback hvis den ikke er klar
                        }

                        if (offset > 0f) {
                            translationY = offset - 65.dp.toPx()
                        }
                    }
            ) {
                Text("Placeholder")
            }
        }
        BottomSheetScaffold(
            sheetPeekHeight = peekVal.dp,
            scaffoldState = scaffoldState,
            containerColor = Color.Transparent,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                    scaffoldState.bottomSheetState.expand()
                                } else {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        }
                        .height(25.dp)
                ) {
                    Image(
                        painter = if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) painterResource(
                            id = R.drawable.arrowup_blue
                        ) else painterResource(id = R.drawable.arrowdown_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
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
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "VELG VÆRLAG",
                                fontWeight = FontWeight.Bold,
                            )
                        }

                    }
                    if (!isObjectVisible) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxHeightVal.dp),
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                            if (selectedLayer != null) {
                                //Slider
                                MapTimeSliderSection(
                                    sliderPosition,
                                    isAnimating,
                                    onSliderChange,
                                    onAnimateToggle,
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                            Text(
                                text = "VELG VÆRLAG",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            //WMSLayer og Farevarsel

                            MapSelectWeatherLayer(
                                selectedLayerDisplayName,
                                selectedLayer,
                                displayLayers,
                                onLayerSelected,

                                onFareVarselToggle,
                                isFareVarselActive
                            )
                            /* SKAL FLYTTES OPP TIL PLACEHOLDER KNAPPEN
                            if (selectedLayer != null) {
                                MapChangeAreaButton(
                                    changeArea,
                                    mapViewRef
                                )
                            }*/

                            Spacer(modifier = Modifier.padding(16.dp))

                        }
                    }

                }

            }
        ) { }
    }

}
