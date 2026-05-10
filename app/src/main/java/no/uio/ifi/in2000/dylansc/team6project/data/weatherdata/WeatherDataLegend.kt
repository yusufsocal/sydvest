package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import androidx.compose.ui.graphics.Color

/**
 * One row in a map legend.
 *
 * @property color Swatch color shown next to the row.
 * @property label Short value range (e.g. "0-5").
 * @property description Human-readable explanation shown to the user.
 */
data class WeatherDataLegend (val color: Color, val label: String, val description: String)

/** Legend rows for the temperature layer. */
val temperatureLegend = listOf(
    WeatherDataLegend(Color(0xFF6A0DAD), "<−20", "Under −20°C"),
    WeatherDataLegend(Color(0xFF1565C0), "−20-5", "−20 til -5°C"),
    WeatherDataLegend(Color(0xFF90CAF9), "−5-0", "−5 til -0°C"),
    WeatherDataLegend(Color(0xFFFFFFFF), "0-5", "0 til 5°C"),
    WeatherDataLegend(Color(0xFFEFFD5F), "5-17", "5 til 17°C"),
    WeatherDataLegend(Color(0xFFFB8C00), "17-30", "17 til 30°C"),
    WeatherDataLegend(Color(0xFFB71C1C), "> 30", "Over 30°C"),
)

/** Legend rows for the precipitation layer. */
val precipitationLegend= listOf(
    WeatherDataLegend(Color(0xFFFFFFFF), "<0,1", "Ingen nedbør"),
    WeatherDataLegend(Color(0xFFB1E9F7), "0,1-0,5", "Lett nedbør (0,1-0,5 mm)"),
    WeatherDataLegend(Color(0xFF49CBEC), "0,5-2", "Moderat nedbør (0,5–2 mm)"),
    WeatherDataLegend(Color(0xFF1565C0), "2-5", "Kraftig nedbør (2-5 mm)"),
    WeatherDataLegend(Color(0xFF5BB450), "5-10", "Svært kraftig nedbør (5–10 mm)"),
    WeatherDataLegend(Color(0xFFFF5724), "10-25", "Ekstrem nedbør (10-25 mm)"),
    WeatherDataLegend(Color(0xFFD42B53), ">25", "Ekstreme mengder (25+ mm)")
)

/** Legend rows for the wind layer. */
val windLegend = listOf(
    WeatherDataLegend(Color(0xFFB2EBF2), "0–2", "Svak vind (0–5 m/s)"),
    WeatherDataLegend(Color(0xFF9AD49F), "2-5", "Lett bris (5–10 m/s)"),
    WeatherDataLegend(Color(0xFF50B458), "5-8", "Laber bris (10–15 m/s)"),
    WeatherDataLegend(Color(0xFFEFFD5F), "8-11", "Frisk bris (15–20 m/s)"),
    WeatherDataLegend(Color(0xFFFB8C00), "11-17", "stiv kuling (20+ m/s)"),
    WeatherDataLegend(Color(0xFFB71C1C), "17-25", "Sterk kuling (20+ m/s)"),
    WeatherDataLegend(Color(0xFF6A0DAD), ">25", "Storm (20+ m/s)"),
)