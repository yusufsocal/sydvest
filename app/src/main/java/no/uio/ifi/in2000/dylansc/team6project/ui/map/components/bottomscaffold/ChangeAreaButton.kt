package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.bottomscaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Floating card with one button per area (Nordic / Arctic / World) plus an
 * info button that opens the area-change dialog.
 *
 * @param changeArea Called with the chosen area label.
 * @param changed Called after the area has been changed so callers can refresh state.
 * @param onShowAreaChange Called when the user taps the info icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapChangeAreaButton(
    changeArea: (String) -> Unit,
    changed: () -> Unit,
    onShowAreaChange: () -> Unit

    ) {

    //Change area from button
    val areaOptions = listOf(
        stringResource(R.string.nordic),
        stringResource(R.string.arctic),
        stringResource(R.string.worldwide))


    Card(
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.zIndex(1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onShowAreaChange() }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.choose_data_source_area),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            areaOptions.forEach { option ->
                Button(
                    onClick = {
                        changeArea(option)
                        changed()
                    }
                ) {
                    Text(option)
                }
            }
        }

    }
}