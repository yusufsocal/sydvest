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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapBottomScaffold
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapDangerWarningHint
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapOsmView
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSearchField
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSideControls
import org.osmdroid.config.Configuration
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

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
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

    //Lager en lytter for kartsøk: PendingLocation oppdaterer seg om man velger et område
    LaunchedEffect(mapScreenUiState.pendingCenterLocation) {
        if (mapScreenUiState.pendingCenterLocation != null && mapViewRef != null) {
            mapViewRef?.controller?.animateTo(mapScreenUiState.pendingCenterLocation)
            mapViewRef?.controller?.setZoom(14.0)

            // Gi beskjed til ViewModel at vi har flyttet oss,
            // slik at den ikke flytter kartet igjen ved neste rekomposisjon
            mapViewModel.onMapCentered()
        }
    }

    MapOsmView(
        uiState = mapScreenUiState,
        granted = granted,
        locationServicesEnabled = locationServicesEnabled,
        geoLocation = geoLocation,
        onMapReady = { mapViewRef = it },
        modifier = Modifier.fillMaxSize()
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            ) {
                MapSearchField(
                    suggestions = mapScreenUiState.searchSuggestions,
                    onQueryChange = { mapViewModel.onSearchQueryChanged(it) },
                    onSuggestionSelected = { suggestion ->
                        mapViewModel.onSuggestionSelected(suggestion) // Lagrer punktet i staten
                    },
                    onSearchActiveChange = { searchActive = it }
                )
                MapSideControls(
                    onCenterClick = {
                        isCenterActive = !isCenterActive
                        locationServicesEnabled = checkLocationEnabled(context)
                        if (locationServicesEnabled) {
                            mapViewRef?.let { centerMapOnUserLocation(context, it) }
                            mapViewRef?.let {
                                if (it.zoomLevelDouble < 12.0) it.controller.zoomTo(
                                    12.0
                                )
                            }
                        }
                    },
                    isCenterActive = isCenterActive,
                )

                MapDangerWarningHint(
                        show = showHint,
                onDismiss = { showHint = false }
                )
            }

            MapBottomScaffold(
                //MapViewModel
                mapViewModel,
                //MapTimeSliderSection
                sliderPosition = mapScreenUiState.sliderPosition,
                isAnimating = mapScreenUiState.isAnimating,
                onSliderChange = { mapViewModel.updateSliderPosition(it) },
                onAnimateToggle = { mapViewModel.toggleAnimate() },

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
                isFareVarselActive = mapScreenUiState.fareVarsel
            )
        }
    }
}

