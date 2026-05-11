package no.uio.ifi.in2000.dylansc.team6project.ui.map.components.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.model.domain.ClothingTip

/**
 * Bottom sheet listing every [ClothingTip] and the condition that triggers it.
 *
 * @param onDismiss Called when the user closes the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingTipsExplanationSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                stringResource(R.string.clothing_advise_explanation),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            ClothingTip.entries.forEach { tip ->
                RuleRow(tip)
                Spacer(Modifier.height(8.dp))

            }
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun ClothingTipsExplanationSheetPreview() {
    ClothingTipsExplanationSheet(
        onDismiss = {}
    )
}