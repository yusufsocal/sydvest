package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather
import no.uio.ifi.in2000.dylansc.team6project.ui.theme.Team6ProjectTheme

@Composable
fun MapWeatherInfoCard (
    weather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Temperatur: ${weather.temperature}°C",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Nedbør: ${weather.rainfall} mm",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text (
                text = "Vind: ${weather.windSpeed} m/s",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", fontScale = 1.5f)
@Composable
private fun MapWeatherInfoCardPreview() {
    Team6ProjectTheme {
        MapWeatherInfoCard(
            weather = CurrentWeather(
                temperature = 12.3,
                windSpeed = 4.1,
                rainfall = 0.5
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}