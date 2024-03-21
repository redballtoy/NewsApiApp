package com.gmail.redballtoy.news_main.usecases

import com.gmail.redballtoy.news_data.ArticlesRepository
import com.gmail.redballtoy.news_data.model.Article
import kotlinx.coroutines.flow.Flow

class GetAllArticlesUseCase(private val repository: ArticlesRepository) {

    operator fun invoke(): Flow<List<Article>> {
        return repository.getAll()
    }
}