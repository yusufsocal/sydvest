package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import no.uio.ifi.in2000.dylansc.team6project.R


val onboardingPages = listOf(
    OnboardingPage(
        title = R.string.onboarding_page0_welcome_title,
        description = R.string.onboarding_page0_welcome_description,
        ctaLabel = R.string.onboarding_cta_next,

    ),
    OnboardingPage(
        title = R.string.onboarding_page_press_for_data_title,
        description = R.string.onboarding_page_press_for_data_description,
        ctaLabel = R.string.onboarding_cta_next,
        image = R.drawable.onboarding_side2,
    ),

    OnboardingPage(
        title = R.string.onboarding_page_search_for_location_title,
        description = R.string.onboarding_page_search_for_location_description,
        ctaLabel = R.string.onboarding_cta_next,
        image = R.drawable.onboarding_side3,
    ),

    OnboardingPage(
        title = R.string.onboarding_page_clothing_tips_title,
        description = R.string.onboarding_page_clothing_tips_description,
        ctaLabel = R.string.onboarding_cta_next,
        image = R.drawable.onboarding_side4,
    ),

    OnboardingPage(
        title = R.string.onboarding_page_visual_weather_title,
        description = R.string.onboarding_page_visual_weather_description,
        ctaLabel = R.string.onboarding_cta_next,
        image = R.drawable.onboarding_side5,
    ),
    OnboardingPage(
        title = R.string.onboarding_page_danger_alerts_title,
        description = R.string.onboarding_page_danger_alerts_description,
        ctaLabel = R.string.onboarding_cta_get_started,
        image = R.drawable.onboarding_side6,
    ),
)