package features.browse.ui.components

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.movies_tab
import cinetracker_kmp.composeapp.generated.resources.shows_tab
import common.domain.models.util.MediaType
import org.jetbrains.compose.resources.StringResource

sealed class MediaTypeTabItem(
    val tabResId: StringResource,
    val mediaType: MediaType,
) {
    data object Movies : MediaTypeTabItem(
        tabResId = Res.string.movies_tab,
        mediaType = MediaType.MOVIE,
    )
    data object Shows : MediaTypeTabItem(
        tabResId = Res.string.shows_tab,
        mediaType = MediaType.SHOW,
    )
}
