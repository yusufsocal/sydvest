package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Layout for a single onboarding page: a "x of y" counter, the title, and the
 * description text.
 *
 * @param page Content shown on this page.
 * @param index Zero-based position of the page.
 * @param total Total number of pages, used for the counter.
 */
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

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.onboarding_page_counter, index + 1, total),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        // Title
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            lineHeight = 32.sp,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 22.sp,
        )

        page.image?.let { imageRes ->
            Spacer(Modifier.height(24.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}