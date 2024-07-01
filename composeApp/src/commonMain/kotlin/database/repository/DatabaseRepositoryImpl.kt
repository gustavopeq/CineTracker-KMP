package database.repository

import common.domain.models.util.MediaType
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.model.ContentEntity
import database.model.ListEntity

class DatabaseRepositoryImpl(
    private val contentEntityDao: ContentEntityDao,
    private val listEntityDao: ListEntityDao,
) : DatabaseRepository {

    override suspend fun insertItem(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
    ) {
        val item = ContentEntity(
            contentId = contentId,
            mediaType = mediaType.name,
            listId = listId,
        )

        contentEntityDao.insert(item)
    }

    override suspend fun deleteItem(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
    ): ContentEntity? {
        val itemRemoved = contentEntityDao.getItem(
            contentId = contentId,
            mediaType = mediaType.name,
            listId = listId,
        )
        if (itemRemoved != null) {
            contentEntityDao.delete(
                contentId = contentId,
                mediaType = mediaType.name,
                listId = listId,
            )
        }
        return itemRemoved
    }

    override suspend fun getAllItemsByListId(listId: Int): List<ContentEntity> {
        return contentEntityDao.getAllItems(listId)
    }

    override suspend fun searchItems(
        contentId: Int,
        mediaType: MediaType,
    ): List<ContentEntity> {
        return contentEntityDao.searchItems(
            contentId = contentId,
            mediaType = mediaType.name,
        )
    }

    override suspend fun moveItemToList(
        contentId: Int,
        mediaType: MediaType,
        currentListId: Int,
        newListId: Int,
    ): ContentEntity? {
        deleteItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = newListId,
        )
        insertItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = newListId,
        )
        return deleteItem(
            contentId = contentId,
            mediaType = mediaType,
            listId = currentListId,
        )
    }

    override suspend fun reinsertItem(contentEntity: ContentEntity) {
        contentEntityDao.insert(contentEntity)
    }

    override suspend fun getAllLists(): List<ListEntity> {
        return listEntityDao.getAllLists()
    }

    /**
     * @return Return true when list is created or false if new list couldn't be created
     */
    override suspend fun addNewList(listName: String): Boolean {
        val newListName = listName.lowercase()
        val isDuplicated = listEntityDao.getListCountByName(newListName) > 0

        return if (isDuplicated) {
            false
        } else {
            listEntityDao.insertList(
                listEntity = ListEntity(
                    listName = newListName,
                ),
            )
            true
        }
    }

    override suspend fun deleteList(listId: Int) {
        listEntityDao.deleteList(listId)
    }
}
