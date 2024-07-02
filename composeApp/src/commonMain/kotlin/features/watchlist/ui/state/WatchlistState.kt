package features.watchlist.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import common.domain.models.content.GenericContent
import common.domain.models.util.LoadStatusState

class WatchlistState(
    var listItems: MutableState<List<GenericContent>> = mutableStateOf(mutableListOf()),
) : LoadStatusState()
