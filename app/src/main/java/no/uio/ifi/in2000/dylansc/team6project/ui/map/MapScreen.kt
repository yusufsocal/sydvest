//Selve skjermen -> Viser kun hva den får beskjed om fra ViewModel

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.TilesOverlay
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import androidx.compose.runtime.LaunchedEffect
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten
    mapViewModel: MapViewModel
) {
    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
/*
    // Launcher for å be om posisjonstillatelse
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            mapViewRef?.let { centerMapOnUserLocation(context, it) }
        }
    }*/

    //Variabler for å sjekke om endringer har forekommet i værlagene.
    var lastDrawnLayerName by remember { mutableStateOf<String?>(null) }
    var lastDrawnTime by remember { mutableStateOf<String?>(null) }
    var lastDrawnArea by remember { mutableStateOf<AreaData?>(null) }

    //Variabel for å velge tidspunkt for værvarsel
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    //Kjører hvis slider endres - sender tidspunkt til ViewModel
    LaunchedEffect(sliderPosition) {
        var now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
        now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())

        mapViewModel.updateTime(now.format(java.time.format.DateTimeFormatter.ISO_INSTANT))
    }

    //Variabel for å sjekke om animasjon er skrudd av eller på - aktiveres med "Animate"-knappen
    var animate by remember { mutableStateOf(false)}

    //Variabel for å sjekke om varevarsler er skrudd av eller på - aktiveres med "Farevarsler"-knappen
    var fareVarsel by remember { mutableStateOf(false)}

    //Variabel for å skrive inn addresse
    var addresse by remember { mutableStateOf("") }

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
                setTilesScaledToDpi(true)
                setMultiTouchControls(true)
                controller.setZoom(10.0)
                val prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                val savedLat = prefs.getFloat("last_lat", 60.90f).toDouble()
                val savedLon = prefs.getFloat("last_lon", 10.75f).toDouble()
                controller.setCenter(GeoPoint(savedLat, savedLon))
                setMinZoomLevel(3.0)
                setMaxZoomLevel(18.0)
                setScrollableAreaLimitLatitude(85.0, -85.0, height + 1000)
                setFlingEnabled(true)
                setVerticalMapRepetitionEnabled(false)
                Configuration.getInstance().cacheMapTileCount = 5000

                mapViewRef = this

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

            //aktiverer / deaktiverer farevarsler
            drawAlerts(view, mapScreenUiState, fareVarsel)

            val prefs = view.context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            val center = view.mapCenter
            prefs.edit()
                .putFloat("current_lat", center.latitude.toFloat())
                .putFloat("current_lon", center.longitude.toFloat())
                .apply()
        }
    )
