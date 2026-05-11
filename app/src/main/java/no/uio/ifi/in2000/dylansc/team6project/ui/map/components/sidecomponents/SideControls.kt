package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Vertical column of controls floating on the right side of the map:
 * center-on-location, zoom in, zoom out, and an info button.
 *
 * @param onCenterClick Called when the user taps the center button.
 * @param isCenterActive Whether the map is currently centered on the user.
 * @param onZoomIn Called when the user taps zoom in.
 * @param onZoomOut Called when the user taps zoom out.
 * @param onInfoClick Called when the user taps the info button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSideControls(
    onCenterClick: () -> Unit,
    isCenterActive: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onInfoClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // compass / center button (unchanged)
        FilledIconButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(48.dp),
            onClick = onCenterClick,
            colors = if (isCenterActive)
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            else
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.center_on_my_location),
                tint = if (isCenterActive) {MaterialTheme.colorScheme.onPrimary} else {MaterialTheme.colorScheme.primary},
                modifier = Modifier.size(32.dp).clip(CircleShape)
            )
        }

        // zoom in
        FilledIconButton(
            modifier = Modifier.size(48.dp),
            onClick = onZoomIn,
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = stringResource(R.string.zoom_in),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }

        // zoom out
        FilledIconButton(
            modifier = Modifier.size(48.dp),
            onClick = onZoomOut,
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircle,
                contentDescription = stringResource(R.string.zoom_out),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }

        FilledIconButton(
            modifier = Modifier.size(48.dp),
            onClick = onInfoClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            ))

            {
                Icon (
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.app_info),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }

    }
}
