package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import androidx.annotation.StringRes

data class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val ctaLabel: Int,
)