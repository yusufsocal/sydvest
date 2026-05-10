package no.uio.ifi.in2000.dylansc.team6project.ui.appinfo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import no.uio.ifi.in2000.dylansc.team6project.R


/** The cards shown on the "About" screen, in display order. */
val appInfoCards = listOf(

    AppInfoContent(
        header = R.string.onboarding_page_visual_weather_title,
        description = R.string.onboarding_page_visual_weather_description,
        icon = Icons.Outlined.Layers,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_clothing_tips_title,
        description = R.string.onboarding_page_clothing_tips_description,
        icon = Icons.Outlined.Checkroom,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_press_for_data_title,
        description = R.string.onboarding_page_press_for_data_description,
        icon = Icons.Outlined.Place,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_search_for_location_title,
        description = R.string.onboarding_page_search_for_location_description,
        icon = Icons.Outlined.Search,
    ),

    AppInfoContent(
        header = R.string.onboarding_page_danger_alerts_title,
        description = R.string.onboarding_page_danger_alerts_description,
        icon = Icons.Outlined.Warning,
    )
)


/**
 * Content for one info card on the "About" screen.
 *
 * @property header String resource for the title.
 * @property description String resource for the body text.
 * @property icon Icon shown next to the text.
 */
data class AppInfoContent(
    val header: Int,
    val description: Int,
    val icon: ImageVector,
)
