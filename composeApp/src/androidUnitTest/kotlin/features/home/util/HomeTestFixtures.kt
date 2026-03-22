package features.home.util

import common.domain.models.content.GenericContent
import features.home.ui.state.HomeState

fun fakeHomeState(vararg items: GenericContent): HomeState = HomeState().apply { trendingList.value = items.toList() }
