    package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather

@Composable
fun MapWeatherInfoDialog(
    weather: CurrentWeather, placeNameFromCoordinates: String? = null, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(modifier = Modifier.padding(8.dp),
                        text = "Været akkurat nå:",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Lukk")
                    }
                }

                Text(
                    text = placeNameFromCoordinates
                        ?: "Lat: %.4f, Lon: %.4f".format(weather.latitude, weather.longitude),
                    modifier = Modifier.padding(8.dp)
                )

                Card(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "🌡️ Temperatur: ${weather.temperature}°C",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Card(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "☔️ Nedbør: ${weather.rainfall} mm",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Card(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "🍃 Vind: ${weather.windSpeed} m/s",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}