/*
    // Sjekker og ber om posisjonertillatelse etter at kartet er klart
    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            mapViewRef?.let { centerMapOnUserLocation(context, it) }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }*/

    Box() {

        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(35.dp)
            ){
                //Slider for å velge dager frem i tid
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        // Force snap to integer during movement
                        sliderPosition = newValue.roundToInt().toFloat()
                        var now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                        now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())
                        mapViewModel.updateTime(now.format(java.time.format.DateTimeFormatter.ISO_INSTANT))

                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 239,
                    valueRange = 0f..240f,
                )
                Text(text = sliderPosition.toInt().toString())

                //Knapp for å animere
                OutlinedButton(
                    //Kan byttes ut med IconButton
                    onClick = {
                        animate = !animate
                        CoroutineScope(Dispatchers.Default).launch {
                            while (isActive && animate) { // isActive checks if the coroutine is still running
                                var now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                                now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())
                                sliderPosition += 1
                                mapViewModel.updateTime(now.format(java.time.format.DateTimeFormatter.ISO_INSTANT))
                                delay(500) // timeInterval is in milliseconds (e.g., 5000 for 5s)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
                ) {
                    Text(
                        text = "Animate",
                        color = ComposeColor.Black
                    )
                }

                //Søkefelt for addresse
                TextField(
                    value = addresse,
                    onValueChange = { addresse = it },
                    label = { Text("Stedsnavn") }
                )
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(addresse, 1) { addresses ->
                        val location = addresses.firstOrNull()
                        val lat = location?.latitude?.toDouble() ?: 0.0
                        val lng = location?.longitude?.toDouble() ?: 0.0
                        mapViewRef?.controller?.setCenter(GeoPoint(lat, lng))
                        mapViewRef?.controller?.setZoom(15.0)


                        Log.e("Addresser", "$addresses")
                    }
                }

            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                //Knapp for å aktivere farevarsler på kartet.
                OutlinedButton(
                    //Kan byttes ut med IconButton
                    onClick = {
                        fareVarsel = !fareVarsel
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
                ) {
                    Text(
                        text = "Farevarsler",
                        color = ComposeColor.Black
                    )
                }
                // LISTE MED VÆRLAG (DROPDOWN)
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf("Velg værlag...") }
                //leser direkte fra state hver gang UI oppdateres
                var areaData = mapScreenUiState.area?.toString() ?: ""

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
                                focusedContainerColor = ComposeColor(0xFFF7FCFE),
                                unfocusedContainerColor = ComposeColor(0xFFF7FCFE)
                            )
                        )

                        // Vi sjekker om det faktisk er noe i lista før vi prøver å vise menyen
                        if (mapScreenUiState.layerList.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                    //PROSJEKT CUSTOM AREA
                                    //Funksjonalitet for å fjerne suffix basert på hvilket område som benyttes og legge de i en ny list
                                    val updatedList = mapScreenUiState.layerList.map { layer ->
                                        layer.copy(
                                            title = when (mapScreenUiState.area) {
                                                AreaData.NORDEN -> layer.title.removeSuffix(" in MEPS VDIV")
                                                AreaData.ARKTIS -> layer.title.removeSuffix(" in Arctic VDIV")
                                                AreaData.VERDEN -> layer.title.removeSuffix(" in ECMWF VDIV 1h")
                                                else -> layer.title
                                            }
                                        )
                                    }

                                // Liste med lag som vi vil ha i appen
                                val allowedLayers = setOf(
                                    "Air temperature 2m",
                                    "Precipitation amount 1h",
                                    "Wind 10m speed",
                                    "Wind 10m vector"
                                )

                                // filtrer ut alt som ikke er i allowedLayers og leg dem i dropdown-menyen med ny navn
                                updatedList
                                    .filter { it.title in allowedLayers }
                                    .forEach { layer ->
                                        var nyTitle: String = when (layer.title) {
                                            "Air temperature 2m" -> "Temperature"
                                            "Precipitation amount 1h" -> "Rainfall"
                                            "Wind 10m speed" -> "Wind speed"
                                            "Wind 10m vector" -> "Wind direction"
                                            else -> error("Unexpected layer title: ${layer.title}")
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

//FUNKSJON FOR Å TEGNE VÆRLAG
/* Bruker TilesOverlay for å tegne værlaget på hver "rute" OSM består av. 'wmsSource' består av URL-en
* som genereres for bildet man trenger. Den tar hensyn til alle parametrene i URL-en, slik at man kan
* endre etter behov - eks. TIME kan endres dynamisk.
* */
fun updateWmsLayer(map: MapView, uiState: MapScreenUiState) {
    val layer = uiState.selectedLayer ?: return
    val currentTime = uiState.selectedTime ?: ""

    // 1. Lag den nye kilden (TileSource)
    val newSource = object : XYTileSource(
        "${layer.name}_$currentTime",
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

            // Victoria WMS med CRS:84 forventer minLon, minLat, maxLon, maxLat
            val bbox = "$lonMin,$latMin,$lonMax,$latMax"

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

            if (uiState.selectedTime?.isNotEmpty() ?: true) {
                url.append("&TIME=${uiState.selectedTime}")
            }

            Log.e("WMS_SJEKK", "Henter tile fra: $url")

            return url.toString()
        }
    }

    //Finner og fjerner gamle værlag, og lukker dem!
    val oldOverlays = map.overlays.filterIsInstance<TilesOverlay>()
    oldOverlays.forEach { oldOverlay ->
        // Dette stopper "Too many receivers"
        oldOverlay.onDetach(map)
        map.overlays.remove(oldOverlay)
    }

    //Opprett ny provider og overlay
    val provider = MapTileProviderBasic(map.context, newSource)
    val tilesOverlay = TilesOverlay(provider, map.context).apply {
        loadingBackgroundColor = AndroidColor.TRANSPARENT

        //Alphafilter
        val alphaMatrix = ColorMatrix().apply { setScale(1f, 1f, 1f, 0.7f) }
        setColorFilter(ColorMatrixColorFilter(alphaMatrix))
    }

    // 4. Legg til det nye laget
    map.overlays.add(tilesOverlay)
    map.invalidate()
}

//FUNKSJON FOR Å LAGE FAREVARSLER
/* Bruker 'Polygon'-objekter for å lage interaktive polygoner, og 'FolderOverlay' for å gruppere
* de sammen, slik at flere varsler kan vises samtidig.
* */
fun drawAlerts(map: MapView, uiState: MapScreenUiState, fareVarsel: Boolean){
    map.overlays.removeAll { it is FolderOverlay && it.name == "Farevarsler" }

    if (!fareVarsel) {
        map.invalidate()
        return
    }

    val folderOverlay = FolderOverlay() //Lager en mappe for å holde på farevarslene
    folderOverlay.name = "Farevarsler" //Kaller Overlay-mappen for farevarsler

    uiState.alertList.forEach { features ->
        // Funksjon som kan tegne et enkelt sett med punkter
        fun addPolygonToFolder(coords: JsonArray) {
            val points = mutableListOf<GeoPoint>()
            // GeoJSON-standard: Første liste [0] er alltid den ytre ringen
            val outerRing = coords[0].jsonArray
            outerRing.forEach { coordinatePair ->
                val pair = coordinatePair.jsonArray
                val lon = pair[0].jsonPrimitive.double
                val lat = pair[1].jsonPrimitive.double
                points.add(GeoPoint(lat, lon))
            }

            if (points.isNotEmpty()) {
                val polygon = Polygon(map)
                polygon.points = points
                polygon.title = features.properties?.title //Tittel på farevarselet
                polygon.snippet = features.properties?.description //Beskrivelse av farevarselet
                // Sett farge og info
                val color = when (features.properties?.riskMatrixColor) {
                    "Yellow" -> "FFFF00"
                    "Orange" -> "FFA500"
                    "Red" -> "FF0000"
                    else -> "FFFFFF"
                }
                polygon.fillPaint.color = AndroidColor.parseColor("#80$color")
                folderOverlay.add(polygon)
            }
        }

// 2. Sjekk typen og fordel jobben
        // Inne i drawAlerts:
        val coords = features.geometry?.coordinates?.jsonArray // Bruk .jsonArray her
        if (features.geometry?.type?.equals("Polygon", true) == true && coords != null) {
            addPolygonToFolder(coords)
        } else if (features.geometry?.type?.equals("MultiPolygon", true) == true && coords != null) {
            coords.forEach { singlePolygonCoords ->
                addPolygonToFolder(singlePolygonCoords.jsonArray)
            }
        }
    }
    map.overlays.add(folderOverlay) //Legger til mappen med overlays til kartet
    map.invalidate()

}

/*
// FUNKSJON FOR ANIMASJON
@SuppressLint("MissingPermission")
fun centerMapOnUserLocation(context: Context, mapView: MapView) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                mapView.controller.animateTo(GeoPoint(location.latitude, location.longitude))
            }
        }
}

*/