package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.dylansc.team6project.R
import kotlin.time.Duration.Companion.seconds

private val HINT_VISIBLE_DURATION = 3.seconds
private const val BACKGROUND_ALPHA = 0.7f

@Composable
fun MapDangerWarningHint(
    show: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(show) {
        if (show) {
            delay(HINT_VISIBLE_DURATION)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = BACKGROUND_ALPHA),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.press_dangerbutton_for_information),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontSize = 14.sp
            )
        }
    }
}