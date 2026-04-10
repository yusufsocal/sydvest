//Selve ViewModel, kommuniserer med ulike Repositories og Screens

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.hardware.Camera
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.model.domene.WMSDomain
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class MapScreenUiState(
    //Liste som inneholder egenskaper for lag fra Victoria - XML
    val layerList: List<WMSLayer> = emptyList(),
    //WMS lag
    val selectedLayer: WMSLayer? = null,
    //tid fra WMS lag
    val selectedTime: String? = "",
    //Liste over Farevarsler
    val alertList: List<AlertFeature> = emptyList(),
    //Boolean som sjekker om en side laster eller ikke
    val isLoading: Boolean = true,
    //PROSJEKT CUSTOM AREA - Brukes i sammenheng med Victoria for å bestemme datalag
    //NORDEN, ARKTIS eller VERDEN)
    val area: AreaData? = null,
    //
)

class MapViewModel(
    private val locationRepo: LocationRepository,
    private val alertRepo: AlertRepository,
    private var newArea: AreaData
): ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenUiState())
    val uiState: StateFlow<MapScreenUiState> = _uiState.asStateFlow()

    //Lagrer startområde, hva vi skal gå tilbake til hvis bruker velger under 60t
    private val originalArea: AreaData = newArea
    //Regel for hvilket område som skal bruker
    private val wmsDomain = WMSDomain()

    init {
        viewModelScope.launch {
            try {
                val newLayerList = locationRepo.getArea(newArea) ?: emptyList()
                val newAlertList = alertRepo.getAlertList()
                _uiState.update {
                    it.copy(
                        layerList = newLayerList,
                        alertList = newAlertList,
                        isLoading = false,
                        //PROSJEKT CUSTOM AREA
                        area = newArea
                        //
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ViewModel", "Feil ved henting av data: ${e.message}")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedLayer(layer: WMSLayer) {
        _uiState.update {
            it.copy(
                selectedLayer = layer,
                // Hvis laget har en dimensjon, velges "nå" som starttidspunkt
                selectedTime = if (layer.dimension != null) getNowTimestamp() else ""
            )
        }
    }

    //Returnerer nåværende tispunkt i riktig format for WMS
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNowTimestamp(): String {
        // Bruk OffsetDateTime eller ZonedDateTime for å sikre UTC/Z-format
        val now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
        val rounded = now.withMinute(0).withSecond(0).withNano(0)
        return rounded.format(java.time.format.DateTimeFormatter.ISO_INSTANT)
    }

    //Tar inn et tidspunkt, regner ut hvor mange timer det er fram i tid fra "nå"
    //Bruker for å sjekke om vi har passert 60t
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHoursAhead(time: String): Long {
        val selectedTime = OffsetDateTime.parse(time)
        val now = OffsetDateTime.now(ZoneOffset.UTC)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        return Duration.between(now, selectedTime).toHours()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    //Oppdaterer tiden på værvarselet man ser på basert på sliderens posisjon
    /*fun updateTime(time: String) {
        _uiState.update{
            it.copy(
                selectedTime = time
            )
        }
    }*/

    //Kalles når slideren endrer tidspunkt
    //Lagre valgt tid, finne ut om vi skal bruke originalt område eller bytte til VERDEN
    //Henter ny lagliste hvis området endrer seg
    fun updateTime(time: String) {
        viewModelScope.launch {
            try {
                val hoursAhead = getHoursAhead(time)
                val resolvedArea = wmsDomain.resolveArea(originalArea, hoursAhead)
                val currentArea = _uiState.value.area

                if (resolvedArea != currentArea) {
                    //Hvis området skal endres
                    val newLayerList = locationRepo.getArea(resolvedArea) ?: emptyList()
                    _uiState.update { state ->
                        state.copy(
                            selectedTime = time,
                            area = resolvedArea,
                            layerList = newLayerList,
                            //nullstiller valgt værlag - kan endres seinere. Er for å unngå at man sender med feil værlag til ny modell
                            selectedLayer = null
                        )
                    }
                } else {
                    //Hvis området ikke har endret seg, oppdateres bare tid
                    _uiState.update { state ->
                        state.copy(selectedTime = time)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Feil ved oppdatering av tid/område: ${e.message}")
            }
        }
    }

    companion object {
        fun provideFactory(
            locationRepo: LocationRepository,
            alertRepo: AlertRepository,
            //PROSJEKT CUSTOM AREA
            area: AreaData
            //
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(locationRepo, alertRepo,area) as T
            }
        }
    }

}

