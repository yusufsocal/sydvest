//NavHost for å skifte mellom forskjellige skjermer.

package no.uio.ifi.in2000.dylansc.team6project.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    //Alle repositories sendes hit! Husk å oppdatere etter hvert som vi får flere!
) {
    //DataSource og Repository for værdata fra Victoria initialiseres her!
    val wmsDataSource = WMSDataSource()
    val locationRepo = LocationRepository(wmsDataSource)

    //DataSource og Repository for Alerts initialiseres her!
    val alertDataSource = AlertDataSource()
    val alertRepo = AlertRepository(alertDataSource)

    //PROSJEKT CUSTOM AREA -> Foreløpig placeholder for opprettelse av variabel for når
    //hvilken data som skal benyttes velges ->
    var area: AreaData = AreaData.VERDEN
    //

    //Hoister ViewModel opp til NavHost
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModel.provideFactory(locationRepo, alertRepo, area)
    )
    val uiState by mapViewModel.uiState.collectAsState()


    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Rute 1 -> Hjemskjerm
        composable("home") {
            MapScreen(
                uiState,
                mapViewModel
            )
        }
    }
}