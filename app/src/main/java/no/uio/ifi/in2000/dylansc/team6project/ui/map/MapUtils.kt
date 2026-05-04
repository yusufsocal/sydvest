package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.ApiConstants
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.TilesOverlay
import android.graphics.Color as AndroidColor

fun updateWmsLayer(mapView: MapView, uiState: MapScreenUiState) {
    val layer = uiState.selectedLayer ?: return
    val currentTime = uiState.selectedTime ?: ""

    val oldOverlays = mapView.overlays.filterIsInstance<TilesOverlay>()
    oldOverlays.forEach { it.onDetach(mapView); mapView.overlays.remove(it) }
    mapView.tileProvider.clearTileCache()

    fun makeTilesOverlay(
        layerName: String,
        style: String = "",
        useEPSG3857: Boolean = false
    ): TilesOverlay {
        val source = object : XYTileSource(
            "${layerName}_${currentTime.replace(":", "")}",
            1, 20, 256, ".png",
            arrayOf(ApiConstants.WMS_BASE_URL)
        ) {
            override fun getTileURLString(pTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pTileIndex)
                val x = MapTileIndex.getX(pTileIndex)
                val y = MapTileIndex.getY(pTileIndex)

                val url = StringBuilder(ApiConstants.WMS_BASE_URL)
                url.append("SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap")
                url.append("&LAYERS=$layerName")
                url.append("&STYLES=$style")

                if (useEPSG3857) {
                    val n = Math.pow(2.0, zoom.toDouble())
                    val tileSize = 20037508.34 * 2 / n
                    val xMin = -20037508.34 + x * tileSize
                    val xMax = xMin + tileSize
                    val yMax = 20037508.34 - y * tileSize
                    val yMin = yMax - tileSize
                    url.append("&CRS=EPSG:3857")
                    url.append("&BBOX=$xMin,$yMin,$xMax,$yMax")
                } else {
                    val n = Math.pow(2.0, zoom.toDouble())
                    val lonMin = x / n * 360.0 - 180.0
                    val lonMax = (x + 1) / n * 360.0 - 180.0
                    val latMin =
                        Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * (y + 1) / n))))
                    val latMax = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n))))
                    url.append("&CRS=CRS:84")
                    url.append("&BBOX=$lonMin,$latMin,$lonMax,$latMax")
                }

                url.append("&WIDTH=256&HEIGHT=256")
                url.append("&FORMAT=image/png&TRANSPARENT=TRUE")
                url.append("&model=${uiState.area?.area ?: "meps"}")
                if (!uiState.selectedTime.isNullOrEmpty()) {
                    url.append("&TIME=${java.net.URLEncoder.encode(uiState.selectedTime, "UTF-8")}")
                }
                return url.toString()
            }
        }
        val provider = MapTileProviderBasic(mapView.context, source)
        return TilesOverlay(provider, mapView.context).apply {
            loadingBackgroundColor = AndroidColor.TRANSPARENT
            setColorFilter(ColorMatrixColorFilter(ColorMatrix().apply {
                setScale(
                    1f,
                    1f,
                    1f,
                    0.8f
                )
            }))
        }
    }

    mapView.overlays.add(makeTilesOverlay(layer.name))

    // Legg til vector-laget oppå hvis det er wind speed
    if (layer.title.contains("Wind 10m speed", ignoreCase = true)) {
        val vectorName = layer.name.replace("speed", "vector")
        mapView.overlays.add(makeTilesOverlay(vectorName, style = "wind_barb", useEPSG3857 = true))
    }

    mapView.invalidate()
}

fun drawAlerts(mapView: MapView, uiState: MapScreenUiState, fareVarsel: Boolean) {
    mapView.overlays.removeAll { it is FolderOverlay && it.name == "Farevarsler" }

    if (!fareVarsel) {
        mapView.invalidate()
        return
    }

    val folderOverlay = FolderOverlay().also { it.name = "Farevarsler" }

    uiState.alertList.forEach { features ->
        fun addPolygonToFolder(coords: JsonArray) {
            val points = coords.firstOrNull()?.jsonArray?.mapNotNull { coordinatePair ->
                val pair = coordinatePair.jsonArray
                if (pair.size >= 2) {
                    GeoPoint(pair[1].jsonPrimitive.double, pair[0].jsonPrimitive.double)
                } else null
            }

            points?.let { safePoints ->

                if (safePoints.isEmpty()) return

                val polygon = Polygon(mapView).apply {
                    this.points = safePoints.toMutableList()
                    title = features.properties?.title
                    snippet = features.properties?.description
                    val hex = when (features.properties?.riskMatrixColor) {
                        "Yellow" -> "FFFF00"
                        "Orange" -> "FFA500"
                        "Red" -> "FF0000"
                        else -> "FFFFFF"
                    }
                    fillPaint.color = AndroidColor.parseColor("#80$hex")
                }

                folderOverlay.add(polygon)
            }
        }
        val coords = features.geometry?.coordinates?.jsonArray
        when {
            features.geometry?.type?.equals("Polygon", true) == true && coords != null ->
                addPolygonToFolder(coords)

            features.geometry?.type?.equals("MultiPolygon", true) == true && coords != null ->
                coords.forEach {
                    if (it is JsonArray) {
                        addPolygonToFolder(it)
                    }
                }
            }
    }

    mapView.overlays.add(folderOverlay)
    mapView.invalidate()
}

fun updateSelectedMarker(mapView: MapView, point: GeoPoint) {
    val existing =
        mapView.overlays.find { it is Marker && it.title == "selected_location" } as? Marker
    if (existing != null) {
        existing.position = point
    } else {
        mapView.overlays.add(Marker(mapView).apply {
            title = "selected_location"
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val b = BitmapFactory.decodeResource(
                mapView.context.resources,
                R.drawable.marker_red
            )
            icon = Bitmap.createScaledBitmap(b, 80, 80, true).toDrawable(mapView.context.resources)
        })
    }
    mapView.invalidate()
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(mapView: MapView, onLocationChanged: (GeoPoint) -> Unit): () -> Unit {
    val fusedClient = LocationServices.getFusedLocationProviderClient(mapView.context)
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .setMinUpdateDistanceMeters(2f)
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val point = GeoPoint(location.latitude, location.longitude)
            updateUserMarker(mapView, point)
            onLocationChanged(point)
        }
    }

    fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    return { fusedClient.removeLocationUpdates(callback) }
}

fun updateUserMarker(mapView: MapView, point: GeoPoint) {
    val existing = mapView.overlays.find { it is Marker && it.title == "user_location" } as? Marker
    if (existing != null) {
        existing.position = point
    } else {
        mapView.overlays.add(Marker(mapView).apply {
            title = "user_location"
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            val b = BitmapFactory.decodeResource(
                mapView.context.resources,
                R.drawable.location_blue
            )
            icon = Bitmap.createScaledBitmap(b, 60, 60, true).toDrawable(mapView.context.resources)
        })
    }
    mapView.invalidate()
}

fun removeUserMarker(mapView: MapView) {
    val existing = mapView.overlays.find { it is Marker && it.title == "user_location" } as? Marker
    if (existing != null) {
        mapView.overlays.remove(existing)
        mapView.invalidate()
    }
}

@SuppressLint("MissingPermission")
fun centerMapOnUserLocation(context: Context, mapView: MapView) {
    LocationServices.getFusedLocationProviderClient(context)
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            location?.let { mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude)) }
        }
}

fun checkLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}
