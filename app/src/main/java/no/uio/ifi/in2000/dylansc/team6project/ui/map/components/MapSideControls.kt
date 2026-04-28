package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSideControls(
    onCenterClick: () -> Unit,
    isCenterActive: Boolean
){
    Button(
        onClick = onCenterClick,
        colors = if (isCenterActive)
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        else
            ButtonDefaults.buttonColors(containerColor = Color.White),
    ) {
        Image (
            painter =  painterResource(id = R.drawable.center_location),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable {onCenterClick()}
        )
    }
}