package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding


/**
 * One page in the onboarding carousel.
 *
 * @property title String resource for the page title.
 * @property description String resource for the body text.
 * @property onboardingButtonLabel String resource for the bottom button label.
 */
data class OnboardingPage(
    val title: Int,
    val description: Int,
    val onboardingButtonLabel: Int,
)