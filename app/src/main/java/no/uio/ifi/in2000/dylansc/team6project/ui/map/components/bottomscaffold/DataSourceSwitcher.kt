package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weather.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weather.WMSLayer
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.info.AreaSourceCard
import org.osmdroid.views.MapView


/** Display content for one area option in [MapDataSourceSwitcher]. */
private data class AreaCardContent(
    val area: AreaData,
    val label: String,
    val metadataArea: String,
    val metadataRange: String,
    val bullet: List<String>
)


/**
 * Dialog-style picker that lets the user choose between Nordic, Arctic and
 * World data sources. Shows an [AreaSourceCard] for the active option
 * and a confirm button.
 *
 * @param changeArea Called with the chosen area label.
 * @param changed Called after the area has been changed so callers can refresh state.
 * @param onShowAreaChange Called to close the picker.
 * @param mapView The current map view (unused here but kept for parity with callers).
 * @param wmsLayer Provides the currently selected WMS layer.
 */
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
            metadataArea = stringResource(R.string.nordic_datasource_metadataArea),
            metadataRange = stringResource(R.string.nordic_datasource_metadataRange),
            bullet = listOf(
                stringResource(R.string.bullet_high_resolution),
                stringResource(R.string.bullet_warnings),
                stringResource(R.string.bullet_60_hour_forecast),
            )
        ),

        AreaCardContent(
            area = AreaData.ARCTIC,
            label = stringResource(R.string.arctic),
            metadataArea = stringResource(R.string.arctic_datasource_metadataArea),
            metadataRange = stringResource(R.string.arctic_datasource_metadataRange),
            bullet = listOf(
                stringResource(R.string.bullet_polar_regions),
                stringResource(R.string.bullet_no_warnings)
            )
        ),

        AreaCardContent(
            area = AreaData.WORLD,
            label = stringResource(R.string.worldwide),
            metadataArea = stringResource(R.string.worldwide_datasource_metadataArea),
            metadataRange = stringResource(R.string.worldwide_datasource_metadataRange),
            bullet = listOf(
                stringResource(R.string.bullet_worldwide),
                stringResource(R.string.bullet_lower_resolution),
                stringResource(R.string.bullet_no_warnings),
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
                    Text(
                        text = stringResource(R.string.change_area_question),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))


                    Text(
                        text = stringResource(R.string.change_data_explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center

                    )

                    Spacer(Modifier.height(16.dp))
                    var selectedIndex by remember { mutableIntStateOf(0) }
                    val options = listOf(stringResource(R.string.nordic), stringResource(R.string.arctic), stringResource(R.string.worldwide))

                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { selectedIndex = index
                                    area = label },
                                selected = index == selectedIndex,
                            ) {
                                Text(label)
                            }
                        }
                    }

                    val areaCard = areaCardList[selectedIndex]

                    AreaSourceCard(
                        label = areaCard.label,
                        metadataArea = areaCard.metadataArea,
                        metadataRange = areaCard.metadataRange,
                        bulletList = areaCard.bullet,
                        onCardClick = {},
                        selectedArea = area,

                    )
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
                }
            },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
        )

    }

}

