package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.model.ListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListEntityDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertList(listEntity: ListEntity)

    @Query("DELETE FROM list_entity WHERE listId = :listId")
    suspend fun deleteList(listId: Int)

    @Query("SELECT * FROM list_entity")
    fun getAllLists(): Flow<List<ListEntity>>

    @Query("SELECT COUNT(*) FROM list_entity WHERE listName = :listName COLLATE NOCASE")
    suspend fun getListCountByName(listName: String): Int

    @Query("SELECT * FROM list_entity")
    suspend fun getAllSnapshot(): List<ListEntity>

    @Query("DELETE FROM list_entity")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ListEntity>)
}
