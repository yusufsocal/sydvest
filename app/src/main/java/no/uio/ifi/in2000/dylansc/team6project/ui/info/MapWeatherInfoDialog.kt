package no.uio.ifi.in2000.dylansc.team6project.ui.info

    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material.icons.outlined.Air
    import androidx.compose.material.icons.outlined.LocationOn
    import androidx.compose.material.icons.outlined.WaterDrop
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.ui.window.Dialog
    import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather

@Composable
    fun MapWeatherInfoDialog(
        weather: CurrentWeather,
        placeNameFromCoordinates: String? = null,
        onDismiss: () -> Unit,
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = placeNameFromCoordinates
                                        ?: "Lat: %.4f, Lon: %.4f".format(
                                            weather.latitude, weather.longitude
                                        ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(24.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Lukk",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = formatTemperature(weather.temperature),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 64.sp,
                                    fontWeight = FontWeight.Light,
                                )
                                Text(
                                    text = "°C",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 14.dp, start = 2.dp),
                                )
                            }

                            Text(
                                text = "akkurat nå",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        UIWeatherStats(
                            icon = Icons.Outlined.WaterDrop,
                            value = "${"%.1f".format(weather.rainfall)} mm",
                            modifier = Modifier.weight(1f),
                        )
                        UIWeatherStats(
                            icon = Icons.Outlined.Air,
                            value = "${"%.1f".format(weather.windSpeed)} m/s",
                            modifier = Modifier.weight(1f),
                        )


                    }

                    Spacer(Modifier.height(10.dp))

                    InfoClothingTips(weather)
                }
            }
        }
    }



    private fun formatTemperature(value: Double): String =
        if (value % 1.0 == 0.0) "${value.toInt()}" else "%.1f".format(value)