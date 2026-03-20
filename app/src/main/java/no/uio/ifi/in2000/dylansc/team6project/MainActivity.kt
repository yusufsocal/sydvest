package no.uio.ifi.in2000.dylansc.team6project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.dylansc.team6project.data.WMSDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.map.OpenLayersMapScreen

class MainActivity : ComponentActivity() {

    // 1. Vi instansierer datakildene manuelt her (Dependency Injection)
    private val wmsDataSource = WMSDataSource()
    private val locationRepository = LocationRepository(wmsDataSource)

    // 2. Vi bruker din provideFactory for å lage ViewModel
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModel.provideFactory(locationRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Vi sender ViewModel inn til skjermen din
                    OpenLayersMapScreen(viewModel = mapViewModel)
                }
            }
        }
    }
}