//NavHost for å skifte mellom forskjellige skjermer.

package no.uio.ifi.in2000.dylansc.team6project.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.SearchRepository
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    //Alle repositories sendes hit! Husk å oppdatere etter hvert som vi får flere!
) {
    //DataSource og Repository for værdata fra Victoria initialiseres her!
    val wmsDataSource = WMSDataSourceImpl()
    val locationRepo = LocationRepository(wmsDataSource)

    //DataSource og Repository for Alerts initialiseres her!
    val alertDataSource = AlertDataSourceImpl()
    val alertRepo = AlertRepository(alertDataSource)

    val searchDataSource = SearchDataSourceImpl()
    val searchRepo = SearchRepository(searchDataSource)

    //PROSJEKT CUSTOM AREA -> Foreløpig placeholder for opprettelse av variabel for når
    //hvilken data som skal benyttes velges ->
    var area: AreaData = AreaData.NORDEN
    //

    //Hoister ViewModel opp til NavHost
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModel.provideFactory(locationRepo, alertRepo, searchRepo, area)
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