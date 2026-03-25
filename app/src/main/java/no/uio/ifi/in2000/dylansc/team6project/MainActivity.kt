package no.uio.ifi.in2000.dylansc.team6project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.dylansc.team6project.ui.AppNavHost

class MainActivity : ComponentActivity() {

    // Instansiering av datakildene manuelt (Dependency Injection)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Oppretter en NavHost
                    val navController = rememberNavController()
                    AppNavHost(navController)


                }
            }
        }
    }
}