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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSideControls(
    onCenterClick: () -> Unit,
    isCenterActive: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
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
                contentDescription = null,
                tint = if (isCenterActive) {MaterialTheme.colorScheme.onPrimary} else {MaterialTheme.colorScheme.primary},
                modifier = Modifier.size(30.dp).clip(CircleShape)
            )
        }

        // zoom in
        FilledIconButton(
            modifier = Modifier.size(32.dp),
            onClick = onZoomIn,
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Zoom inn",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        // zoom out
        FilledIconButton(
            modifier = Modifier.size(32.dp),
            onClick = onZoomOut,
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircle,
                contentDescription = "Zoom ut",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        FilledIconButton(
            modifier = Modifier.size(32.dp),
            onClick = // TODO: Add functionality
            {},
            colors = IconButtonDefaults.filledIconButtonColors(
                MaterialTheme.colorScheme.onPrimary
            ))

            {
                Icon (
                    imageVector = Icons.Default.Info,
                    contentDescription = "info om appen",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

    }
}
