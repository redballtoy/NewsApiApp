package com.gmail.redballtoy.news_main.usecases

import com.gmail.redballtoy.news_data.ArticlesRepository
import com.gmail.redballtoy.news_data.model.Article
import kotlinx.coroutines.flow.Flow

class GetAllArticlesUseCase(private val repository: ArticlesRepository) {

    suspend fun invoke(): Flow<Article> {
        return repository.getAll()
    }
}