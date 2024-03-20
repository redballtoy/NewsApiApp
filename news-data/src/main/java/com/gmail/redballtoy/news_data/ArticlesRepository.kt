package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import kotlinx.coroutines.flow.Flow

class ArticlesRepository(
    private val database: NewsDatabase,
    private val api: NewsApi
) {

    fun request(): Flow<Article> {
        TODO("Not implemented")
    }
}