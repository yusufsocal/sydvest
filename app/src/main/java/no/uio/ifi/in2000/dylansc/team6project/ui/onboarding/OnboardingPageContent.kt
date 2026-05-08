package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.R

// Composable that defines the layout fo the onboarding pages.
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    index: Int,
    total: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        //TODO Illustration placeholder card - not sure if this is something we want yet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // For illustrasjoner: .aspectRatio(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.onboarding_page_counter, index + 1, total),
            style = MaterialTheme.typography.labelMedium,
            // Se om vi trenger denne: color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        // Title
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            // Se om vi trenger denne:  color = MaterialTheme.colorScheme.primary,
            lineHeight = 32.sp,
        )

        Spacer(Modifier.height(12.dp))

        //Body
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 22.sp,
        )
    }
}