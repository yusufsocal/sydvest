package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

// onboarding/OnboardingPage.kt
import androidx.compose.runtime.Composable

data class OnboardingPage(
    val title: String,
    val description: String,
    val ctaLabel: String,
    val illustration: @Composable () -> Unit,
)