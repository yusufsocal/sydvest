package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import android.R.attr.contentDescription
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapChangeAreaButton (
    changeArea: (String) -> Unit,
    changed: () -> Unit,

) {

    //ENDRE OMRÅDE FRA KNAPP
    val areaOptions = listOf("Norden", "Arktis", "Verden")

    Card() {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                modifier = Modifier.size(16.dp),
                contentDescription = null
            )
            Text(
                text = "VELG OMRÅDE",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            areaOptions.forEach { option ->
                Button(
                    onClick = {
                        changeArea(option)
                        changed()
                    }
                ) {
                    Text(option)
                }
            }
        }

    }
}