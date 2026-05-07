package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.Info.MapAreaDataCard
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.views.MapView


private data class AreaCardContent(
    val area: AreaData, val label: String, val metadata: String, val bullet: List<String>
)


@Composable
fun MapDataSourceSwitcher(
    changeArea: (String) -> Unit,
    changed: () -> Unit,
    onShowAreaChange: () -> Unit,
    mapView: MapView?,
    wmsLayer: () -> WMSLayer?
) {

    val areaCardList = listOf(
        AreaCardContent(
            area = AreaData.NORDIC,
            label = stringResource(R.string.nordic),
            metadata = stringResource(R.string.nordic_datasource_metadata),
            bullet = listOf(
                stringResource(R.string.bullet_high_resolution),
                stringResource(R.string.bullet_warnings),
                stringResource(R.string.bullet_60_hour_forecast),
            )
        ),

        AreaCardContent(
            area = AreaData.WORLD,
            label = stringResource(R.string.worldvide),
            metadata = stringResource(R.string.worldwide_datasource_metadata), //TODO: Sjekk at dette er riktig. Eventuelt forenkle
            bullet = listOf(
                stringResource(R.string.bullet_worldwide),
                stringResource(R.string.bullet_lower_resolution),
                stringResource(R.string.bullet_no_warnings),
            )
        ),

        AreaCardContent(
            area = AreaData.ARCTIC,
            label = stringResource(R.string.arctic),
            metadata = stringResource(R.string.arctic_datasource_metadata),
            bullet = listOf(
                stringResource(R.string.bullet_polar_regions),
                stringResource(R.string.bullet_no_warnings)
            )
        )
    )


    var area by remember { mutableStateOf("") }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
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
                        text = stringResource(R.string.more_data_available),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.zoomed_out_of_nordic),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))


                    Text(
                        text = stringResource(R.string.global_data_explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center

                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.manual_switch_in_settings),
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
                            changed()
                            onShowAreaChange()

                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.confirm_choice))
                    }

                    Spacer(Modifier.height(4.dp))

                    TextButton(onClick = {
                        changeArea("Norden")
                        changed()
                        onShowAreaChange()


                    }) {
                        Text(stringResource(R.string.cancel_keep_nordic))
                    }
                }
            },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
        )

    }

}

