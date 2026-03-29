package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.model.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentEntityDao {
    @Query("SELECT * FROM content_entity WHERE listId = :listId ORDER BY createdAt DESC")
    fun getAllItems(listId: Int): Flow<List<ContentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contentEntity: ContentEntity)

    @Query(
        "DELETE FROM content_entity " +
            "WHERE contentId=:contentId AND mediaType = :mediaType AND listId = :listId"
    )
    suspend fun delete(contentId: Int, mediaType: String, listId: Int)

    @Query(
        "SELECT * FROM content_entity WHERE contentId = :contentId AND mediaType = :mediaType"
    )
    fun searchItems(contentId: Int, mediaType: String): Flow<List<ContentEntity>>

    @Query(
        "SELECT * FROM content_entity WHERE " +
            "contentId = :contentId AND mediaType = :mediaType AND listId = :listId"
    )
    suspend fun getItem(contentId: Int, mediaType: String, listId: Int): ContentEntity?

    @Query(
        "UPDATE content_entity SET title = :title, posterPath = :posterPath, voteAverage = :voteAverage " +
            "WHERE contentId = :contentId AND mediaType = :mediaType"
    )
    suspend fun updateCachedFields(
        contentId: Int,
        mediaType: String,
        title: String,
        posterPath: String?,
        voteAverage: Float
    )
}
