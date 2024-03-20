package com.gmail.redballtoy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gmail.redballtoy.database.dao.ArticleDao
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.database.utils.Converters

@Database(entities = [ArticleDBO::class], version = 1)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getArticlesDao(): ArticleDao

}

fun NewsDatabase(applicationContext: Context): NewsDatabase {
    return Room.databaseBuilder(
        checkNotNull(applicationContext.applicationContext),
        NewsDatabase::class.java, "news"
    ).build()
}