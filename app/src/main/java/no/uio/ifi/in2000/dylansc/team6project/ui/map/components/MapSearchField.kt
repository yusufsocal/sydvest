package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchField(
    suggestions: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (SearchResult) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = suggestions.isNotEmpty(),
        onExpandedChange = {}
    ) {
        TextField(
            value = query,
            onValueChange = { newText ->
                query = newText
                onQueryChange(newText)
            },
            label = { Text("Stedsnavn") },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = ComposeColor(0xFFF7FCFE),
                unfocusedContainerColor = ComposeColor(0xFFF7FCFE)
            )
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
