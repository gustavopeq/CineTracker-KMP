package features.watchlist.domain

import common.domain.models.list.ListItem
import common.domain.models.list.toListItem
import common.domain.models.util.MediaType
import database.repository.DatabaseRepository

class ListInteractor(private val databaseRepository: DatabaseRepository) {

    suspend fun getAllLists(): List<ListItem> = databaseRepository.getAllLists().map { listEntity ->
        listEntity.toListItem()
    }

    suspend fun verifyContentInLists(contentId: Int, mediaType: MediaType): Map<Int, Boolean> {
        val allLists = databaseRepository.getAllLists()
        val contentInListMap = allLists.associate { list ->
            list.listId to false
        }.toMutableMap()

        val result = databaseRepository.searchItems(
            contentId = contentId,
            mediaType = mediaType
        )

        result.forEach { content ->
            contentInListMap[content.listId] = true
        }

        return contentInListMap
    }

    suspend fun toggleWatchlist(currentStatus: Boolean, contentId: Int, mediaType: MediaType, listId: Int) {
        if (currentStatus) {
            databaseRepository.deleteItem(contentId, mediaType, listId)
        } else {
            databaseRepository.insertItem(contentId, mediaType, listId)
        }
    }
}
