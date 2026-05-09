package no.uio.ifi.in2000.dylansc.team6project.ui.appinfo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppInfoCardComponent(
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp)) {

        Text (text = "")
        Spacer(modifier = Modifier.padding(16.dp))
        Text (text = "")
    }
}

@Preview
fun AppInfoCardComponentPreview() {}

