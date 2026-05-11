package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.dylansc.team6project.R
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import androidx.compose.ui.graphics.Color as ComposeColor

/**
 * Search bar for finding and selecting a location on the map.
 *
 * Renders a Material 3 [SearchBar] with a list of location suggestions
 * underneath. While the search is active a full-screen backdrop is drawn
 * behind the bar so tapping outside collapses the search and hides the
 * keyboard. An empty-state row is shown if the query produced no matches.
 *
 * @param suggestions list of location matches for the current query,
 *   provided by the caller (typically from the ViewModel).
 * @param onQueryChange called on every keystroke with the new query
 *   text — the caller is expected to debounce and fetch suggestions.
 * @param onSuggestionSelected called when the user taps a suggestion;
 *   the caller is responsible for moving the map to that location.
 * @param onSearchActiveChange notifies the caller whenever the search
 *   gains or loses focus, so other UI elements can react (e.g. dim or hide).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchField(
    suggestions: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (SearchResult) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    Box(
        modifier = Modifier
            .then(if (expanded) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
    ) {
        if (expanded) {
            //Background behind the search bar
            //Is created only when you are searching for something
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        onClickLabel = stringResource(R.string.close_search),
                        role = Role.Button
                    ) {
                        android.util.Log.d("Search", "Background not working! expanded=$expanded")
                        expanded = false
                        keyboardController?.hide()
                    }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //The search bar itself
            SearchBar(
                inputField = {
                    TextField(
                        value = query,
                        onValueChange = {
                            query = it
                            onQueryChange(it)
                            val isActive = it.isNotEmpty()
                            expanded = isActive
                            onSearchActiveChange(isActive)
                                        },
                        placeholder = { Text("Søk", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_for_location)
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        query = ""
                                        expanded = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear_search)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                expanded = false
                            }
                        ),
                        //Colours for search bar
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ComposeColor.Transparent,
                            unfocusedContainerColor = ComposeColor.Transparent,
                            disabledContainerColor = ComposeColor.Transparent,
                            focusedIndicatorColor = ComposeColor.Transparent,
                            unfocusedIndicatorColor = ComposeColor.Transparent,
                        ),

                        modifier = Modifier.onFocusChanged { focusState ->
                            onSearchActiveChange(focusState.isFocused)
                        }

                        )
                },
                expanded = expanded,
                colors = SearchBarDefaults.colors(
                    MaterialTheme.colorScheme.background
                ),
                onExpandedChange = {
                    expanded = it
                    if (!it) keyboardController?.hide()
                                   },
                shape =  RoundedCornerShape(16f.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {

                        if (suggestions.isEmpty() && query.isNotEmpty()) {
                            item {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            "Fant ingen resultater for \"$query\"\n¯\\_(ツ)_/¯",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        } else {
                            items(suggestions) { suggestion ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            suggestion.name,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        query = suggestion.name
                                        expanded = false
                                        onSearchActiveChange(false)
                                        onSuggestionSelected(suggestion) // Sends the search result back
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }

                                )
                            }
                        }
                    }
                }
            )
        }
    }

}


