package no.uio.ifi.in2000.dylansc.team6project

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.dylansc.team6project.ui.AppNavHost
import no.uio.ifi.in2000.dylansc.team6project.ui.theme.Team6ProjectTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Team6ProjectTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Creates a NavHost
                    val navController = rememberNavController()
                    AppNavHost(navController)

                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("last_lat", prefs.getFloat("current_lat", 65.90f))
            .putFloat("last_lon", prefs.getFloat("current_lon", 10.75f))
            .apply()
    }

}
