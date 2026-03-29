package features.watchlist.domain

import common.domain.models.list.ListItem
import common.domain.models.list.toListItem
import common.domain.models.util.MediaType
import database.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ListInteractor(private val databaseRepository: DatabaseRepository) {

    fun getAllLists(): Flow<List<ListItem>> = databaseRepository.getAllLists().map { lists ->
        lists.map { it.toListItem() }
    }

    fun verifyContentInLists(contentId: Int, mediaType: MediaType): Flow<Map<Int, Boolean>> = combine(
        databaseRepository.getAllLists(),
        databaseRepository.searchItems(contentId, mediaType)
    ) { allLists, contentEntries ->
        val contentListIds = contentEntries.map { it.listId }.toSet()
        allLists.associate { list ->
            list.listId to (list.listId in contentListIds)
        }
    }

    suspend fun toggleWatchlist(
        currentStatus: Boolean,
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
        title: String = "",
        posterPath: String? = null,
        voteAverage: Float = 0f
    ) {
        if (currentStatus) {
            databaseRepository.deleteItem(contentId, mediaType, listId)
        } else {
            databaseRepository.insertItem(contentId, mediaType, listId, title, posterPath, voteAverage)
        }
    }
}
