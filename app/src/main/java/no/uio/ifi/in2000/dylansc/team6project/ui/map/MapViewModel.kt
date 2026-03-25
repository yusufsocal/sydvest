//Selve ViewModel, kommuniserer med ulike Repositories og Screens

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer

data class MapScreenUiState(
    val lagListe: List<WMSLayer> = emptyList(),
    val isLoading: Boolean = true
)

class MapViewModel(
    private val locationRepo: LocationRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenUiState())
    val uiState: StateFlow<MapScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val vaerlagListe = locationRepo.getArea(AreaData.NORDEN) ?: emptyList()
                _uiState.update {
                    it.copy(
                        lagListe = vaerlagListe,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ViewModel", "Feil ved henting av data: ${e.message}")
            }

        }
    }
    companion object {
        fun provideFactory(
            locationRepo: LocationRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(locationRepo) as T
            }
        }
    }

}

