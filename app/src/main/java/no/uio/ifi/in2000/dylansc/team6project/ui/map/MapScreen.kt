//Selve skjermen -> Viser kun hva den får beskjed om fra ViewModel

package no.uio.ifi.in2000.dylansc.team6project.ui.map

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData

@SuppressLint("JavascriptInterface")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapScreenUiState: MapScreenUiState, // Mottar hele staten
    mapViewModel: MapViewModel
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var showAlerts by remember { mutableStateOf(false)}

    val webInterface = remember {
        WebAppInterface { jsonString ->
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                mapViewModel.setSelectedAlert(jsonString)
            }
        }
    }

    Box() {
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
                    this.addJavascriptInterface(webInterface, "AlertBridge")
                    loadUrl("file:///android_asset/map.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (mapScreenUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                //Knapp for å aktivere farevarsler på kartet.
                OutlinedButton( //Kan byttes ut med IconButton
                    onClick = {
                        showAlerts = !showAlerts
                        if (showAlerts) {
                            val jsonString = Json.encodeToString(mapScreenUiState.alertList)
                            webViewRef?.evaluateJavascript(
                                "drawAlerts('${
                                    jsonString.replace(
                                        "'",
                                        "\\'"
                                    )
                                }')", null
                            )
                        } else {
                            webViewRef?.evaluateJavascript("alertVectorSource.clear()", null)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                ) {
                    Text(
                        text = "Farevarsler",
                        color = Color.Black
                    )
                }
                // LISTE MED VÆRLAG (DROPDOWN)
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf("Velg værlag...") }
                var areaData by remember { mutableStateOf("${mapScreenUiState.area}")}
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            label = { Text("Velg værlag (${areaData.lowercase()})") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color(0xFFF7FCFE),
                                unfocusedContainerColor = Color(0xFFF7FCFE)
                            )
                        )

                        // Vi sjekker om det faktisk er noe i lista før vi prøver å vise menyen
                        if (mapScreenUiState.layerList.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                mapScreenUiState.layerList.forEach { layer ->
                                    //PROSJEKT CUSTOM AREA
                                    //Funksjonalitet for å fjerne suffix basert på hvilket område som benyttes
                                    var nyTitle: String = ""
                                    if (mapScreenUiState.area == AreaData.NORDEN) {
                                        nyTitle = layer.title.removeSuffix("in MEPS VDIV")
                                    } else if (mapScreenUiState.area == AreaData.ARKTIS) {
                                        nyTitle = layer.title.removeSuffix("in Arctic VDIV")
                                    } else if (mapScreenUiState.area == AreaData.VERDEN) {
                                        nyTitle = layer.title.removeSuffix("in ECMWF VDIV 1h")
                                    }
                                    //-----------------------
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = nyTitle)
                                        },
                                        onClick = {
                                            selectedOptionText = layer.title
                                            expanded = false
                                            webViewRef?.evaluateJavascript(
                                                "addWmsLayer('${layer.name}')",
                                                null
                                            )
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
            mapScreenUiState.selectedAlertJson?.let { json ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("DETALJER OM VARSEL", style = MaterialTheme.typography.headlineSmall)
                        Text(json) // Her kommer informasjonen fra kartet

                        OutlinedButton(onClick = { mapViewModel.setSelectedAlert(null) }) {
                            Text("Lukk")
                        }
                    }
                }
            }
        }
    }
}

//Brukes for å lese koordinater når man trykker på skjermen.
class WebAppInterface(private val onAlertSelected: (String) -> Unit) {
    @android.webkit.JavascriptInterface
    fun postAlertData(json: String) {
        onAlertSelected(json)
    }
}