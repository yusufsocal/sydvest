package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.sidecomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Vertical column of controls floating on the right side of the map:
 * center-on-location, zoom in, zoom out, and a "more" menu.
 *
 * Only the controls needed for actually viewing the map (center, zoom) sit
 * on screen at all times. Everything else the map screen offers — area
 * selection, weather warnings, app info — used to be scattered across
 * several always-visible floating buttons; they're now grouped behind the
 * single "more" button so they don't compete with the map for space until
 * the user actually wants them.
 *
 * @param onCenterClick Called when the user taps the center button.
 * @param isCenterActive Whether the map is currently centered on the user.
 * @param onZoomIn Called when the user taps zoom in.
 * @param onZoomOut Called when the user taps zoom out.
 * @param onInfoClick Called when the user picks "App info" from the menu.
 * @param onShowAreaChange Called when the user picks "Change area" from the menu.
 * @param onDangerAlertToggle Called when the user picks "Weather warnings" from the menu.
 * @param isDangerAlertActive Whether weather warnings are currently shown, reflected in the menu item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSideControls(
    onCenterClick: () -> Unit,
    isCenterActive: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onInfoClick: () -> Unit,
    onShowAreaChange: () -> Unit,
    onDangerAlertToggle: () -> Unit,
    isDangerAlertActive: Boolean,
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

        var showMenu by remember { mutableStateOf(false) }
        Box {
            FilledIconButton(
                modifier = Modifier.size(48.dp),
                onClick = { showMenu = true },
                colors = IconButtonDefaults.filledIconButtonColors(
                    MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.choose_data_source_area)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null)
                    },
                    onClick = {
                        showMenu = false
                        onShowAreaChange()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.weather_warnings)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isDangerAlertActive) MaterialTheme.colorScheme.error
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDangerAlertToggle()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.app_info)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    },
                    onClick = {
                        showMenu = false
                        onInfoClick()
                    }
                )
            }
        }

    }
}
