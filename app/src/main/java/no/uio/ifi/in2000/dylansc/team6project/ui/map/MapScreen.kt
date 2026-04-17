//Selve skjermen -> Viser kun hva den får beskjed om fra ViewModel

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.TilesOverlay
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten
    mapViewModel: MapViewModel
) {
    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Oppsett for at kartet skal kunne lagre bilder på telefonen
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
    )

    //Variabel for å sjekke om bruker gir tillatelse for å bruke geolokasjon
    var granted by remember { mutableStateOf(false)}

    // Launcher for å be om posisjonstillatelse
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    //Variabler for å sjekke om endringer har forekommet i værlagene.
    var lastDrawnLayerName by remember { mutableStateOf<String?>(null) }
    var lastDrawnTime by remember { mutableStateOf<String?>(null) }
    var lastDrawnArea by remember { mutableStateOf<AreaData?>(null) }
    var lastDrawnFareVarsel by remember { mutableStateOf<Boolean?>(null) }

    //Variabel for å velge tidspunkt for værvarsel
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    //Variabel for å sjekke om animasjon er skrudd av eller på - aktiveres med "Animate"-knappen
    var animate by remember { mutableStateOf(false)}

    //Variabel for å sjekke om varevarsler er skrudd av eller på - aktiveres med "Farevarsler"-knappen
    var fareVarsel by remember { mutableStateOf(false)}

    //Variabel for å skrive inn addresse
    var addresse by remember { mutableStateOf("") }

    //Variabel for å se brukerens posisjon
    var geoLocation by remember { mutableStateOf<GeoPoint?>(null)}

    //Variabel for å sjekke om GPS er på eller ikke
    var locationServicesEnabled by remember { mutableStateOf(true) }

    //Kjører hvis slider endres - sender tidspunkt til ViewModel
    LaunchedEffect(sliderPosition) {
        var now = OffsetDateTime.now(ZoneOffset.UTC)
        now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())
        mapViewModel.updateTime(now.format(DateTimeFormatter.ISO_INSTANT))
    }

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
            granted = true
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    //Starter en lytter for å se om brukeren flytter på seg
    LaunchedEffect(granted, mapViewRef) {
        if (granted) {
            mapViewRef?.let { view ->
                startLocationUpdates(view) { newPoint ->
                    geoLocation = newPoint
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            val currentStatus = checkLocationEnabled(context)
            if (locationServicesEnabled != currentStatus) {
                locationServicesEnabled = currentStatus
                if (!currentStatus) {
                    geoLocation = null // Nullstill posisjonen hvis GPS skrus av
                }
            }
            delay(1000) // Vent 1 sekund før neste sjekk
        }
    }

    // AndroidView brukes for å putte det gamle Android-kartet inn i Compose
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setTilesScaledToDpi(true)
                setMultiTouchControls(true)
                controller.setZoom(10.0)
                val prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                val savedLat = prefs.getFloat("last_lat", 59.9127f).toDouble()
                val savedLon = prefs.getFloat("last_lon", 10.7461f).toDouble()
                controller.setCenter(GeoPoint(savedLat, savedLon))
                setMinZoomLevel(3.0)
                setMaxZoomLevel(18.0)
                setScrollableAreaLimitLatitude(85.0, -85.0, height + 1000)
                setFlingEnabled(true)
                setVerticalMapRepetitionEnabled(false)
                Configuration.getInstance().cacheMapTileCount = 5000
                if (granted) { //Setter brukerens posisjon til deres geolokasjon
                    let { centerMapOnUserLocation(context, it) }
                }
                mapViewRef = this

            }
        },


        modifier = Modifier.fillMaxSize(),

        //UPDATE SCREEN
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

            //aktiverer / deaktiverer farevarsler (bare tegn på nytt hvis noe har endret seg)
            if (fareVarsel != lastDrawnFareVarsel || (fareVarsel && mapScreenUiState.alertList != (view.tag as? List<*>))) {
                drawAlerts(view, mapScreenUiState, fareVarsel)
                lastDrawnFareVarsel = fareVarsel
                view.tag = mapScreenUiState.alertList
            }

            val prefs = view.context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            val center = view.mapCenter
            prefs.edit()
                .putFloat("current_lat", center.latitude.toFloat())
                .putFloat("current_lon", center.longitude.toFloat())
                .apply()

            //aktiverer / deaktiverer markør for GPS
            locationServicesEnabled = checkLocationEnabled(context)
            if (locationServicesEnabled) {
                geoLocation?.let { punkt ->
                    updateUserMarker(view, punkt)
                }
            } else {
                removeUserMarker(view)
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
                    .align(Alignment.TopCenter)
                    .padding(35.dp)
            ){
                //Slider for å velge dager frem i tid
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        // Force snap to integer during movement
                        sliderPosition = newValue.roundToInt().toFloat()
                        var now = OffsetDateTime.now(ZoneOffset.UTC)
                        now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())
                        mapViewModel.updateTime(now.format(DateTimeFormatter.ISO_INSTANT))

                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 239,
                    valueRange = 0f..240f,
                )


                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    //Tekst som viser antall timer frem i tid
                    Text(
                        text = sliderPosition.toInt().toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                color =ComposeColor.White,
                                shape = RoundedCornerShape(12.dp) // Background shape (should match border)
                            )

                            .border(
                                width = 2.dp,
                                color = ComposeColor.Black,
                                shape = RoundedCornerShape(12.dp) // Border shape
                            )
                            .padding(16.dp)
                    )
                    //Knapp for å animere
                    OutlinedButton(
                        //Kan byttes ut med IconButton
                        onClick = {
                            animate = !animate
                            CoroutineScope(Dispatchers.Default).launch {
                                while (isActive && animate && sliderPosition < 240) { // isActive checks if the coroutine is still running
                                    var now = OffsetDateTime.now(ZoneOffset.UTC)
                                    now = now.withMinute(0).withSecond(0).withNano(0).plusHours(sliderPosition.toLong())
                                    sliderPosition += 1
                                    mapViewModel.updateTime(now.format(DateTimeFormatter.ISO_INSTANT))
                                    delay(1000) // Gir flisene tid til å laste inn før neste frame
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
                    ) {//Tekst for animasjonsknapp
                        Text(
                            text = "Animer",
                            color = ComposeColor.Black
                        )
                    }
                }
                //Søkefelt for addresse
                TextField(
                    value = addresse,
                    onValueChange = { addresse = it },
                    label = { Text("Stedsnavn") },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = ComposeColor(0xFFF7FCFE),
                        unfocusedContainerColor = ComposeColor(0xFFF7FCFE)
                    )
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
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    OutlinedButton(
                        onClick = {
                            locationServicesEnabled = checkLocationEnabled(context)
                            if (locationServicesEnabled) {
                                mapViewRef?.let { centerMapOnUserLocation(context, it)}
                                mapViewRef?.zoomLevelDouble?.let {
                                    if (it < 12.0)
                                        mapViewRef?.controller?.zoomTo(12.0)
                                }
                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComposeColor.White),
                    ) {
                        Text(
                            text = "Sentrer",
                            color = ComposeColor.Black
                        )
                    }
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
                }

                // LISTE MED VÆRLAG (DROPDOWN)
                var expanded by remember { mutableStateOf(false) }
                val selectedOptionText = mapScreenUiState.selectedLayer?.title
                    ?.removeSuffix(" in MEPS VDIV")
                    ?.removeSuffix(" in Arctic VDIV")
                    ?.removeSuffix(" in ECMWF VDIV 1h")
                    ?.trim()
                    ?: "Velg værlag..."

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
                                                nyTitle // Oppdaterer teksten i feltet
                                                false

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
            return url.toString()
        }
    }

    // Ta vare på gamle lag før vi legger til det nye
    val oldOverlays = map.overlays.filterIsInstance<TilesOverlay>().toList()

    //Opprett ny provider og overlay
    val provider = MapTileProviderBasic(map.context, newSource)
    val tilesOverlay = TilesOverlay(provider, map.context).apply {
        loadingBackgroundColor = AndroidColor.TRANSPARENT

        //Alphafilter
        val alphaMatrix = ColorMatrix().apply { setScale(1f, 1f, 1f, 0.7f) }
        setColorFilter(ColorMatrixColorFilter(alphaMatrix))
    }

    // Legg til nytt lag FØRST så det nye laget lastes i bakgrunnen
    // mens det gamle laget fortsatt vises (unngår blank flash)
    map.overlays.add(tilesOverlay)
    map.invalidate()

    // Fjern gamle lag etter at det nye har fått tid til å laste inn fliser
    CoroutineScope(Dispatchers.Main).launch {
        delay(400)
        oldOverlays.forEach { oldOverlay ->
            // Dette stopper "Too many receivers"
            oldOverlay.onDetach(map)
            map.overlays.remove(oldOverlay)
        }
        map.invalidate()
    }
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

