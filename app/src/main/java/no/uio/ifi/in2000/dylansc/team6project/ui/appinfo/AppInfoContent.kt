package no.uio.ifi.in2000.dylansc.team6project.ui.appinfo
import no.uio.ifi.in2000.dylansc.team6project.R
import androidx.annotation.StringRes


val appInfoCards  = listOf(
    AppInfoContent(
        header = R.string.onboarding_page_press_for_data_title,
        description = R.string.onboarding_page_press_for_data_description,
    ),

    AppInfoContent (
        header = R.string.onboarding_page_search_for_location_title,
        description = R.string.onboarding_page_search_for_location_description,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_clothing_tips_title,
        description = R.string.onboarding_page_clothing_tips_description,
    ),

    AppInfoContent (
        header = R.string.onboarding_page_visual_weather_title,
        description = R.string.onboarding_page_visual_weather_description,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_danger_alerts_title,
        description = R.string.onboarding_page_danger_alerts_description,
    )



)


data class AppInfoContent(
    @StringRes val header: Int,
    @StringRes val description: Int,
)