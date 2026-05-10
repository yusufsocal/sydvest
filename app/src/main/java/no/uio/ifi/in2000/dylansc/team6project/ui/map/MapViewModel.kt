package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
import no.uio.ifi.in2000.dylansc.team6project.data.repository.WeatherRepository
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather
import no.uio.ifi.in2000.dylansc.team6project.model.domene.WMSDomain
import org.osmdroid.util.GeoPoint
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class MapScreenUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,

    val currentWeather: CurrentWeather? = null,
    val layerList: List<WMSLayer> = emptyList(),
    val selectedLayer: WMSLayer? = null,
    val selectedTime: String? = "",
    val area: AreaData? = null,

    val selectedArea: AreaData? = null,

    // place name for reversing coordinates to place name
    val placeNameFromCoordinates: String? = null,

    //Search bar
    val searchSuggestions: List<SearchResult> = emptyList(),
    val searchQuery: String = "",
    val pendingCenterLocation: GeoPoint? = null,

    //dangerAlert
    val dangerAlert: Boolean = false,
    val alertList: List<AlertFeature> = emptyList(),

    //Slider
    val isAnimating: Boolean = false,
    val sliderPosition: Float = 0f,
    val stepHours: Int = 1,
    val sliderState: String = "timer",

    // TODO lag forklaring på hva dette er
    val displayLayers: List<Pair<WMSLayer, String>> = emptyList(),
    val selectedLayerDisplayName: String = "Velg værlag...",

    //dangerAlert info
    val selectedAlert: AlertFeature? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MapViewModel(
    private val locationRepo: LocationRepository,
    private val alertRepo: AlertRepository,
    private val searchRepo: SearchRepository,
    private val newArea: AreaData,
    val weatherRepo: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapScreenUiState())
    val uiState: StateFlow<MapScreenUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val originalArea: AreaData = newArea

    private val selectedArea = newArea
    private val wmsDomain = WMSDomain()
    private var animationJob: Job? = null
    private var updateTimeJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val newLayerList = locationRepo.getArea(newArea) ?: emptyList()
                val newAlertList = alertRepo.getAlertList()
                val displayLayers = computeDisplayLayers(newLayerList, newArea)
                _uiState.update { state ->
                    state.copy(
                        layerList = newLayerList,
                        alertList = newAlertList,
                        isLoading = false,
                        hasError = displayLayers.isEmpty(),
                        area = newArea,
                        selectedArea = newArea,
                        displayLayers = displayLayers
                    )
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Feil ved henting av data: ${e.message}")
                _uiState.update { it.copy(isLoading = false, hasError = true) }
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
            val layerDimension = layer?.dimension
            val newTime = if (layerDimension != null)
                coerceTimeToDimension(getNowTimestamp(), layerDimension)
            else ""
            val displayName = state.displayLayers
                .find { it.first.name == layer?.name }
                ?.second ?: "Velg værlag..."
            state.copy(
                selectedLayer = layer,
                selectedTime = newTime,
                selectedLayerDisplayName = displayName,
                stepHours = parseStepHours(layerDimension)
            )
        }
    }

    fun retry() {
        loadData()
    }

    fun toggledangerAlert() {
        _uiState.update { it.copy(dangerAlert = !it.dangerAlert) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSliderPosition(position: Float) {
        val step = _uiState.value.stepHours.coerceAtLeast(1)
        val snapped = ((position / step).roundToInt() * step).toFloat()
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
                    var step = _uiState.value.stepHours.toFloat()
                    if (_uiState.value.sliderPosition >= 24) step = 24f
                    val newPos = (_uiState.value.sliderPosition + step).coerceAtMost(240f)
                    updateSliderPosition(newPos)
                    delay(1500)
                }

                _uiState.update { it.copy(isAnimating = false) }
            }
        }
    }

    fun updateSliderState(){
        if (_uiState.value.sliderPosition >= 23) {
            if (_uiState.value.sliderState != "døgn") _uiState.update { it.copy(sliderState = "døgn") }
        } else {
            if (_uiState.value.sliderState != "timer") _uiState.update { it.copy(sliderState = "timer") }
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
    fun updateArea(areaName: String) {
        viewModelScope.launch {
            try {
                // Find the new area based on the text string from the dropdown
                val userPreferredArea = wmsDomain.changeArea(areaName, originalArea)

                // Check if the area must be forced to WORLD because of current slider-position
                val hoursAhead = _uiState.value.sliderPosition.toLong()
                val resolvedArea = wmsDomain.resolveArea(userPreferredArea, hoursAhead)

                // Get data for the actual area to be shown
                val newLayerList = locationRepo.getArea(resolvedArea) ?: emptyList()

                // Find again the chosen weather layer(e.g. Temperatur) in the new list
                val oldNormalizedTitle =
                    _uiState.value.selectedLayer?.title?.let { normalizeLayerTitle(it) }
                val matchedLayer =
                    newLayerList.find { normalizeLayerTitle(it.title) == oldNormalizedTitle }

                _uiState.update { state ->
                    state.copy(
                        selectedArea = userPreferredArea, // Save the users choice
                        area = resolvedArea,              // What is actually drawn
                        layerList = newLayerList,
                        selectedLayer = matchedLayer,
                        displayLayers = computeDisplayLayers(newLayerList, resolvedArea),
                        selectedLayerDisplayName = computeSelectedLayerDisplayName(matchedLayer),
                        stepHours = parseStepHours(matchedLayer?.dimension)
                    )
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Feil ved oppdatering av område: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTime(time: String) {
        // Cancel the last updateTime-job. Or else a fast slider drag or
        // animation step leave behind more parallel jobs which writes
        // to _uiState in random order and ends with stale time.
        updateTimeJob?.cancel()
        updateTimeJob = viewModelScope.launch {
            try {
                val hoursAhead = getHoursAhead(time)
                val resolvedArea = wmsDomain.resolveArea(_uiState.value.selectedArea, hoursAhead)
                val currentArea = _uiState.value.area
                Log.d("OMRÅDER", "resolvedArea = $resolvedArea - currentArea = $currentArea")

                if (resolvedArea != currentArea) {
                    val newLayerList = locationRepo.getArea(resolvedArea) ?: emptyList()
                    val oldNormalizedTitle =
                        _uiState.value.selectedLayer?.title?.let { normalizeLayerTitle(it) }
                    val matchedLayer =
                        newLayerList.find { normalizeLayerTitle(it.title) == oldNormalizedTitle }
                    val matchedDimension = matchedLayer?.dimension
                    val validTime = if (matchedDimension != null)
                        coerceTimeToDimension(time, matchedDimension)
                    else time

                    _uiState.update { state ->
                        state.copy(
                            selectedTime = validTime,
                            area = resolvedArea,
                            layerList = newLayerList,
                            selectedLayer = matchedLayer,
                            displayLayers = computeDisplayLayers(newLayerList, resolvedArea),
                            selectedLayerDisplayName = computeSelectedLayerDisplayName(matchedLayer),
                            stepHours = parseStepHours(matchedLayer?.dimension)
                        )
                    }
                } else {
                    val selectedLayer = _uiState.value.selectedLayer
                    val selectedDimension = selectedLayer?.dimension
                    val coercedTime = if (selectedDimension != null)
                        coerceTimeToDimension(time, selectedDimension)
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
            AreaData.NORDIC -> " in MEPS VDIV"
            AreaData.ARCTIC -> " in Arctic VDIV"
            AreaData.WORLD -> " in ECMWF SFC"
            else -> ""
        }
        val allowedLayers = when (area) {
            AreaData.WORLD -> setOf(
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
        // Keeps the original for testing purposes
        return layerList
            .filter { normalizeLayerTitle(it.title) in allowedLayers }
            .mapNotNull { layer ->
                val normalizedTitle = normalizeLayerTitle(layer.title)
                displayNames[normalizedTitle]?.let { name -> layer to name }
            }
    }

    private fun computeSelectedLayerDisplayName(layer: WMSLayer?): String {
        if (layer == null) return "Velg værlag..."
        return _uiState.value.displayLayers
            .find { it.first.name == layer.name }
            ?.second ?: normalizeLayerTitle(layer.title)
    }

    private fun normalizeLayerTitle(title: String): String =
        title.removeSuffix(" in MEPS VDIV")
            .removeSuffix(" in Arctic VDIV")
            .removeSuffix(" in ECMWF SFC")
            .trim()

    // Returns the current time as ISO 8601-formatted string
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNowTimestamp(): String {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        return now.withMinute(0).withSecond(0).withNano(0)
            .format(DateTimeFormatter.ISO_INSTANT)
    }

    // Calculates hours ahead from now
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHoursAhead(time: String): Long {
        val selected = OffsetDateTime.parse(time)
        val now = OffsetDateTime.now(ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0)
        return Duration.between(now, selected).toHours()
    }

    // Retrieves the step (in hours) from a WMS Dimension-field.
    // The form is "start/end/PTxH" or "start/end/PTxH/PTyH" when cadence
    // changes is the middle of the prognosis (e.g. ECMWF: PT3H first, then PT6H further ahead).
    // We first choose the first step (parts[2]) — the nicest cadence.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseStepHours(dimension: String?): Int {
        if (dimension == null) return 1
        return try {
            val parts = dimension.split("/")
            if (parts.size < 3) 1
            else Duration.parse(parts[2]).toHours().toInt().coerceAtLeast(1)
        } catch (e: Exception) {
            1
        }
    }

    // Checks that the time is in the right interval
    @RequiresApi(Build.VERSION_CODES.O)
    private fun coerceTimeToDimension(requestedTime: String, dimension: String): String {
        return try {
            val parts = dimension.split("/")
            if (parts.size < 3) return requestedTime

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

    // Function for position marking and getting data from API.
    // It also retrieves place names based on the coordinates.
    fun onLocationSelected(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weather = async { weatherRepo.getCurrentWeather(lat, lon) }
            val placeNameFromCoordinates = async { searchRepo.reverseGeocode(lat, lon) }
            _uiState.update {
                it.copy(currentWeather = weather.await(), placeNameFromCoordinates = placeNameFromCoordinates.await())
            }
        }
    }

    fun dismissCurrentWeather() {
        _uiState.update { it.copy(currentWeather = null, placeNameFromCoordinates = null) }
    }

    fun onAlertClick(feature: AlertFeature?) {
        _uiState.update { it.copy(selectedAlert = feature) }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(selectedAlert = null) }
    }

    companion object {
        fun provideFactory(
            locationRepo: LocationRepository,
            alertRepo: AlertRepository,
            searchRepo: SearchRepository,
            area: AreaData,
            weatherRepo: WeatherRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(locationRepo, alertRepo, searchRepo, area, weatherRepo) as T
            }
        }
    }
}
