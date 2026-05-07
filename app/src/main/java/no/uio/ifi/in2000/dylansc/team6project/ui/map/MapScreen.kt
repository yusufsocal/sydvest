package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapDangerWarningHint
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapDangerWarningInfo
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapDataSourceSwitcher
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapOsmView
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSearchField
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSideControls
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapWeatherBottomScaffold
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.Info.MapWeatherInfoDialog
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState,
    mapViewModel: MapViewModel,
) {
    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var granted by remember { mutableStateOf(false) }
    var geoLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationServicesEnabled by remember { mutableStateOf(true) }
    var isCenterActive by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchActive by remember { mutableStateOf(false) }


    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    //Waits until the map is done initialising before the location is checked and is run
    LaunchedEffect(mapViewRef) {
        val mapView = mapViewRef ?: return@LaunchedEffect // waits until the map is ready

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            // Permission is already given — center the map on the users phone position
            centerMapOnUserLocation(context, mapView)
            granted = true
        } else {
            locationPermissionLauncher.launch(
                // Permission is not given — ask the user for permission
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(granted, mapViewRef) {
        var stopUpdates: (() -> Unit)? = null
        if (granted) {
            mapViewRef?.let { view ->
                stopUpdates = startLocationUpdates(view) { newPoint -> geoLocation = newPoint }
            }
        }
        onDispose { stopUpdates?.invoke() }
    }

    DisposableEffect(context) {
        locationServicesEnabled = checkLocationEnabled(context)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                locationServicesEnabled = checkLocationEnabled(ctx)
            }
        }
        context.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        onDispose { context.unregisterReceiver(receiver) }
    }

    //Creates a listener for map search: PendingLocation updates if you choose a location
    LaunchedEffect(mapScreenUiState.pendingCenterLocation) {
        if (mapScreenUiState.pendingCenterLocation != null && mapViewRef != null) {
            mapViewRef?.controller?.animateTo(mapScreenUiState.pendingCenterLocation)
            mapViewRef?.controller?.setZoom(14.0)

            // Gives a message to ViewModel that we have moved,
            // so it does not move the map again the next recomposition
            mapViewModel.onMapCentered()
        }
    }

    DisposableEffect(mapViewRef) {
        val listener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                isCenterActive = false
                return false
            }
            override fun onZoom(event: ZoomEvent?): Boolean {
                isCenterActive = false
                return false
            }
        }
        mapViewRef?.addMapListener(listener)
        onDispose { mapViewRef?.removeMapListener(listener) }
    }

    MapOsmView(
        uiState = mapScreenUiState,
        granted = granted,
        locationServicesEnabled = locationServicesEnabled,
        geoLocation = geoLocation,
        onMapReady = { mapViewRef = it },
        onLocationSelected = { geoPoint ->
            mapViewModel.onLocationSelected(geoPoint.latitude, geoPoint.longitude)
        },
        onAlertClick = { mapViewModel.onAlertClick(it) },
        modifier = Modifier.fillMaxSize()
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (mapScreenUiState.hasError) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kunne ikke laste værdata")
                Button(onClick = { mapViewModel.retry() }) {
                    Text("Prøv igjen")
                }
            }
        } else {

            if (searchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            searchActive = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                )
            }
            Column() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MapSearchField(
                            suggestions = mapScreenUiState.searchSuggestions,
                            onQueryChange = { mapViewModel.onSearchQueryChanged(it) },
                            onSuggestionSelected = { suggestion ->
                                mapViewModel.onSuggestionSelected(suggestion)
                            },
                            onSearchActiveChange = { searchActive = it }
                        )
                    }
                    MapSideControls(
                        onZoomIn = {mapViewRef?.controller?.zoomIn()},
                        onZoomOut = {mapViewRef?.controller?.zoomOut()},
                        onCenterClick = {
                            isCenterActive = true
                            locationServicesEnabled = checkLocationEnabled(context)
                            if (locationServicesEnabled) {
                                mapViewRef?.let { centerMapOnUserLocation(context, it) }
                                mapViewRef?.let {
                                    if (it.zoomLevelDouble < 12.0) it.controller.zoomTo(12.0)

                                }
                            }
                        },
                        isCenterActive = isCenterActive,
                    )
                }

                MapDangerWarningHint(
                    show = showHint,
                    onDismiss = { showHint = false }
                )
            }
        }

            MapWeatherBottomScaffold(
                //MapTimeSliderSection
                sliderPosition = mapScreenUiState.sliderPosition,
                isAnimating = mapScreenUiState.isAnimating,
                onSliderChange = {
                    mapViewModel.updateSliderPosition(it)
                    mapViewModel.updateSliderState() },
                onAnimateToggle = { mapViewModel.toggleAnimate() },
                stepHours = mapScreenUiState.stepHours,
                sliderState = mapScreenUiState.sliderState,

                //MapLayerDropdown
                selectedLayerDisplayName = mapScreenUiState.selectedLayerDisplayName,
                selectedLayer = mapScreenUiState.selectedLayer,         // NY: Send med selve objektet fra uiState,
                displayLayers = mapScreenUiState.displayLayers,
                onLayerSelected = { mapViewModel.setSelectedLayer(it) },

                //Farevarsel
                onFareVarselToggle = {
                    mapViewModel.toggleFareVarsel()
                    if (!mapScreenUiState.fareVarsel) { // if it's currently off, it's about to turn on
                        showHint = true
                    }

                },
                isFareVarselActive = mapScreenUiState.fareVarsel,

                //MapChangeAreaButton
                area = mapScreenUiState.area,
                changeArea = {mapViewModel.updateArea(it)},

            )

            MapDataSourceSwitcher(
                changeArea = {mapViewModel.updateArea(it)},
                mapViewRef,
                wmsLayer = {mapScreenUiState.selectedLayer}
            )

            mapScreenUiState.currentWeather?.let { weather ->
                MapWeatherInfoDialog(
                    weather = weather,
                    placeNameFromCoordinates = mapScreenUiState.placeNameFromCoordinates,
                    onDismiss = { mapViewModel.dismissCurrentWeather() }
                )
            }

            mapScreenUiState.selectedAlert?.let { alert ->
                MapDangerWarningInfo(
                    feature = alert,
                    onDismiss = { mapViewModel.dismissAlert() }
                )
            }
    }
}


