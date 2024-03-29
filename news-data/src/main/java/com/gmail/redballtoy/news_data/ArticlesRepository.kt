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
        val cachedAllArticles: Flow<RequestResult<List<Article>>> = getAllFromDatabase()
            .map { result ->
                result.map { articleDbos ->
                    articleDbos.map { it.toArticle() }
                }
            }


        //remote data
        val remoteArticles = getAllFromServer()
            .map { result ->
                result.map { response ->
                    response.articles.map { it.toArticle() }
                }
            }

        //merge result used with order
        return cachedAllArticles.combine(remoteArticles) { dbos: RequestResult<List<Article>>, dtos: RequestResult<List<Article>> ->
            RequestResult.InProgress()
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

sealed class RequestResult<out E>(internal val data: E? = null) {
    class InProgress<E>(data: E? = null) : RequestResult<E>(data)
    class Success<E : Any>(data: E) : RequestResult<E>(data)
    class Error<E>(data: E? = null) : RequestResult<E>()
}

internal fun <T : Any> RequestResult<T?>.requireData(): T = checkNotNull(data)

//custom mapping
internal fun <In, Out> RequestResult<In>.map(mapper: (In) -> Out): RequestResult<Out> {
    return when (this) {
        is RequestResult.Success -> {
            val outData: Out = mapper(checkNotNull(data))
            RequestResult.Success(checkNotNull(outData))
        }

        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
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