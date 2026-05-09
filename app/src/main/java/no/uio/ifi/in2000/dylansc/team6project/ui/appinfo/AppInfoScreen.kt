package no.uio.ifi.in2000.dylansc.team6project.ui.appinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ktor.websocket.Frame
import no.uio.ifi.in2000.dylansc.team6project.ui.theme.Team6ProjectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Frame.Text("Om Sydvest") }
            )
        }

    ) {
        innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(appInfoCards) { card -> AppInfoCardComponent(card)}

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppInfoScreenPreview() {
    Team6ProjectTheme {
        AppInfoScreen()
    }
}
