//Selve ViewModel, kommuniserer med ulike Repositories og Screens

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.os.Build
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
import java.time.LocalDate
import java.time.LocalDateTime

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

    //Oppdaterer tiden på værvarselet man ser på basert på sliderens posisjon
    fun updateTime(time: String) {
        _uiState.update{
            it.copy(
                selectedTime = time
            )
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

