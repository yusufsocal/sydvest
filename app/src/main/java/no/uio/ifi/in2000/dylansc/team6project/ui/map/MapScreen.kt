//Selve skjermen -> Viser kun hva den får beskjed om fra ViewModel

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten
    mapViewModel: MapViewModel
) {

    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // 1. Viktig oppsett for at kartet skal kunne lagre bilder på telefonen
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
    )

    // 2. Vi bruker AndroidView for å putte det gamle Android-kartet inn i Compose
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(10.0)
                controller.setCenter(GeoPoint(60.90, 10.75))
                mapViewRef = this // Lagre referansen her!
            }
        },
        modifier = Modifier.fillMaxSize(),
    )


    Box() {

        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                //Knapp for å aktivere farevarsler på kartet.
                OutlinedButton( //Kan byttes ut med IconButton
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                ) {
                    Text(
                        text = "Farevarsler",
                        color = Color.Black
                    )
                }
                // LISTE MED VÆRLAG (DROPDOWN)
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf("Velg værlag...") }
                var areaData by remember { mutableStateOf("${mapScreenUiState.area}")}
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor() // KRITISK: Denne kobler TextField til menyen
                                .fillMaxWidth(),
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = {},
                            label = { Text("Velg værlag (${areaData.lowercase()})") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color(0xFFF7FCFE),
                                unfocusedContainerColor = Color(0xFFF7FCFE)
                            )
                        )

                        // Vi sjekker om det faktisk er noe i lista før vi prøver å vise menyen
                        if (mapScreenUiState.layerList.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                mapScreenUiState.layerList.forEach { layer ->
                                    //PROSJEKT CUSTOM AREA
                                    //Funksjonalitet for å fjerne suffix basert på hvilket område som benyttes
                                    var nyTitle: String = ""
                                    if (mapScreenUiState.area == AreaData.NORDEN) {
                                        nyTitle = layer.title.removeSuffix("in MEPS VDIV")
                                    } else if (mapScreenUiState.area == AreaData.ARKTIS) {
                                        nyTitle = layer.title.removeSuffix("in Arctic VDIV")
                                    } else if (mapScreenUiState.area == AreaData.VERDEN) {
                                        nyTitle = layer.title.removeSuffix("in ECMWF VDIV 1h")
                                    }
                                    //-----------------------
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = nyTitle)
                                        },
                                        onClick = {
                                            selectedOptionText = nyTitle // Oppdaterer teksten i feltet
                                            expanded = false

                                            // FORTEL ViewModel hvilket lag som er valgt
                                            // (Du må ha en funksjon i ViewModel som heter setSelectedLayer)
                                            mapViewModel.setSelectedLayer(layer)
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        } else {
                            // Hvis lista er tom, viser vi en liten hjelpetekst i steden
                            ExposedDropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("Laster lag...") },
                                    onClick = { expanded = false })
                            }
                        }
                    }
                }

            }
        }
    }
}
