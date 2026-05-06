package no.uio.ifi.in2000.dylansc.team6project.model.domene

// Gives tips about one piece of clothing based on the weather conditions.
fun getClothingTips(weather: CurrentWeather): String? {
    return when {
        // They have to be in the right order -- they are checked from the top down.
        // Rain, colder temperatures and wind trumps warmer temperatures.
        weather.temperature < 0 -> "🧥 Vinterjakke"
        weather.rainfall > 0.1 -> "🌂 Paraply eller regnjakke"
        weather.windSpeed > 5.5 -> "🌬️ Vindjakke"
        weather.temperature >= 25 -> "👙 Badetøy"
        weather.temperature >= 18 -> "🩳Shorts"

        else -> "👕 Vanlige klær holder nå"
    }
}