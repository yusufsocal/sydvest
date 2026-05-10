package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

// ui/onboarding/OnboardingCarousel.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.dylansc.team6project.R

/**
 * Swipeable onboarding carousel with a skip link, a horizontal pager of
 * [OnboardingPageContent]s, page indicator dots, and back/next buttons.
 *
 * @param pages Pages to display in order.
 * @param onFinish Called when the user taps "Skip" or finishes the last page.
 */
@Composable
fun OnboardingCarousel(
    pages: List<OnboardingPage>,
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex
    val isFirstPage = pagerState.currentPage == 0

    Column(modifier = Modifier.fillMaxSize()) {

        // Top bar: "Hopp over"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            TextButton(onClick = onFinish) {
                Text(
                    "Hopp over",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                )
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
                index = pageIndex,
                total = pages.size,
            )
        }

        // Bottom row: dots + CTA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PageIndicator(
                count = pages.size,
                currentIndex = pagerState.currentPage,
            )

            Button(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                enabled = !isFirstPage,
                shape = RoundedCornerShape(percent = 50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.go_back))
            }

            Button(
                onClick = {
                    if (isLastPage) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                shape = RoundedCornerShape(percent = 50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            ) {
                Text(
                    if (isLastPage) stringResource(pages.last().onboardingButtonLabel)
                    else stringResource(R.string.onboarding_cta_next),
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/** Row of dots showing total pages, with the active dot stretched into a pill. */
@Composable
private fun PageIndicator(count: Int, currentIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            val active = i == currentIndex
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (active) 22.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}