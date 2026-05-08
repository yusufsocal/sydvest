package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        // Illustration placeholder card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Spacer(Modifier.height(28.dp))

        // 01 / 04 counter
        Text(
            text = "%02d / %02d".format(index + 1, total),
            // Se om vi trenger denne: style = MaterialTheme.typography.labelMedium,
            // Se om vi trenger denne: color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        // Title
        Text(
            text = page.title,
            // Se om vi trenger denne: style = MaterialTheme.typography.headlineSmall,
            // Se om vi trenger denne:  color = MaterialTheme.colorScheme.primary,
            lineHeight = 32.sp,
        )

        Spacer(Modifier.height(12.dp))

        // Body
        Text(
            text = page.description,
            // Se om vi trenger denne: style = MaterialTheme.typography.bodyMedium,
           // Se om vi trenger denne:  color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
        )
    }
}