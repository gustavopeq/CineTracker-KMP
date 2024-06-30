package features.browse.ui.components

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import common.domain.util.UiConstants.BROWSE_TAB_ROW_OFFSET_HEIGHT
import features.browse.events.BrowseEvent
import features.browse.ui.BrowseViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollapsingTabRow(
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: BrowseViewModel,
    pagerState: PagerState,
) {
    TopAppBar(
        title = {
            BrowseTypeTabRow(
                viewModel = viewModel,
                pagerState = pagerState,
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BrowseTypeTabRow(
    viewModel: BrowseViewModel,
    pagerState: PagerState,
) {
    val tabList = listOf(MediaTypeTabItem.Movies, MediaTypeTabItem.Shows)
    val selectedTabIndex = pagerState.currentPage

    val coroutineScope = rememberCoroutineScope()

    TabRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp)
            .offset(y = BROWSE_TAB_ROW_OFFSET_HEIGHT.dp),
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabIndicator(
                width = tabPositions[selectedTabIndex].width,
                left = tabPositions[selectedTabIndex].left,
                pagerState = pagerState,
            )
        },
        divider = { },
        containerColor = Color.Transparent,
    ) {
        tabList.forEachIndexed { index, mediaTypeTabItem ->
            BrowseTypeTab(
                text = stringResource(resource = mediaTypeTabItem.tabResId),
                tabIndex = index,
                isSelected = selectedTabIndex == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(it)
                    }
                    viewModel.onEvent(BrowseEvent.UpdateMediaType(mediaTypeTabItem.mediaType))
                },
            )
        }
    }
}

@Composable
private fun BrowseTypeTab(
    text: String,
    tabIndex: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit,
) {
    Tab(
        selected = isSelected,
        onClick = { onClick(tabIndex) },
    ) {
        Text(
            modifier = Modifier.offset(y = (-4).dp),
            text = text,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.tertiary
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabIndicator(
    width: Dp,
    left: Dp,
    pagerState: PagerState,
) {
    val pagerOffset = (pagerState.currentPageOffsetFraction.dp * 175).value.toInt()

    val animateIndicatorOffset by animateIntOffsetAsState(
        targetValue = IntOffset(x = pagerOffset + left.value.toInt(), 0),
        animationSpec = tween(100),
        label = "indicatorAnimation",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .offset(x = animateIndicatorOffset.x.dp - 10.dp)
            .width(width)
            .height(2.dp)
            .background(color = MaterialTheme.colorScheme.secondary),
    )
}
