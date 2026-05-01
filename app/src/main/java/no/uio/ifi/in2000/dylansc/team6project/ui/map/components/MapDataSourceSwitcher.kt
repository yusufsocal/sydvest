package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.ui.theme.Team6ProjectTheme
import kotlin.math.abs


@Composable
fun MapDataSourceSwitcher (
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentSource: AreaData,
    availableSources: List<AreaData>,
    onSourceChange: (AreaData) -> Unit
) {
    if (isVisible) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Globale data"
            )
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