//FUNKSJON FOR Å VISE BRUKERENS POSISJON
@SuppressLint("MissingPermission")
fun startLocationUpdates(mapView: MapView, onLocationChanged: (GeoPoint) -> Unit) {
    val context = mapView.context
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    //Definer kravene til oppdatering
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000) //Bruk GPS for å få nøyaktig posisjon, hvert 5. sekund
        .setMinUpdateDistanceMeters(2f) // Kartet endres kun hvis man flytter seg mer enn 2 meter
        .build()

    //Lytteren -> Ser om man flytter på seg
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            val newPoint = GeoPoint(location.latitude, location.longitude)

            // Oppdater markøren på kartet
            updateUserMarker(mapView, newPoint)

            // Send den nye posisjonen tilbake til ViewModel/State hvis du trenger den der
            onLocationChanged(newPoint)
        }
    }

    //Start lytting
    fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}

// TEGNER MARKØR
private fun updateUserMarker(mapView: MapView, point: GeoPoint) {
    val context = mapView.context

    // Finn eksisterende markør eller lag en ny
    val existingMarker = mapView.overlays.find { it is Marker && it.title == "user_location" } as? Marker

    if (existingMarker != null) {
        existingMarker.position = point
    } else {
        val marker = Marker(mapView).apply {
            title = "user_location"
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER) // Senter/Senter er ofte bedre for "prikk"

            // Tips: Lagre denne bitmap-en et sted så du slipper å dekode hver gang
            val b = BitmapFactory.decodeResource(context.resources,
                R.drawable.location_placeholder
            )
            val scaled = Bitmap.createScaledBitmap(b, 40, 40, true)
            icon = scaled.toDrawable(context.resources)
        }
        mapView.overlays.add(marker)
    }
    mapView.invalidate()
}

// FJERNER MARKØR
private fun removeUserMarker(mapView: MapView) {
    val existingMarker = mapView.overlays.find { it is Marker && it.title == "user_location" } as? Marker
    if (existingMarker != null) {
        mapView.overlays.remove(existingMarker)
        mapView.invalidate() // Tving kartet til å tegne på nytt uten markøren
    }
}

// FUNKSJON FOR SENTRERING AV POSISJON
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

//FUNKSJON FOR Å SJEKKE OM GPS ER PÅ
fun checkLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return androidx.core.location.LocationManagerCompat.isLocationEnabled(locationManager)
}
