package features.watchlist.domain

import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.util.Constants
import database.model.ContentEntity
import database.repository.DatabaseRepository
import database.repository.PersonalRatingRepository
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WatchlistInteractor(
    private val databaseRepository: DatabaseRepository,
    private val personalRatingRepository: PersonalRatingRepository
) {

    private var lastRemovedItem: ContentEntity? = null
    private var lastMovedListId: Int? = null

    fun getListContentWithRatings(listId: Int): Flow<List<GenericContent>> = combine(
        databaseRepository.getAllItemsByListId(listId),
        personalRatingRepository.getAllRatings()
    ) { entities, ratingsMap ->
        mapEntitiesToGenericContent(entities, ratingsMap)
    }

    fun mapEntitiesToGenericContent(
        entities: List<ContentEntity>,
        ratingsMap: Map<Int, Float> = emptyMap()
    ): List<GenericContent> = entities.filter { it.posterPath != null }.map { entity ->
        GenericContent(
            id = entity.contentId,
            name = entity.title,
            rating = entity.voteAverage.toDouble(),
            overview = "",
            posterPath = entity.posterPath.orEmpty(),
            backdropPath = "",
            mediaType = MediaType.getType(entity.mediaType),
            personalRating = ratingsMap[entity.contentId]
        )
    }

    suspend fun removeContentFromDatabase(contentId: Int, mediaType: MediaType, listId: Int) {
        lastRemovedItem = databaseRepository.deleteItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = listId
        )
    }

    suspend fun moveItemToList(contentId: Int, mediaType: MediaType, currentListId: Int, newListId: Int) {
        lastRemovedItem = databaseRepository.moveItemToList(
            contentId = contentId,
            mediaType = mediaType,
            currentListId = currentListId,
            newListId = newListId
        )
        lastMovedListId = newListId
    }

    suspend fun undoItemRemoved() {
        lastRemovedItem?.let { contentEntity ->
            databaseRepository.reinsertItem(contentEntity)
        }
    }

    suspend fun undoMovedItem() {
        lastRemovedItem?.let { contentEntity ->
            databaseRepository.reinsertItem(contentEntity)
            lastMovedListId?.let {
                databaseRepository.deleteItem(
                    contentId = contentEntity.contentId,
                    mediaType = MediaType.getType(contentEntity.mediaType),
                    listId = it
                )
            }
        }
    }

    fun getAllLists(): Flow<List<WatchlistTabItem>> = databaseRepository.getAllLists().map { allListsEntity ->
        val allWatchlistTabs = mutableListOf<WatchlistTabItem>()

        allListsEntity.forEach { listEntity ->
            when (listEntity.listId) {
                DefaultLists.WATCHLIST.listId -> allWatchlistTabs.add(WatchlistTabItem.WatchlistTab)
                DefaultLists.WATCHED.listId -> allWatchlistTabs.add(WatchlistTabItem.WatchedTab)
                else -> {
                    val customTab = WatchlistTabItem.CustomTab(
                        tabName = listEntity.listName,
                        listId = listEntity.listId
                    )
                    allWatchlistTabs.add(customTab)
                }
            }
        }
        if (allListsEntity.size < Constants.MAX_WATCHLIST_LIST_NUMBER) {
            allWatchlistTabs.add(WatchlistTabItem.AddNewTab)
        }

        allWatchlistTabs.forEachIndexed { index, tabItem ->
            tabItem.tabIndex = index
        }
        allWatchlistTabs
    }

    suspend fun deleteList(listId: Int) {
        databaseRepository.deleteList(listId)
    }
}
