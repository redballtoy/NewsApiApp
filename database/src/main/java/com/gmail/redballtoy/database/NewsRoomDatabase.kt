package com.gmail.redballtoy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gmail.redballtoy.database.dao.ArticleDao
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.database.utils.Converters


class NewsDatabase
internal constructor(private val database: NewsRoomDatabase) {
    val articleDao: ArticleDao
        get() = database.getArticlesDao()

}


@Database(entities = [ArticleDBO::class], version = 1)
@TypeConverters(Converters::class)
internal abstract class NewsRoomDatabase : RoomDatabase() {

    abstract fun getArticlesDao(): ArticleDao

}

fun NewsDatabase(applicationContext: Context): NewsDatabase {
    val newsRoomDatabase = Room.databaseBuilder(
        checkNotNull(applicationContext.applicationContext),
        NewsRoomDatabase::class.java,
        "news"
    ).build()
    return NewsDatabase(newsRoomDatabase)
}