package features.watchlist.domain

import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.util.MediaType
import common.domain.util.Constants
import database.model.ContentEntity
import database.repository.DatabaseRepository
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import features.watchlist.ui.state.WatchlistState
import network.models.content.common.MovieResponse
import network.models.content.common.ShowResponse
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import network.util.Left
import network.util.Right

class WatchlistInteractor(
    private val databaseRepository: DatabaseRepository,
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
) {
    private var lastRemovedItem: ContentEntity? = null
    private var lastMovedListId: Int? = null

    suspend fun getAllItems(listId: Int): List<ContentEntity> {
        return databaseRepository.getAllItemsByListId(listId = listId)
    }

    suspend fun fetchListDetails(
        entityList: List<ContentEntity>,
    ): WatchlistState {
        val watchlistState = WatchlistState()
        try {
            val detailedWatchlist = entityList.mapNotNull { entity ->
                getContentDetailsById(
                    contentId = entity.contentId,
                    mediaType = MediaType.getType(entity.mediaType),
                )
            }
            watchlistState.listItems.value = detailedWatchlist
        } catch (e: IllegalStateException) {
            watchlistState.setError(
                errorCode = e.message,
            )
        }
        return watchlistState
    }

    private suspend fun getContentDetailsById(
        contentId: Int,
        mediaType: MediaType,
    ): GenericContent? {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieDetailsById(contentId)
            MediaType.SHOW -> showRepository.getShowDetailsById(contentId)
            else -> return null
        }

        var contentDetails: GenericContent? = null
        result.collect { response ->
            when (response) {
                is Right -> {
                    println("getContentDetailsById failed with error: ${response.error}")
                    throw IllegalStateException(
                        response.error.code,
                        response.error.exception,
                    )
                }
                is Left -> {
                    contentDetails = when (mediaType) {
                        MediaType.MOVIE -> (response.value as MovieResponse).toGenericContent()
                        MediaType.SHOW -> (response.value as ShowResponse).toGenericContent()
                        else -> return@collect
                    }
                }
            }
        }
        return contentDetails
    }

    suspend fun removeContentFromDatabase(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
    ) {
        lastRemovedItem = databaseRepository.deleteItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = listId,
        )
    }
    suspend fun moveItemToList(
        contentId: Int,
        mediaType: MediaType,
        currentListId: Int,
        newListId: Int,
    ) {
        lastRemovedItem = databaseRepository.moveItemToList(
            contentId = contentId,
            mediaType = mediaType,
            currentListId = currentListId,
            newListId = newListId,
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
                    listId = it,
                )
            }
        }
    }

    suspend fun getAllLists(): List<WatchlistTabItem> {
        val allListsEntity = databaseRepository.getAllLists()

        val allWatchlistTabs = mutableListOf<WatchlistTabItem>()

        allListsEntity.forEach { listEntity ->
            when (listEntity.listId) {
                DefaultLists.WATCHLIST.listId -> allWatchlistTabs.add(WatchlistTabItem.WatchlistTab)
                DefaultLists.WATCHED.listId -> allWatchlistTabs.add(WatchlistTabItem.WatchedTab)
                else -> {
                    val customTab = WatchlistTabItem.CustomTab(
                        tabName = listEntity.listName,
                        listId = listEntity.listId,
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
        return allWatchlistTabs
    }

    suspend fun deleteList(listId: Int) {
        databaseRepository.deleteList(listId)
    }
}
