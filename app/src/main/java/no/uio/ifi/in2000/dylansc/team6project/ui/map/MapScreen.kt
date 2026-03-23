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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        // KART
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewRef = this
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
            }, modifier = Modifier.fillMaxSize()
        )

        // LISTE MED VÆRLAG (DROPDOWN)
        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf("Velg værlag...") }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter) // Sørger for at den ligger øverst
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor() // KRITISK: Denne kobler TextField til menyen
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedOptionText,
                        onValueChange = {},
                        label = { Text("Velg værlag (Norden)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color(0xFFF7FCFE),
                            unfocusedContainerColor = Color(0xFFF7FCFE)
                        )
                    )

                    // Vi sjekker om det faktisk er noe i lista før vi prøver å vise menyen
                    if (mapScreenUiState.lagListe.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded, onDismissRequest = { expanded = false }) {
                            mapScreenUiState.lagListe.forEach { lag ->
                                DropdownMenuItem(
                                    text = {
                                    Text(text = lag.title)
                                },
                                    onClick = {
                                        selectedOptionText = lag.title
                                        expanded = false
                                        webViewRef?.evaluateJavascript("addWmsLayer('${lag.name}')", null)
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    } else {
                        // Hvis lista er tom, viser vi en liten hjelpetekst i steden
                        ExposedDropdownMenu(
                            expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Laster lag...") },
                                onClick = { expanded = false })
                        }
                    }
                }
            }
        }
    }
}