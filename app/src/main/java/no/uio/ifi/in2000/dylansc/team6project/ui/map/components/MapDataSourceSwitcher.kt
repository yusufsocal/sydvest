package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import org.osmdroid.views.MapView


private data class AreaCardContent(
    val area: AreaData, val label: String, val metadata: String, val bullet: List<String>
)

private val areaCardList = listOf(
    AreaCardContent(
        area = AreaData.NORDIC,
        label = "Norden",
        metadata = "MET Norden - 1km - 1t",
        bullet = listOf(
            "Høy oppløsning",
            "Farevarsler",
            "60 timer kvalitetsvarsel",
        )
    ),

    AreaCardContent(
        area = AreaData.WORLD,
        label = "Verden",
        metadata = "ECMWF - 25km - 3t", //TODO: Sjekk at dette er riktig. Eventuelt forenkle
        bullet = listOf(
            "Verdensdekkende",
            "Litt dårligere oppløsning",
            "Ingen farevarsler",
        )
    ),

    AreaCardContent(
        area = AreaData.ARCTIC,
        label = "Arktis",
        metadata = "AROME Arctic - 2.5km - 1t",
        bullet = listOf(
            "Polare områder", "Ingen farevarsler"
        )
    )
)

@Composable
fun MapDataSourceSwitcher(
    changeArea: (String) -> Unit,
    changed: () -> Unit,
    onShowAreaChange: () -> Unit
) {

    var area by remember { mutableStateOf("") }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .background(Color.White.copy(alpha = 0.5f))
            .padding(16.dp)
    ) {
        Card(
            content = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        modifier = Modifier.size(56.dp),
                        contentDescription = null
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "GLOBALE DATA TILGJENGELIG",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Du har zoomet ut av Norden",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))


                    Text(
                        text = "Detaljert værvarsel for resten av verden er ikke tilgjengelig her. Du kan bytte til globalt værvarsel, men her er oppløsningen lavere, og det vises ingen farevarsler.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center

                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Du kan også manuelt bytte mellom globalt og nåværende værvarsel manuelt i innstillinger",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)

                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),

                        ) {
                        areaCardList.forEach { card ->
                            MapAreaDataCard(
                                label = card.label,
                                metadata = card.metadata,
                                bulletList = card.bullet,
                                onCardClick = {
                                    area = card.label
                                },
                                selectedArea = area,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))


                    Button(
                        onClick = {
                            changeArea(area)
                            onShowAreaChange()
                            changed()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Bekreft valg")
                    }

                    Spacer(Modifier.height(4.dp))

                    TextButton(onClick = {
                        changeArea("Norden")
                        onShowAreaChange()
                        changed()
                    }) {
                        Text("Avbryt - behold Norden")
                    }
                }
            },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
        )

    }

}

