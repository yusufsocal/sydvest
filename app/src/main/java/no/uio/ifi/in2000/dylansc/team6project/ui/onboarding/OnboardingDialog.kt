package no.uio.ifi.in2000.dylansc.team6project.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun OnboardingDialog(
    pages: List<OnboardingPage>,
    onFinish: () -> Unit,
) {
    Dialog(
        onDismissRequest = { /* force completion */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp))
                // SE om vi trenger denne: .background(MaterialTheme.colorScheme.background)
        ) {
            OnboardingCarousel(
                pages = pages,
                onFinish = onFinish,
            )
        }
    }
}