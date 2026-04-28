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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapBottomControls
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapLayerDropdown
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapOsmView
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSearchField
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapTimeSliderSection
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
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                    modifier = Modifier
                        .padding(35.dp)
                ) {
                    MapTimeSliderSection(
                        sliderPosition = mapScreenUiState.sliderPosition,
                        isAnimating = mapScreenUiState.isAnimating,
                        onSliderChange = { mapViewModel.updateSliderPosition(it) },
                        onAnimateToggle = { mapViewModel.toggleAnimate() }
                    )
                }
            }


            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                MapBottomControls(
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
                    onFareVarselToggle = { mapViewModel.toggleFareVarsel() },
                    isFareVarselActive = mapScreenUiState.fareVarsel
                )

                MapLayerDropdown(
                    selectedLayerDisplayName = mapScreenUiState.selectedLayerDisplayName,
                    areaLabel = mapScreenUiState.area?.toString() ?: "",
                    displayLayers = mapScreenUiState.displayLayers,
                    onLayerSelected = { mapViewModel.setSelectedLayer(it) }
                )
            }
        }
    }
}

