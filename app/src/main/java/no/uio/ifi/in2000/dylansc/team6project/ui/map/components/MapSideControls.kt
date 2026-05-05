package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSideControls(
    onCenterClick: () -> Unit,
    isCenterActive: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // compass / center button (unchanged)
        FilledIconButton(
            modifier = Modifier
                .padding(top = 34.dp)
                .size(55.dp),
            onClick = onCenterClick,
            colors = if (isCenterActive)
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Image(
                painter = painterResource(id = R.drawable.compass_blue),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(CircleShape)
            )
        }

        // zoom in
        FilledIconButton(
            modifier = Modifier.size(55.dp),
            onClick = onZoomIn,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom inn",
                modifier = Modifier.size(30.dp)
            )
        }

        // zoom out
        FilledIconButton(
            modifier = Modifier.size(55.dp),
            onClick = onZoomOut,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom ut",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
