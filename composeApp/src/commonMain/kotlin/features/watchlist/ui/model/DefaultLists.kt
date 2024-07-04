package features.watchlist.ui.model

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.unknown
import cinetracker_kmp.composeapp.generated.resources.watched_tab
import cinetracker_kmp.composeapp.generated.resources.watchlist_tab
import common.util.Constants.ADD_NEW_TAB_ID
import common.util.capitalized
import org.jetbrains.compose.resources.StringResource

enum class DefaultLists(val listId: Int) {
    WATCHLIST(1),
    WATCHED(2),
    ADD_NEW(ADD_NEW_TAB_ID),
    ;

    override fun toString(): String {
        return super.toString().lowercase().capitalized()
    }
    companion object {
        fun getListById(listId: Int): DefaultLists? {
            return values().firstOrNull { it.listId == listId }
        }
        fun getOtherList(listId: Int): DefaultLists {
            return when (listId) {
                WATCHLIST.listId -> WATCHED
                else -> WATCHLIST
            }
        }

        fun getListLocalizedName(
            list: DefaultLists?,
        ): StringResource {
            return when (list) {
                WATCHLIST -> Res.string.watchlist_tab
                WATCHED -> Res.string.watched_tab
                else -> Res.string.unknown
            }
        }
    }
}
