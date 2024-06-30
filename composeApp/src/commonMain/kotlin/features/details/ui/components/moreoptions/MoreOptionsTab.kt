package features.details.ui.components.moreoptions

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import common.domain.models.content.GenericContent
import common.domain.models.content.Videos
import common.domain.models.util.MediaType
import common.domain.util.UiConstants.MAX_COUNT_MORE_LIKE_THIS_CARDS
import common.ui.components.GridContentList
import common.ui.components.tab.GenericTabRow
import common.ui.components.tab.setupGenericTabs
import features.details.ui.components.moreoptions.MoreOptionsTabItem.MoreLikeThisTab
import features.details.ui.components.moreoptions.MoreOptionsTabItem.VideosTab

@Composable
fun MoreOptionsTab(
    videoList: List<Videos>,
    contentSimilarList: List<GenericContent>,
    goToDetails: (Int, MediaType) -> Unit,
) {
    val availableTabs = mutableListOf<MoreOptionsTabItem>()
    if (contentSimilarList.isNotEmpty()) {
        availableTabs.add(MoreLikeThisTab)
    }
    if (videoList.isNotEmpty()) {
        availableTabs.add(VideosTab)
    }

    val (tabList, selectedTabIndex, updateSelectedTab) = setupGenericTabs(availableTabs)

    if (tabList.isNotEmpty()) {
        Column {
            GenericTabRow(selectedTabIndex.value, tabList, updateSelectedTab)

            when (tabList[selectedTabIndex.value].tabIndex) {
                VideosTab.tabIndex -> {
//                    VideoList(videoList)
                }

                MoreLikeThisTab.tabIndex -> {
                    GridContentList(
                        mediaContentList = contentSimilarList,
                        maxCardsNumber = MAX_COUNT_MORE_LIKE_THIS_CARDS,
                        openContentDetails = goToDetails,
                    )
                }
            }
        }
    }
}
