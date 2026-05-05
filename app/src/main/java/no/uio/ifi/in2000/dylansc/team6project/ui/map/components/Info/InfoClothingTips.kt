package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.Info

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather
import no.uio.ifi.in2000.dylansc.team6project.model.domene.getClothingTips

@Composable
fun InfoClothingTips (weather: CurrentWeather) {
    val tip = getClothingTips(weather) ?: ""

    Card(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Klestips:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(8.dp)
        )

        Text (tip,
            modifier = Modifier.padding (8.dp))
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