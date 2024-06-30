package features.details.ui.components.moreoptions

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.images_tab
import cinetracker_kmp.composeapp.generated.resources.more_options_similar
import cinetracker_kmp.composeapp.generated.resources.more_options_videos
import cinetracker_kmp.composeapp.generated.resources.movies_tab
import cinetracker_kmp.composeapp.generated.resources.shows_tab
import common.domain.util.Constants.UNSELECTED_OPTION_INDEX
import common.ui.components.tab.TabItem
import org.jetbrains.compose.resources.StringResource

sealed class MoreOptionsTabItem(
    override val tabResId: StringResource,
    override val tabName: String? = "",
    override var tabIndex: Int = UNSELECTED_OPTION_INDEX,
) : TabItem {
    data object VideosTab : MoreOptionsTabItem(
        tabResId = Res.string.more_options_videos,
    )
    data object MoreLikeThisTab : MoreOptionsTabItem(
        tabResId = Res.string.more_options_similar,
    )
    data object MoviesTab : MoreOptionsTabItem(
        tabResId = Res.string.movies_tab,
    )
    data object ShowsTab : MoreOptionsTabItem(
        tabResId = Res.string.shows_tab,
    )
    data object ImagesTab : MoreOptionsTabItem(
        tabResId = Res.string.images_tab,
    )
}
