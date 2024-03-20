package com.gmail.redballtoy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gmail.redballtoy.database.dao.ArticleDao
import com.gmail.redballtoy.database.models.ArticleDBO

@Database(entities = [ArticleDBO::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getArticlesDao(): ArticleDao

}

fun NewsDatabase(applicationContext: Context): NewsDatabase {
    return Room.databaseBuilder(
        checkNotNull(applicationContext.applicationContext),
        NewsDatabase::class.java, "news"
    ).build()
}