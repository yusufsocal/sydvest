package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import no.uio.ifi.in2000.dylansc.team6project.R


/** The pages shown in the onboarding carousel, in order. */
val onboardingPages = listOf(
    OnboardingPage(
        title = R.string.onboarding_page0_welcome_title,
        description = R.string.onboarding_page0_welcome_description,
        onboardingButtonLabel = R.string.onboarding_cta_next,
    ),
    OnboardingPage(
        title = R.string.onboarding_page_press_for_data_title,
        description = R.string.onboarding_page_press_for_data_description,
        onboardingButtonLabel = R.string.onboarding_cta_next,
    ),

    OnboardingPage(
        title = R.string.onboarding_page_search_for_location_title,
        description = R.string.onboarding_page_search_for_location_description,
        onboardingButtonLabel = R.string.onboarding_cta_next
    ),

    OnboardingPage(
        title = R.string.onboarding_page_clothing_tips_title,
        description = R.string.onboarding_page_clothing_tips_description,
        onboardingButtonLabel = R.string.onboarding_cta_next,
    ),

    OnboardingPage(
        title = R.string.onboarding_page_visual_weather_title,
        description = R.string.onboarding_page_visual_weather_description,
        onboardingButtonLabel = R.string.onboarding_cta_next,
    ),
    OnboardingPage(
        title = R.string.onboarding_page_danger_alerts_title,
        description = R.string.onboarding_page_danger_alerts_description,
        onboardingButtonLabel = R.string.onboarding_cta_get_started,
    ),
)