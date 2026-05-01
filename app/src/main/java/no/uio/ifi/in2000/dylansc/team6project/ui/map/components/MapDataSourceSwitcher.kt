package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.ui.theme.Team6ProjectTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign


@Composable
fun MapDataSourceSwitcher(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentSource: AreaData,
    availableSources: List<AreaData>,
    onSourceChange: (AreaData) -> Unit,

) {
    var selected by remember { mutableStateOf(AreaData.NORDIC) }
    val hasChanged = selected != AreaData.NORDIC

    if (isVisible) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                modifier = Modifier.size(56.dp),
                contentDescription = "Globale data"
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Du har zoomet ut av Norden",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))


            Text(
                text = "Detaljert værvarsel for resten av verden er ikke tilgjengelig her. Du kan bytte til globalt værvarsel, men her er oppløsningen lavere, og det vises ingen farevarsler.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center

            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MapAreaDataCard(
                    label = "Nåværende",
                    metadata = "MET Norden - 1km - 1t",
                    bulletList = listOf(
                        "Høy oppløsning",
                        "Farevarsler",
                        "60 timer kvalitetsvarsel",
                    ),
                    onCardClick = { /* TODO: select Nordic source */ },
                    modifier = Modifier.weight(1f),
                )
                MapAreaDataCard(
                    label = "Arktis",
                    metadata = "AROME Arctic - 2.5km - 1t",
                    bulletList = listOf(
                        "Polare områder",
                        "Høy oppløsning",
                        "Ingen farevarsler",
                    ),
                    onCardClick = { /* TODO: select Arctic source */ },
                    modifier = Modifier.weight(1f),
                )
                MapAreaDataCard(
                    label = "Global",
                    metadata = "ECMWF - 25km - 3t",
                    bulletList = listOf(
                        "Verdensdekkende",
                        "Grovere oppløsning",
                        "Ingen farevarsler",
                    ),
                    onCardClick = { /* TODO: select Global source */ },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { /* TODO: logikk senere */ },
                enabled = hasChanged,
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

            TextButton(onClick = { /* TODO: logikk senere */ }) {
                Text("Avbryt - behold Norden")
            }
        }
    }
}


@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", fontScale = 1.5f)
@Composable
private fun MapDataSourceSwitcherPreviews() {
    Team6ProjectTheme {
        MapDataSourceSwitcher(
            isVisible = true,
            onDismiss = {},
            currentSource = AreaData.NORDIC,
            availableSources = listOf(AreaData.NORDIC, AreaData.ARCTIC, AreaData.WORLD),
            onSourceChange = {},
        )
    }
}