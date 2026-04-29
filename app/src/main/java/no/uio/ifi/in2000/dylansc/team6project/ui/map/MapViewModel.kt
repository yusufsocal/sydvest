package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.SearchRepository
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.model.domene.WMSDomain
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlinx.coroutines.FlowPreview
import org.osmdroid.util.GeoPoint

data class MapScreenUiState(
    val isLoading: Boolean = true,

    val layerList: List<WMSLayer> = emptyList(),
    val selectedLayer: WMSLayer? = null,
    val selectedTime: String? = "",
    val area: AreaData? = null,

    //Søkefelt
    val searchSuggestions: List<SearchResult> = emptyList(),
    val searchQuery: String = "",
    val pendingCenterLocation: GeoPoint? = null,

    //Farevarsel
    val fareVarsel: Boolean = false,
    val alertList: List<AlertFeature> = emptyList(),

    //Slider
    val isAnimating: Boolean = false,
    val sliderPosition: Float = 0f,

    val displayLayers: List<Pair<WMSLayer, String>> = emptyList(),
    val selectedLayerDisplayName: String = "Velg værlag..."
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MapViewModel(
    private val locationRepo: LocationRepository,
    private val alertRepo: AlertRepository,
    private val searchRepo: SearchRepository,
    private var newArea: AreaData
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapScreenUiState())
    val uiState: StateFlow<MapScreenUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val originalArea: AreaData = newArea
    private val wmsDomain = WMSDomain()
    private var animationJob: Job? = null

    init {
        viewModelScope.launch {
            try {
                val newLayerList = locationRepo.getArea(newArea) ?: emptyList()
                val newAlertList = alertRepo.getAlertList()
                _uiState.update { state ->
                    state.copy(
                        layerList = newLayerList,
                        alertList = newAlertList,
                        isLoading = false,
                        area = newArea,
                        displayLayers = computeDisplayLayers(newLayerList, newArea)
                    )
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Feil ved henting av data: ${e.message}")
            }
        }

        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) flowOf(emptyList())
                    else flow { emit(searchRepo.getSuggestions(query)) }
                }
                .catch { e ->
                    Log.e("ViewModel", "Feil ved søk: ${e.message}")
                    emit(emptyList())
                }
                .collect { suggestions ->
                    _uiState.update { it.copy(searchSuggestions = suggestions) }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedLayer(layer: WMSLayer?) {
        _uiState.update { state ->
            val newTime = if (layer?.dimension != null)
                coerceTimeToDimension(getNowTimestamp(), layer.dimension)
            else ""
            val displayName = state.displayLayers
                .find { it.first.name == layer?.name }
                ?.second ?: "Velg værlag..."
            state.copy(
                selectedLayer = layer,
                selectedTime = newTime,
                selectedLayerDisplayName = displayName
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTime(time: String, layer: WMSLayer?):String {
        var newTime = ""
        if (layer != null && layer.dimension != null) {
            newTime = coerceTimeToDimension(time,layer.dimension)
        }
        return newTime
    }

    fun toggleFareVarsel() {
        _uiState.update { it.copy(fareVarsel = !it.fareVarsel) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSliderPosition(position: Float) {
        val snapped = position.roundToInt().toFloat()
        _uiState.update { it.copy(sliderPosition = snapped) }
        val now = OffsetDateTime.now(ZoneOffset.UTC)
            .withMinute(0).withSecond(0).withNano(0)
            .plusHours(snapped.toLong())
        updateTime(now.format(DateTimeFormatter.ISO_INSTANT))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleAnimate() {
        if (_uiState.value.isAnimating) {
            animationJob?.cancel()
            _uiState.update { it.copy(isAnimating = false) }
        } else {
            _uiState.update { it.copy(isAnimating = true) }
            animationJob = viewModelScope.launch {
                while (isActive && _uiState.value.sliderPosition < 240f) {
                    val step = if (_uiState.value.area == AreaData.VERDEN) 3f else 1f
                    val newPos = (_uiState.value.sliderPosition + step).coerceAtMost(240f)
                    updateSliderPosition(newPos)
                    delay(500)
                }
                _uiState.update { it.copy(isAnimating = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onSuggestionSelected(suggestion: SearchResult) {
        _uiState.update {
            it.copy(
                pendingCenterLocation = GeoPoint(suggestion.lat, suggestion.lon),
                searchSuggestions = emptyList()
            )
        }
    }

    fun onMapCentered() {
        _uiState.update { it.copy(pendingCenterLocation = null) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTime(time: String) {
        viewModelScope.launch {
            try {
                val hoursAhead = getHoursAhead(time)
                val resolvedArea = wmsDomain.resolveArea(originalArea, hoursAhead)
                val currentArea = _uiState.value.area

                if (resolvedArea != currentArea) {
                    val newLayerList = locationRepo.getArea(resolvedArea) ?: emptyList()
                    val oldNormalizedTitle =
                        _uiState.value.selectedLayer?.title?.let { normalizeLayerTitle(it) }
                    val matchedLayer =
                        newLayerList.find { normalizeLayerTitle(it.title) == oldNormalizedTitle }
                    val validTime = if (matchedLayer?.dimension != null)
                        coerceTimeToDimension(time, matchedLayer.dimension)
                    else time

                    _uiState.update { state ->
                        state.copy(
                            selectedTime = validTime,
                            area = resolvedArea,
                            layerList = newLayerList,
                            selectedLayer = matchedLayer,
                            displayLayers = computeDisplayLayers(newLayerList, resolvedArea),
                            selectedLayerDisplayName = computeSelectedLayerDisplayName(matchedLayer)
                        )
                    }
                } else {
                    val selectedLayer = _uiState.value.selectedLayer
                    val coercedTime = if (selectedLayer?.dimension != null)
                        coerceTimeToDimension(time, selectedLayer.dimension)
                    else time
                    Log.d("ViewModel", "Slider tid: $time -> Blir til: $coercedTime")
                    _uiState.update { it.copy(selectedTime = coercedTime) }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Feil ved oppdatering av tid/område: ${e.message}")
            }
        }
    }

    private fun computeDisplayLayers(
        layerList: List<WMSLayer>,
        area: AreaData?
    ): List<Pair<WMSLayer, String>> {
        val suffix = when (area) {
            AreaData.NORDEN -> " in MEPS VDIV"
            AreaData.ARKTIS -> " in Arctic VDIV"
            AreaData.VERDEN -> " in ECMWF SFC"
            else -> ""
        }
        val allowedLayers = when (area) {
            AreaData.VERDEN -> setOf(
                "Air temperature 2m",
                "Precipitation amount 3h",
                "Wind 10m speed"
            )

            else -> setOf("Air temperature 2m", "Precipitation amount 1h", "Wind 10m speed")
        }
        val displayNames = mapOf(
            "Air temperature 2m" to "Temperatur",
            "Precipitation amount 1h" to "Nedbør",
            "Precipitation amount 3h" to "Nedbør",
            "Wind 10m speed" to "Vind"
        )
        // Beholder originalen for å teste
        return layerList
            .filter { normalizeLayerTitle(it.title) in allowedLayers }
            .mapNotNull { layer ->
                val normalizedTitle = normalizeLayerTitle(layer.title)
                displayNames[normalizedTitle]?.let { name -> layer to name }
            }
    }

    private fun computeSelectedLayerDisplayName(layer: WMSLayer?): String =
        layer?.title?.let { normalizeLayerTitle(it) } ?: "Velg værlag..."

    private fun normalizeLayerTitle(title: String): String =
        title.removeSuffix(" in MEPS VDIV")
            .removeSuffix(" in Arctic VDIV")
            .removeSuffix(" in ECMWF SFC")
            .trim()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNowTimestamp(): String {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        return now.withMinute(0).withSecond(0).withNano(0)
            .format(DateTimeFormatter.ISO_INSTANT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHoursAhead(time: String): Long {
        val selected = OffsetDateTime.parse(time)
        val now = OffsetDateTime.now(ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0)
        return Duration.between(now, selected).toHours()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun coerceTimeToDimension(requestedTime: String, dimension: String): String {
        return try {
            val parts = dimension.split("/")
            if (parts.size != 3) return requestedTime

            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ")
            val start = OffsetDateTime.parse(parts[0], fmt)
            val end = OffsetDateTime.parse(parts[1], fmt)
            val step = Duration.parse(parts[2])
            val requested = OffsetDateTime.parse(requestedTime)

            val clamped = if (requested.isBefore(start)) start else requested
            val minutesFromStart = Duration.between(start, clamped).toMinutes()
            val stepMinutes = step.toMinutes()
            val steps = if (stepMinutes > 0) minutesFromStart / stepMinutes else 0
            start.plusMinutes(steps * stepMinutes).format(DateTimeFormatter.ISO_INSTANT)
        } catch (e: Exception) {
            requestedTime
        }
    }

    companion object {
        fun provideFactory(
            locationRepo: LocationRepository,
            alertRepo: AlertRepository,
            searchRepo: SearchRepository,
            area: AreaData
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(locationRepo, alertRepo, searchRepo, area) as T
            }
        }
    }
}
