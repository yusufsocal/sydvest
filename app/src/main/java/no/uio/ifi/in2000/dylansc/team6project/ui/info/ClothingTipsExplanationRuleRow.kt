package no.uio.ifi.in2000.dylansc.team6project.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.model.domene.ClothingTip

/**
 * One row in the clothing-tips explanation sheet:
 * emoji on the left, label and condition on the right.
 */
@Composable
fun RuleRow(tip: ClothingTip) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text (
            tip.emoji,
            style = MaterialTheme.typography.titleLarge)
        Column (Modifier
            .padding(start = 16.dp)
            .weight(1f)) {
            Text(
                tip.label,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start)
            Text (
                tip.condition,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }
}