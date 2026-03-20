//Selve skjermen -> Viser kun hva den får beskjed om fra ViewModel

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten

) {
    // Vi henter ut staten fra ViewModel

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. KARTET (Bakgrunn)
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    // Viktig for å se JS-feil i Logcat!
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(msg: ConsoleMessage?): Boolean {
                            Log.d("MapJS", "${msg?.message()}")
                            return true
                        }
                    }

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl("file:///android_asset/map.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. OVERLEGG (Listen med værlag)
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Vi lager en liten rullemeny øverst på skjermen
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Ikke dekk hele skjermen
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                item {
                    Text(
                        "Tilgjengelige værlag (Norden):",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()
                }

                items(uiState.lagListe) { lag ->
                    Text(
                        text = lag.title, // Her bruker vi "title" fra WmsLayer!
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                Log.d("Map", "Bruker valgte: ${lag.name}")
                                // Her kan du senere sende lag.name til WebView via JavaScript!
                            }, style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}