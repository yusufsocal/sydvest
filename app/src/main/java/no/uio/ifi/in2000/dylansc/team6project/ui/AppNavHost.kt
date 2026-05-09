//NavHost for å skifte mellom forskjellige skjermer.

package no.uio.ifi.in2000.dylansc.team6project.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.dylansc.team6project.App
import no.uio.ifi.in2000.dylansc.team6project.data.locationforecastdata.LocationforecastDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.SearchRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.WeatherRepository
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSourceImpl
import no.uio.ifi.in2000.dylansc.team6project.ui.appinfo.AppInfoScreen
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreen
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.onboarding.OnboardingCarousel
import no.uio.ifi.in2000.dylansc.team6project.ui.onboarding.onboardingPages

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    //All repositories are sent here! Remember to update as we go along!
) {
    val app = LocalContext.current.applicationContext as App

    //DataSource and Repository for weather data from Victoria are initialised here!
    val wmsDataSource = remember { WMSDataSourceImpl(app.httpClient) }
    val locationRepo = remember { LocationRepository(wmsDataSource) }

    //LoationforecastDataSource and Repository for weather data are initialised here!
    val weatherDataSource = remember { LocationforecastDataSourceImpl(app.jsonHttpClient) }
    val weatherRepo = remember { WeatherRepository(weatherDataSource) }

    //DataSource and Repository for Alerts are initialised here!
    val alertDataSource = remember { AlertDataSourceImpl(app.jsonHttpClient) }
    val alertRepo = remember { AlertRepository(alertDataSource) }

    val searchDataSource = remember { SearchDataSourceImpl(app.httpClient) }
    val searchRepo = remember { SearchRepository(searchDataSource) }

    //PROJECT CUSTOM AREA -> Currently a placeholder for when the user has chosen
    //which data source to use ->
    var area: AreaData = AreaData.NORDIC
    //

    //Hoisting ViewModel up to NavHost
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModel.provideFactory(locationRepo, alertRepo, searchRepo, area, weatherRepo)
    )
    val uiState by mapViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE) }
    val onboardingCompleted = remember { prefs.getBoolean("onboarding_completed", false) }

    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted) "map" else "onboarding"
    ) {
        composable("onboarding") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                OnboardingCarousel(
                    pages = onboardingPages,
                    onFinish = {
                        prefs.edit().putBoolean("onboarding_completed", true).apply()
                        navController.navigate("map") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                )
            }
        }

        // Route 1 -> HomeScreen
        composable("map") {
            MapScreen(
                uiState,
                mapViewModel,
                onInfoClick = { navController.navigate("appInfo") }
            )
        }

        composable("appInfo") {
            AppInfoScreen(
                onBackClick = {navController.popBackStack()},
            )
        }
    }
}