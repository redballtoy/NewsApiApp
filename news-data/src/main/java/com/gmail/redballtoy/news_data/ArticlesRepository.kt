package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticlesRepository(
    private val database: NewsDatabase,
    private val api: NewsApi
) {

    fun getAll(): Flow<List<Article>> {
        return database.articleDao
            .getAll()
            .map { articles ->
                articles.map { it.toArticle() }

            }
    }

    suspend fun search(query: String): Flow<Article> {
        api.everything(query)
        TODO("Not Implemented")
    }
}