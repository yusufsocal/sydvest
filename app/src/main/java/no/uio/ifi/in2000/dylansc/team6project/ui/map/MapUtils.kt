package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.LocationManager
import android.os.Looper
import android.util.Log
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

fun updateWmsLayer(map: MapView, uiState: MapScreenUiState) {
    val layer = uiState.selectedLayer ?: return
    val currentTime = uiState.selectedTime ?: ""

    val oldOverlays = map.overlays.filterIsInstance<TilesOverlay>()
    oldOverlays.forEach { oldOverlay ->
        oldOverlay.onDetach(map)
        map.overlays.remove(oldOverlay)
    }
    map.tileProvider.clearTileCache()

    val newSource = object : XYTileSource(
        "${layer.name}_${currentTime.replace(":", "")}",
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
            val latMin = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * (y + 1) / n))))
            val latMax = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n))))
            val bbox = "$lonMin,$latMin,$lonMax,$latMax"
            val modelParam = uiState.area?.area ?: "meps"

            val url = StringBuilder("https://public-victoria.met.no/wms?")
            url.append("SERVICE=WMS")
            url.append("&VERSION=1.3.0")
            url.append("&REQUEST=GetMap")
            url.append("&LAYERS=${layer.name}")
            url.append("&STYLES=")
            url.append("&CRS=CRS:84")
            url.append("&BBOX=$bbox")
            url.append("&WIDTH=256")
            url.append("&HEIGHT=256")
            url.append("&FORMAT=image/png")
            url.append("&TRANSPARENT=TRUE")
            url.append("&model=$modelParam")
            if (!uiState.selectedTime.isNullOrEmpty()) {
                url.append("&TIME=${uiState.selectedTime}")
            }
            return url.toString()
        }
    }

    val provider = MapTileProviderBasic(map.context, newSource)
    val tilesOverlay = TilesOverlay(provider, map.context).apply {
        loadingBackgroundColor = AndroidColor.TRANSPARENT
        val alphaMatrix = ColorMatrix().apply { setScale(1f, 1f, 1f, 0.5f) }
        setColorFilter(ColorMatrixColorFilter(alphaMatrix))
    }

    Log.e("endrer tid til", "$currentTime")
    map.overlays.add(tilesOverlay)
    map.invalidate()
}

fun drawAlerts(map: MapView, uiState: MapScreenUiState, fareVarsel: Boolean) {
    map.overlays.removeAll { it is FolderOverlay && it.name == "Farevarsler" }

    if (!fareVarsel) {
        map.invalidate()
        return
    }

    val folderOverlay = FolderOverlay().also { it.name = "Farevarsler" }

    uiState.alertList.forEach { features ->
        fun addPolygonToFolder(coords: JsonArray) {
            val points = coords[0].jsonArray.map { coordinatePair ->
                val pair = coordinatePair.jsonArray
                GeoPoint(pair[1].jsonPrimitive.double, pair[0].jsonPrimitive.double)
            }
            if (points.isEmpty()) return

            val polygon = Polygon(map).apply {
                this.points = points.toMutableList()
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

        val coords = features.geometry?.coordinates?.jsonArray
        when {
            features.geometry?.type?.equals("Polygon", true) == true && coords != null ->
                addPolygonToFolder(coords)
            features.geometry?.type?.equals("MultiPolygon", true) == true && coords != null ->
                coords.forEach { addPolygonToFolder(it.jsonArray) }
        }
    }

    map.overlays.add(folderOverlay)
    map.invalidate()
}

fun updateSelectedMarker(mapView: MapView, point: GeoPoint) {
    val existing = mapView.overlays.find { it is Marker && it.title == "selected_location" } as? Marker
    if (existing != null) {
        existing.position = point
    } else {
        mapView.overlays.add(Marker(mapView).apply {
            title = "selected_location"
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(mapView.context, R.drawable.location_pin)
        })
    }
    mapView.invalidate()
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(mapView: MapView, onLocationChanged: (GeoPoint) -> Unit) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(mapView.context)
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .setMinUpdateDistanceMeters(2f)
        .build()

    fusedClient.requestLocationUpdates(request, object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val point = GeoPoint(location.latitude, location.longitude)
            updateUserMarker(mapView, point)
            onLocationChanged(point)
        }
    }, Looper.getMainLooper())
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
            val b = BitmapFactory.decodeResource(mapView.context.resources, R.drawable.location_placeholder)
            icon = Bitmap.createScaledBitmap(b, 40, 40, true).toDrawable(mapView.context.resources)
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
