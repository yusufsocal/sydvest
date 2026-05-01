package no.uio.ifi.in2000.dylansc.team6project.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.dylansc.team6project.data.searchdata.SearchResult
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchField(
    suggestions: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (SearchResult) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }


    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .then(if (expanded) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
            .zIndex(2f)
    ) {
        if (expanded) {
            //Bakgrunn bak søkefeltet
            //Opprettes kun når man søker etter noe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        android.util.Log.d("SØK", "Bakgrunn klikket! expanded=$expanded")
                        expanded = false
                        keyboardController?.hide()
                    }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Selve søkefelt
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
                        placeholder = { Text("Søk", color = MaterialTheme.colorScheme.onSurface) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
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
                                        contentDescription = "Clear search"
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
                        //Farger for søkefeltet
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
                onExpandedChange = {
                    expanded = it
                    if (!it) keyboardController?.hide()
                                   },
                shape =  RoundedCornerShape(16f.dp),
                modifier = Modifier
                    .fillMaxWidth(),
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
                                        onSuggestionSelected(suggestion) // Sender resultatet tilbake
                                        focusRequester.requestFocus()
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


