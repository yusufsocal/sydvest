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

    var lastDrawnLayerName by remember { mutableStateOf<String?>(null) }
    var lastDrawnTime by remember { mutableStateOf<String?>(null) }
    var lastDrawnArea by remember { mutableStateOf<AreaData?>(null) }

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
                controller.setZoom(10.0) // Mengde zoom ved åpning av app
                controller.setCenter(GeoPoint(60.90, 10.75)) //Setter startposisjon -> MÅ ENDRES TIL GEOLOKASJON
                setMinZoomLevel(4.0); // Begrens zoom ut
                setMaxZoomLevel(18.0); // Begrens zoom inn
                setScrollableAreaLimitLatitude(85.0, -85.0, height + 1000) //Begrenser hvor mye man kan skrolle vertikalt og horisontalt på kartet

                setUseDataConnection(true)
                setFlingEnabled(true)

                setVerticalMapRepetitionEnabled(false)

                mapViewRef = this // Lagre referansen her
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            val currentLayer = mapScreenUiState.selectedLayer
            val currentTime = mapScreenUiState.selectedTime
            val currentArea = mapScreenUiState.area

            // Sjekk om vi faktisk trenger å tegne på nytt
            if (currentLayer?.name != lastDrawnLayerName || currentTime != lastDrawnTime || currentArea != lastDrawnArea) {

                updateWmsLayer(view, mapScreenUiState)

                // Oppdater "hukommelsen"
                lastDrawnLayerName = currentLayer?.name
                lastDrawnTime = currentTime
                lastDrawnArea = currentArea
            }
        }
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

                                            // FORTELL ViewModel hvilket lag som er valgt
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

fun updateWmsLayer(map: MapView, uiState: MapScreenUiState) {
    val layer = uiState.selectedLayer ?: return
    // 1. I stedet for å slette ALLE med en gang, finn de gamle lagene først
    val oldLayers = map.overlays.filterIsInstance<TilesOverlay>()

    // Fjern gamle lag for å unngå overlapping
    map.overlays.removeAll { it is TilesOverlay }

    val wmsSource = object : XYTileSource(
        layer.name,
        1, 20, 256, ".png",
        arrayOf("https://public-victoria.met.no/wms?")
    ) {
        override fun getTileURLString(pTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pTileIndex)
            val x = MapTileIndex.getX(pTileIndex)
            val y = MapTileIndex.getY(pTileIndex)

            val n = Math.pow(2.0, zoom.toDouble())
            val lonMin = x / n * 360.0 - 180.0
            val lonMax = (x + 1) / n * 360.0 - 180.0
            val latMinRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * (y + 1) / n)))
            val latMin = Math.toDegrees(latMinRad)
            val latMaxRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)))
            val latMax = Math.toDegrees(latMaxRad)

            // VIKTIG: Victoria WMS med CRS:84 forventer minLon, minLat, maxLon, maxLat
            val bbox = "$lonMin,$latMin,$lonMax,$latMax"

            val formattedTime = uiState.selectedTime.replace("+0000", "Z")

            // Hent model-navnet fra staten (f.eks. "meps", "arome" eller "ec")
            val modelParam = uiState.area?.area ?: "meps"

            val url = StringBuilder("https://public-victoria.met.no/wms?")
            url.append("SERVICE=WMS")
            url.append("&VERSION=1.3.0")
            url.append("&REQUEST=GetMap")
            url.append("&LAYERS=${layer.name}")
            url.append("&STYLES=") // Noen servere krever denne, selv om den er tom
            url.append("&CRS=CRS:84")
            url.append("&BBOX=$bbox")
            url.append("&WIDTH=256")
            url.append("&HEIGHT=256")
            url.append("&FORMAT=image/png")
            url.append("&TRANSPARENT=TRUE")
            url.append("&model=$modelParam") // KRITISK for Victoria

            if (formattedTime.isNotEmpty()) {
                url.append("&TIME=$formattedTime")
            }

            return url.toString()
        }
    }

    val provider = MapTileProviderBasic(map.context, wmsSource)
    val tilesOverlay = TilesOverlay(provider, map.context)

    // Bruk ColorMatrix for alpha-kontroll (0.7f = 70% synlig)
    val alphaMatrix = android.graphics.ColorMatrix().apply {
        setScale(1f, 1f, 1f, 0.7f)
    }
    tilesOverlay.setColorFilter(android.graphics.ColorMatrixColorFilter(alphaMatrix))
    tilesOverlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
    tilesOverlay.loadingLineColor = android.graphics.Color.TRANSPARENT

    // 2. Legg til det NYE laget (det legger seg nå på toppen)
    map.overlays.add(tilesOverlay)
    // 3. Nå kan vi trygt fjerne de GAMLE lagene fra lista
    map.overlays.removeAll(oldLayers)
    // 4. Oppdater skjermen
    map.invalidate()
}