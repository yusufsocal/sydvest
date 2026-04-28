package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomScaffold (
    //Variabler for slider
    sliderPosition: Float,
    isAnimating: Boolean,
    onSliderChange: (Float) -> Unit,
    onAnimateToggle: () -> Unit,

    //Variabler for "Sentrer" og "Farevarsel"
    onCenterClick: () -> Unit,
    isCenterActive: Boolean,
    onFareVarselToggle: () -> Unit,
    isFareVarselActive: Boolean,

    //Variabler for WMSLayers
    selectedLayerDisplayName: String,
    areaLabel: String,
    displayLayers: List<Pair<WMSLayer, String>>,
    onLayerSelected: (WMSLayer) -> Unit
) {
    val showDragHandle  by remember { mutableStateOf(false) }

    BottomSheetScaffold(

        sheetContent = { if (showDragHandle) {
            Spacer(modifier = Modifier.height(8.dp))
        }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ){
                //Slider
                MapTimeSliderSection(
                    sliderPosition,
                    isAnimating,
                    onSliderChange,
                    onAnimateToggle
                )

                //Sentrer og Farevarsler
                MapBottomControls(
                    onCenterClick,
                    isCenterActive,
                    onFareVarselToggle,
                    isFareVarselActive,
                )

                //WMSLayer
                MapLayerDropdown(
                    selectedLayerDisplayName,
                    areaLabel,
                    displayLayers,
                    onLayerSelected
                )
            }
        }
    ) { }
}
