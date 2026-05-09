package no.uio.ifi.in2000.dylansc.team6project.model.domene

enum class ClothingTip(val emoji: String, val label: String, val condition: String) {
    HEAVY_WINTER("🧣", "Tykk vinterjakke, lue og skjerf ", "Under -10°C"),
    WINTER_JACKET("🧥", "Vinterjakke ", "-10 til 5°C"),
    LIGHT_JACKET("🧥", "Lett jakke ", "5–10°C"),
    SPRING_JACKET("🌷", "Vårjakke ", "10–17°C"),
    RAIN_JACKET("🌧️", "Regnjakke ", "Regn og vind"),
    UMBRELLA("🌂", "Paraply ", "Regn uten mye vind"),
    WIND_JACKET("🌬️", "Vindjakke ", "Vind over 8 m/s"),
    SWIMWEAR("👙", "Badetøy ", "25°C eller varmere"),
    SHORTS("🩳", "Shorts og t-skjorte ", "18–24°C")
}

fun getClothingTip(weather: CurrentWeather): ClothingTip = when {
    weather.temperature < -10 -> ClothingTip.HEAVY_WINTER
    weather.temperature < 5 -> ClothingTip.WINTER_JACKET
    weather.rainfall > 0.2 && weather.windSpeed > 8 -> ClothingTip.RAIN_JACKET
    weather.rainfall > 0.2 -> ClothingTip.UMBRELLA
    weather.windSpeed > 8 -> ClothingTip.WIND_JACKET
    weather.temperature >= 25 -> ClothingTip.SWIMWEAR
    weather.temperature >= 18 -> ClothingTip.SHORTS
    weather.temperature < 10 -> ClothingTip.LIGHT_JACKET
    else -> ClothingTip.SPRING_JACKET
}