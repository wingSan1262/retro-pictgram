package com.example.freeimagefeed.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.freeimagefeed.data.remote.model.response.PostDto
import com.example.freeimagefeed.data.remote.model.response.UserDto


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table
        database.execSQL("""
            CREATE TABLE post_comments (
                postId TEXT PRIMARY KEY NOT NULL,
                comments TEXT NOT NULL
            )
        """.trimIndent())
    }
}

@Database(entities = [PostDto::class, UserDto::class, CommentContentEntity::class], version = 2)
@TypeConverters(StringListConverter::class, UserDtoConverter::class, CommentEntityListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): FriendDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}