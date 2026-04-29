package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.icu.number.Scale.none
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomScaffold (
    mapViewModel: MapViewModel,
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
    isFareVarselActive: Boolean

) {
    val showDragHandle  by remember { mutableStateOf(false) }
    var peekVal = 0
    if (selectedLayer != null)  peekVal = 100 else peekVal = 70

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


    BottomSheetScaffold(
        sheetPeekHeight = peekVal.dp,
        scaffoldState = scaffoldState,
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
            ) {
                BottomSheetDefaults.DragHandle()
            }
        },
        sheetContent = { if (showDragHandle) {
            Spacer(modifier = Modifier
                .height(8.dp)

            )
        }
            if (isObjectVisible && selectedLayer != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MapSelectedLayer (
                        selectedLayerDisplayName,
                        selectedLayer,
                    )

                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(2.dp)
                    .verticalScroll(rememberScrollState())
            ){
                //Slider
                MapTimeSliderSection(
                    mapViewModel,
                    sliderPosition,
                    isAnimating,
                    onSliderChange,
                    onAnimateToggle,
                    selectedLayer
                )

                //WMSLayer og Farevarsel
                MapLayerDropdown(
                    selectedLayerDisplayName,
                    selectedLayer,
                    displayLayers,
                    onLayerSelected,

                    onFareVarselToggle,
                    isFareVarselActive
                )
            }
        }
    ) { }
}
