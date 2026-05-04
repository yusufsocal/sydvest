package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
    isCenterActive: Boolean
){
    FilledIconButton(
        modifier = Modifier
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
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
        )
    }
}
