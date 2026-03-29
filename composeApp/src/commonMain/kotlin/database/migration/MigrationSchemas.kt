package database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import common.domain.models.util.MediaType
import features.watchlist.ui.model.DefaultLists

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE content_entity (
                contentEntityDbId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                contentId INTEGER NOT NULL,
                mediaType TEXT NOT NULL
            )
        """
        )
        val defaultMediaType = MediaType.MOVIE.name
        connection.execSQL(
            "INSERT INTO content_entity (contentId, mediaType) " +
                "SELECT dbId, '$defaultMediaType' FROM item_entity"
        )
        connection.execSQL("DROP TABLE item_entity")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        val defaultListId = DefaultLists.WATCHLIST.listId

        connection.execSQL(
            """
            ALTER TABLE content_entity
            ADD COLUMN listId TEXT NOT NULL DEFAULT '$defaultListId'
        """
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE content_entity ADD COLUMN createdAt INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        val watchlistName = DefaultLists.WATCHLIST.name.lowercase()
        val watchedName = DefaultLists.WATCHED.name.lowercase()

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS new_list_entity (
                listId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                listName TEXT NOT NULL
            )
            """
        )
        connection.execSQL("INSERT INTO new_list_entity (listName) VALUES ('$watchlistName')")
        connection.execSQL("INSERT INTO new_list_entity (listName) VALUES ('$watchedName')")

        connection.execSQL(
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

        connection.execSQL(
            """
            INSERT INTO new_content_entity (contentEntityDbId, contentId, mediaType, listId, createdAt)
            SELECT CE.contentEntityDbId, CE.contentId, CE.mediaType, LE.listId, CE.createdAt
            FROM content_entity AS CE
            JOIN new_list_entity AS LE ON LOWER(CE.listId) = LE.listName
            """
        )

        connection.execSQL("DROP TABLE content_entity")
        connection.execSQL("ALTER TABLE new_list_entity RENAME TO list_entity")
        connection.execSQL("ALTER TABLE new_content_entity RENAME TO content_entity")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
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

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE list_entity ADD COLUMN isDefault INTEGER NOT NULL DEFAULT 0")
        connection.execSQL(
            "UPDATE list_entity SET isDefault = 1 WHERE listName IN ('watchlist', 'watched')"
        )
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE content_entity ADD COLUMN title TEXT NOT NULL DEFAULT ''")
        connection.execSQL("ALTER TABLE content_entity ADD COLUMN posterPath TEXT DEFAULT NULL")
        connection.execSQL("ALTER TABLE content_entity ADD COLUMN voteAverage REAL NOT NULL DEFAULT 0")
    }
}
