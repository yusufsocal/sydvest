package no.uio.ifi.in2000.dylansc.team6project.model.domene

// Gir tips om ett klesplagg basert på værforhold
fun getClothingTips(weather: CurrentWeather): String? {
    return when {
        // Dises må være i riktig rekkefølge -- de sjekkes ovenfra og ned
        // Regn, kulde og vind trumfer varmere temperaturer
        weather.temperature < 0 -> "🧥 Vinterjakke"
        weather.rainfall > 0.1 -> "🌂 Paraply eller regnjakke"
        weather.windSpeed > 5.5 -> "🌬️ Vindjakke"
        weather.temperature >= 25 -> "👙 Badetøy"
        weather.temperature >= 18 -> "🩳Shorts"

        else -> "👕 Vanlige klær holder nå"
    }
}