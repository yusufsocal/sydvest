//NavHost for å skifte mellom forskjellige skjermer.

package no.uio.ifi.in2000.dylansc.team6project.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.SearchRepository
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreen
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    //Alle repositories sendes hit! Husk å oppdatere etter hvert som vi får flere!
) {
    //DataSource og Repository for værdata fra Victoria initialiseres her!
    val wmsDataSource = remember { WMSDataSourceImpl() }
    val locationRepo = remember { LocationRepository(wmsDataSource) }

    //DataSource og Repository for Alerts initialiseres her!
    val alertDataSource = remember { AlertDataSourceImpl() }
    val alertRepo = remember { AlertRepository(alertDataSource) }

    val searchDataSource = remember { SearchDataSourceImpl() }
    val searchRepo = remember { SearchRepository(searchDataSource) }

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
        startDestination = "map"
    ) {
        // Rute 1 -> Hjemskjerm
        composable("map") {
            MapScreen(
                uiState,
                mapViewModel
            )
        }
    }
}