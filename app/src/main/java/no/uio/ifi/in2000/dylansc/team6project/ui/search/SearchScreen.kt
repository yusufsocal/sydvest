package no.uio.ifi.in2000.dylansc.team6project.ui.search

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapScreenUiState
import no.uio.ifi.in2000.dylansc.team6project.ui.map.MapViewModel
import no.uio.ifi.in2000.dylansc.team6project.ui.map.components.MapSearchField

@Composable
fun SearchScreen(
    mapScreenUiState: MapScreenUiState,
    mapViewModel: MapViewModel,
    navController: NavController,

    ) {
    MapSearchField(
        suggestions = mapScreenUiState.searchSuggestions,
        onQueryChange = { mapViewModel.onSearchQueryChanged(it) },
        onSuggestionSelected = { suggestion ->
            mapViewModel.onSuggestionSelected(suggestion) // Lagrer punktet i staten
            navController.popBackStack() // Går tilbake til kartet
        },
        onDismiss = { mapViewModel.onSearchDismissed() },
    )
}