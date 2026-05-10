package no.uio.ifi.in2000.dylansc.team6project.ui.info


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather
import no.uio.ifi.in2000.dylansc.team6project.model.domene.getClothingTip


@Suppress("AssignedValueIsNeverRead")
@Composable
fun InfoClothingTips (weather: CurrentWeather) {
    val tip = getClothingTip(weather)
    var showSheet by remember { mutableStateOf(false) }

        if (showSheet) {
            ClothingTipsExplanationSheet(
                onDismiss = { showSheet = false }
            )
        }

        Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()) {

        Row (
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.clothing_advise),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )

            IconButton(
                onClick = {showSheet = true}
            ) { Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.clothing_tips_explanation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            }
        }

        Text (
            "${tip.emoji} ${tip.label}",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoClothingTipsPreview() {
    InfoClothingTips(
        weather = CurrentWeather(
            latitude = 59.9139,
            longitude = 10.7522,
            temperature = -2.0,
            windSpeed = 3.0,
            rainfall = 0.0
        )
    )
}