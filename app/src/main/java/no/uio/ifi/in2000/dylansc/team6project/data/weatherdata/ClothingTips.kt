package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

import no.uio.ifi.in2000.dylansc.team6project.model.domene.CurrentWeather

// Gir tips om ett klesplagg basert på værforhold
fun getClothingTips(weather: CurrentWeather): String? {
    return when {
        // Dises må være i riktig rekkefølge -- de sjekkes ovenfra og ned
        // Regn, kulde og vind trumfer varmere temperaturer
        weather.temperature < 0 -> "Vinterjakke"
        weather.precipitationMM > 0.1 -> "Regnjakke"
        weather.windSpeed > 8 -> "Vindjakke"
        weather.temperature >= 25 -> "Badetøy"
        weather.temperature >= 18 -> "Shorts"

        else -> null
    }
}