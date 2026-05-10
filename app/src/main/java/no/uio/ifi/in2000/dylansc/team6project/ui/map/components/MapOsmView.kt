package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreenUiState
import no.uio.ifi.in2000.dylansc.team6project.ui.map.centerMapOnUserLocation
import no.uio.ifi.in2000.dylansc.team6project.ui.map.drawAlerts
import no.uio.ifi.in2000.dylansc.team6project.ui.map.removeUserMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateSelectedMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateUserMarker
import no.uio.ifi.in2000.dylansc.team6project.ui.map.updateWmsLayer
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.TilesOverlay

private data class DrawnLayerState(
    val layerName: String?,
    val time: String?,
    val area: AreaData?,
    val dangerAlert: Boolean = false,
    val alertCount: Int = 0
)

/**
 * Composable wrapper around osmdroid's [MapView] for the weather map screen.
 *
 * The map initializes at the last known center (read from the "osmdroid"
 * SharedPreferences) and supports a single weather WMS layer plus alert
 * overlays driven by [uiState]. A long-press on the map places a selected
 * marker and reports the location via [onLocationSelected]; the current
 * map center is persisted to SharedPreferences on every update so it can
 * be restored on the next launch.
 *
 * @param uiState current map state — selected layer, time, area, and
 *   active weather alerts to render.
 * @param granted whether location permission has been granted; when true,
 *   the map auto-centers on the user's position on first load.
 * @param locationServicesEnabled whether OS location services are on;
 *   when false the user marker is removed instead of updated.
 * @param geoLocation the user's current position used to draw the user
 *   marker; ignored when null or when [locationServicesEnabled] is false.
 * @param onMapReady invoked once after the [MapView] is constructed so
 *   the caller can keep a reference for further interaction.
 * @param onLocationSelected called with the long-pressed [GeoPoint] when
 *   the user picks a location on the map.
 * @param onAlertClick invoked when the user taps a weather alert overlay
 *   with the corresponding [AlertFeature], or null if no alert was hit.
 * @param modifier modifier applied to the underlying [AndroidView].
 */

@Composable
fun MapOsmView(
    uiState: MapScreenUiState,
    granted: Boolean,
    locationServicesEnabled: Boolean,
    geoLocation: GeoPoint?,
    onMapReady: (MapView) -> Unit,
    onLocationSelected: (GeoPoint) -> Unit,
    onAlertClick: (AlertFeature?) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setTilesScaledToDpi(true)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                controller.setZoom(10.0)

                val prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                controller.setCenter(
                    GeoPoint(
                        prefs.getFloat("start_lat", 59.9127f).toDouble(),
                        prefs.getFloat("start_lon", 10.7461f).toDouble()
                    )
                )
                minZoomLevel = 3.0
                maxZoomLevel = 18.0
                setScrollableAreaLimitLatitude(85.0, -85.0, height + 1000)
                isFlingEnabled = true
                setVerticalMapRepetitionEnabled(false)
                Configuration.getInstance().cacheMapTileCount = 5000

                if (granted) centerMapOnUserLocation(ctx, this)

                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            updateSelectedMarker(this@apply, it)
                            onLocationSelected(it)
                        }
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

            // We stash the previously drawn state in view.tag so we can diff against
            // new inputs and only redraw layers/alerts when something relevant changed.
            val lastState = view.tag as? DrawnLayerState ?: DrawnLayerState(null, null, null)
            val newState = DrawnLayerState(
                layerName = currentLayer?.name,
                time = currentTime,
                area = currentArea,
                dangerAlert = uiState.dangerAlert,
                alertCount = uiState.alertList.size
            )

            if (newState != lastState) {
                // Update the weather layer if layer, time or area has been changed
                if (newState.layerName != lastState.layerName ||
                    newState.time != lastState.time ||
                    newState.area != lastState.area) {
                    if (currentLayer == null) {
                        view.overlays.removeAll(
                            view.overlays.filterIsInstance<TilesOverlay>()
                        )
                    } else {
                        updateWmsLayer(view, uiState)
                    }
                }

                // Update alerts only if dangerAlert or the number of warnings have changed
                if (newState.dangerAlert != lastState.dangerAlert ||
                    newState.alertCount != lastState.alertCount) {
                    drawAlerts(view, uiState, uiState.dangerAlert, onAlertClick)
                }

                view.tag = newState
            }

            // The users marker is always updated
            val prefs = view.context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            val center = view.mapCenter
            prefs.edit {
                putFloat("current_lat", center.latitude.toFloat())
                putFloat("current_lon", center.longitude.toFloat())
            }

            if (locationServicesEnabled) {
                geoLocation?.let { updateUserMarker(view, it) }
            } else {
                removeUserMarker(view)
            }
        }
    )
}
