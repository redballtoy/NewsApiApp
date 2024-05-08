package com.gmail.redballtoy.news_main.usecases

import com.gmail.redballtoy.news_data.ArticlesRepository
import com.gmail.redballtoy.news_data.RequestResult
import com.gmail.redballtoy.news_data.map
import com.gmail.redballtoy.news_data.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllArticlesUseCase @Inject constructor(
    private val repository: ArticlesRepository
) {

    operator fun invoke(query:String): Flow<RequestResult<List<Article>>> {
        return repository.getAll(query)
            .map { requestResult ->
                requestResult.map { articles ->
                    articles.map { it.toUiArticles() }
                }
            }
    }

}

private fun Article.toUiArticles(): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        urlToImage = urlToImage,
        url = url,
        author = author,
        publishedAt = publishedAt,
        source = source,
        content = content
    )
}