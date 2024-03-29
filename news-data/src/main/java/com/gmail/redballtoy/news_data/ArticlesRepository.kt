package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import com.gmail.redballtoy.newsapi.models.ArticleDTO
import com.gmail.redballtoy.newsapi.models.ResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach


class ArticlesRepository(
    private val database: NewsDatabase, private val api: NewsApi
) {
    fun getAll(): Flow<RequestResult<List<Article>>> {

        //local cache
        val cachedAllArticles = getAllFromDatabase()
            .map { result ->
                result.map { articlesDbos ->
                    articlesDbos?.map { it.toArticle() }
                }
            }.map { it.map { } }

        //remote data
        val remoteArticles: Flow<RequestResult<*>> = getAllFromServer()

        //merge result used with order
        return cachedAllArticles.combine(remoteArticles) { dbos: RequestResult<Article>, dtos: RequestResult<Article> ->


        }
    }

    private fun getAllFromServer(): Flow<RequestResult<ResponseDTO<ArticleDTO>>> {

        val apiRequest = flow {
            emit(api.everything())
        }.onEach { result ->
            if (result.isSuccess) {
                saveNetResponseToCache(checkNotNull(result.getOrThrow().articles))
            }
        }.map {
            it.toRequestResult()
        }

        //emit inProgress
        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())

        //union flows
        return merge(apiRequest, start)
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val dbos = data.map { articleDTO ->
            articleDTO.toArticleDbo()
        }
        database.articleDao.insert(dbos)
    }

    private fun getAllFromDatabase(): Flow<RequestResult.Success<List<ArticleDBO>>> {
        return database.articleDao.getAll().map { RequestResult.Success(it) }
    }

    suspend fun search(query: String): Flow<Article> {
        api.everything(query)
        TODO("Not Implemented")
    }
}

sealed class RequestResult<E>(internal val data: E? = null) {
    class InProgress<E>(data: E? = null) : RequestResult<E>(data)
    class Success<E>(data: E) : RequestResult<E>(data)
    class Error<E> : RequestResult<E>()
}


internal fun <T : Any> RequestResult<T?>.requireData(): T = checkNotNull(data)


//custom mapping
internal fun <In, Out> RequestResult<In>.map(mapper: (In?) -> Out): RequestResult<Out> {
    val outData = mapper(data)
    return when (this) {
        is RequestResult.Success -> RequestResult.Success(outData)
        is RequestResult.Error -> RequestResult.Error()
        is RequestResult.InProgress -> RequestResult.InProgress(outData)
    }
}

//convert Result(kotlin) to ResultRequest
internal fun <T> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}