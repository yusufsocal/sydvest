package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreenUiState
import no.uio.ifi.in2000.dylansc.team6project.ui.map.centerMapOnUserLocation
import no.uio.ifi.in2000.dylansc.team6project.ui.map.drawAlerts
import no.uio.ifi.in2000.dylansc.team6project.ui.map.removeUserMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateSelectedMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateUserMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateWmsLayer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

private data class DrawnLayerState(val layerName: String?, val time: String?, val area: AreaData?)

@Composable
fun MapOsmView(
    uiState: MapScreenUiState,
    granted: Boolean,
    locationServicesEnabled: Boolean,
    geoLocation: GeoPoint?,
    onMapReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setTilesScaledToDpi(true)
                setMultiTouchControls(true)
                controller.setZoom(10.0)

                val prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                controller.setCenter(
                    GeoPoint(
                        prefs.getFloat("start_lat", 59.9127f).toDouble(),
                        prefs.getFloat("start_lon", 10.7461f).toDouble()
                    )
                )
                setMinZoomLevel(3.0)
                setMaxZoomLevel(18.0)
                setScrollableAreaLimitLatitude(85.0, -85.0, height + 1000)
                setFlingEnabled(true)
                setVerticalMapRepetitionEnabled(false)
                Configuration.getInstance().cacheMapTileCount = 5000

                if (granted) centerMapOnUserLocation(ctx, this)

                val mapEventsReceiver = object : org.osmdroid.events.MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        p?.let { updateSelectedMarker(this@apply, it) }
                        return true
                    }
                }
                overlays.add(0, MapEventsOverlay(mapEventsReceiver))
                onMapReady(this)
            }
        },
        modifier = modifier,
        update = { view ->
            val currentLayer = uiState.selectedLayer
            val currentTime = uiState.selectedTime
            val currentArea = uiState.area

            Log.d("UPDATE", "SKJERM OPPDATERES")

            val lastState = view.tag as? DrawnLayerState ?: DrawnLayerState(null, null, null)
            val newState = DrawnLayerState(currentLayer?.name, currentTime, currentArea)

            if (newState != lastState) {
                updateWmsLayer(view, uiState)
                view.tag = newState
            }

            drawAlerts(view, uiState, uiState.fareVarsel)

            val prefs = view.context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            val center = view.mapCenter
            prefs.edit()
                .putFloat("current_lat", center.latitude.toFloat())
                .putFloat("current_lon", center.longitude.toFloat())
                .apply()

            if (locationServicesEnabled) {
                geoLocation?.let { updateUserMarker(view, it) }
            } else {
                removeUserMarker(view)
            }
        }
    )
}
