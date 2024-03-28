package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import com.gmail.redballtoy.newsapi.models.ArticleDTO
import com.gmail.redballtoy.newsapi.models.ResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach


class ArticlesRepository(
    private val database: NewsDatabase,
    private val api: NewsApi
) {
    fun getAll(): Flow<RequestResult<List<Article>>> {

        //local cache
        val cachedAllArticles = getAllFromDatabase()

        //remote data
        val remoteArticles: Flow<RequestResult<*>> = getAllFromServer()


        //merge result
        cachedAllArticles.map
        {

        }

        return cachedAllArticles.combine(remoteArticles)
        {

        }

    }

    private fun getAllFromServer(): Flow<RequestResult<*>> {
        return flow {
            emit(api.everything())
        }.map { result ->
            if (result.isSuccess) {
                val response: ResponseDTO<ArticleDTO> = result.getOrThrow()
                RequestResult.Success(response.articles)
            } else {
                RequestResult.Error(null)
            }
        }.onEach { requestResult ->
            if (requestResult is RequestResult.Success) {
                requestResult.requireData()
                    .map {
                        articleDTO -> articleDTO.toArticleDbo()
                    }

                requestResult.map { dtos ->
                    dtos.map { articleDTO ->
                        articleDTO.toArticleDbo()
                    }
                }
                database.articleDao.insert(requestResult.data)
            }
        }

    }

    private fun getAllFromDatabase(): Flow<RequestResult.Success<List<ArticleDBO>>> {
        return database.articleDao
            .getAll()
            .map { RequestResult.Success(it) }
    }

    suspend fun search(query: String): Flow<Article> {
        api.everything(query)
        TODO("Not Implemented")
    }
}

sealed class RequestResult<E>(internal val data: E) {
    class InProgress<E>(data: E) : RequestResult<E>(data)
    class Success<E>(data: E) : RequestResult<E>(data)
    class Error<E>(data: E) : RequestResult<E>(data)
}


internal fun <T : Any> RequestResult<T?>.requireData(): T {
    return checkNotNull(data)
}

//custom mapping
internal fun <In, Out> RequestResult<In>.map(mapper: (In) -> Out): RequestResult<Out> {
    val outData = mapper(data)
    return when (this) {
        is RequestResult.Success -> RequestResult.Success(outData)
        is RequestResult.Error -> RequestResult.Error(outData)
        is RequestResult.InProgress -> RequestResult.InProgress(outData)
    }
}