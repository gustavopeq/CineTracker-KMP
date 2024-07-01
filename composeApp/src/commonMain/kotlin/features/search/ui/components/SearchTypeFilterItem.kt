package features.search.ui.components

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.search_movies_tab
import cinetracker_kmp.composeapp.generated.resources.search_person_tab
import cinetracker_kmp.composeapp.generated.resources.search_shows_tab
import cinetracker_kmp.composeapp.generated.resources.search_top_results_tab
import common.domain.models.util.MediaType
import org.jetbrains.compose.resources.StringResource

sealed class SearchTypeFilterItem(
    val tabResId: StringResource,
    val mediaType: MediaType?,
) {
    data object TopResults : SearchTypeFilterItem(
        tabResId = Res.string.search_top_results_tab,
        mediaType = null,
    )
    data object Movies : SearchTypeFilterItem(
        tabResId = Res.string.search_movies_tab,
        mediaType = MediaType.MOVIE,
    )
    data object Shows : SearchTypeFilterItem(
        tabResId = Res.string.search_shows_tab,
        mediaType = MediaType.SHOW,
    )
    data object Person : SearchTypeFilterItem(
        tabResId = Res.string.search_person_tab,
        mediaType = MediaType.PERSON,
    )
}
