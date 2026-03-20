//NavHost for å skifte mellom forskjellige skjermer.

package no.uio.ifi.in2000.dylansc.team6project.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    //Alle repositories sendes hit! Husk å oppdatere etter hvert som vi får flere!
    locationRepo: LocationRepository,

) {
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModel.provideFactory(locationRepo)
    )
    val uiState by mapViewModel.uiState.collectAsState()


    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Rute 1 -> Hjemskjerm
        composable("home") {
            MapScreen(
                mapScreenUiState = uiState,
            )
        }
    }
}