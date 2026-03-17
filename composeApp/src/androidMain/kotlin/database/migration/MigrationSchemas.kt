package database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import common.domain.models.util.MediaType
import features.watchlist.ui.model.DefaultLists

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE content_entity (
                contentEntityDbId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                contentId INTEGER NOT NULL,
                mediaType TEXT NOT NULL
            )
        """
        )
        val defaultMediaType = MediaType.MOVIE.name
        db.execSQL(
            "INSERT INTO content_entity (contentId, mediaType) " +
                "SELECT dbId, '$defaultMediaType' FROM item_entity"
        )
        db.execSQL("DROP TABLE item_entity")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val defaultListId = DefaultLists.WATCHLIST.listId

        db.execSQL(
            """
            ALTER TABLE content_entity 
            ADD COLUMN listId TEXT NOT NULL DEFAULT '$defaultListId'
        """
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE content_entity ADD COLUMN createdAt INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_4_5: (Map<String, String>) -> Migration = { localizedListNamesMap ->
    object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create a temporary table for ListEntity
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS new_list_entity (
                listId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                listName TEXT NOT NULL
            )
            """
            )

            // Create a temporary table for mapping old list names to new list names
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS list_name_mapping (
                oldListName TEXT NOT NULL,
                newListName TEXT NOT NULL
            )
            """
            )

            // Populate the mapping table with old and new list names
            localizedListNamesMap.forEach { (oldName, localizedName) ->
                db.execSQL(
                    """
                INSERT INTO list_name_mapping (oldListName, newListName)
                VALUES (?, ?)
                """,
                    arrayOf(oldName, localizedName)
                )
            }

            // Populate the new_list_entity table with localized list names from the mapping table
            db.execSQL(
                """
            INSERT INTO new_list_entity (listName)
            SELECT DISTINCT newListName FROM list_name_mapping
            """
            )

            // Create a temporary table for ContentEntity
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS new_content_entity (
                contentEntityDbId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                contentId INTEGER NOT NULL,
                mediaType TEXT NOT NULL,
                listId INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(listId) REFERENCES list_entity(listId) ON DELETE CASCADE
            )
            """
            )

            // Copy and convert the listId in content_entity to integer referencing new_list_entity
            db.execSQL(
                """
            INSERT INTO new_content_entity (contentEntityDbId, contentId, mediaType, listId, createdAt)
            SELECT CE.contentEntityDbId, CE.contentId, CE.mediaType, LE.listId, CE.createdAt
            FROM content_entity AS CE
            JOIN list_name_mapping AS LNM ON CE.listId = LNM.oldListName
            JOIN new_list_entity AS LE ON LNM.newListName = LE.listName
            """
            )

            // Drop the old tables
            db.execSQL("DROP TABLE content_entity")
            db.execSQL("DROP TABLE list_name_mapping")

            // Rename new tables to the official table names
            db.execSQL("ALTER TABLE new_list_entity RENAME TO list_entity")
            db.execSQL("ALTER TABLE new_content_entity RENAME TO content_entity")

            // Insert default lists if they do not exist
            localizedListNamesMap.values.forEach { localizedName ->
                db.execSQL(
                    """
                INSERT INTO list_entity (listName)
                SELECT ? WHERE NOT EXISTS (
                    SELECT 1 FROM list_entity WHERE listName = ?
                )
                """,
                    arrayOf(localizedName, localizedName)
                )
            }
        }
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS personal_ratings (
                contentId INTEGER PRIMARY KEY NOT NULL,
                mediaType TEXT NOT NULL,
                rating REAL NOT NULL
            )
            """
        )
    }
}
