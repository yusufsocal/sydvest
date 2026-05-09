package no.uio.ifi.in2000.dylansc.team6project.model.domene

enum class ClothingTip(val emoji: String, val label: String, val condition: String) {
    WINTER_JACKET("🧥", "Vinterjakke", "Under 0°C"),
    RAIN_JACKET("🌂", "Regnjakke", "Mer enn 0.2 mm nedbør"),
    WIND_JACKET("🌬️", "Vindjakke", "Vind over 5.5 m/s"),
    SWIMWEAR("👙", "Badetøy", "25°C eller varmere"),
    SHORTS("🩳", "Shorts", "18–24°C"),
    NORMAL("👕", "Vanlige klær", "Alt annet (mildt og tørt)")
}

fun getClothingTip(weather: CurrentWeather): ClothingTip = when {
    weather.temperature < 0 -> ClothingTip.WINTER_JACKET
    weather.rainfall > 0.2 -> ClothingTip.RAIN_JACKET
    weather.windSpeed > 5.5 -> ClothingTip.WIND_JACKET
    weather.temperature >= 25 -> ClothingTip.SWIMWEAR
    weather.temperature >= 18 -> ClothingTip.SHORTS
    else -> ClothingTip.NORMAL
}