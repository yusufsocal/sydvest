package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchField(
    suggestions: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (SearchResult) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(35.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = suggestions.isNotEmpty(),
                onExpandedChange = {},
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(White, shape = RoundedCornerShape(25.dp))
                    .border(
                        width = 1.dp,
                        color = ComposeColor.Black,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(16.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = { newText ->
                        query = newText
                        onQueryChange(newText)
                    },
                    label = { Text("Stedsnavn") },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = ComposeColor(0xFFF7FCFE),
                        unfocusedContainerColor = ComposeColor(0xFFF7FCFE)
                    ),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = suggestions.isNotEmpty(),
                    onDismissRequest = onDismiss
                ) {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion.name) },
                            onClick = {
                                query = suggestion.name
                                onSuggestionSelected(suggestion)
                            }
                        )
                    }
                }
            }
        }


    }

}
