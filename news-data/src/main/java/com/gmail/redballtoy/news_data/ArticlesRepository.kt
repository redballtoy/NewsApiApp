package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.news_common.Logger
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import com.gmail.redballtoy.newsapi.models.ArticleDTO
import com.gmail.redballtoy.newsapi.models.ResponseDTO
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach


class ArticlesRepository @Inject constructor(
    private val database: NewsDatabase,
    private val api: NewsApi,
    private val logger: Logger

) {

    fun getAll(
        query: String,
        mergeStrategy: MergeStrategy<RequestResult<List<Article>>> = DefaultMergeStrategy()
    ): Flow<RequestResult<List<Article>>> {

        //local cache
        val cachedAllArticles: Flow<RequestResult<List<Article>>> = getAllFromDatabase()

        //remote data
        val remoteArticles: Flow<RequestResult<List<Article>>> = getAllFromServer(query)

        //merge result used with order
        return cachedAllArticles.combine(remoteArticles, mergeStrategy::merge)
            .flatMapLatest { result ->
                if (result is RequestResult.Success) {
                    database.articleDao.observeAll()
                        .map { dbos -> dbos.map { it.toArticle() } }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(result)
                }
            }
    }


    private fun getAllFromDatabase(): Flow<RequestResult<List<Article>>> {
        val databaseRequest = database.articleDao::getAll
            .asFlow()
            .map<List<ArticleDBO>, RequestResult<List<ArticleDBO>>> { RequestResult.Success(it) }
            .catch {
                logger.e(TAG, "Error getting data from database. Cause = $it")
                emit(RequestResult.Error<List<ArticleDBO>>(error = it))
            }

        //emit inProgress
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())

        //union flows
        return merge(start, databaseRequest).map { result ->
            result.map { articleDbos ->
                articleDbos.map { it.toArticle() }
            }
        }
    }


    suspend fun search(query: String): Flow<Article> {
        api.everything(query)
        TODO("Not Implemented")
    }


    private fun getAllFromServer(query: String): Flow<RequestResult<List<Article>>> {
        val apiRequest = flow {
            emit(api.everything(query = query))
        }.onEach { result ->
            if (result.isSuccess) {
                saveNetResponseToCache(result.getOrThrow().articles)
            }
        }.onEach { result ->
            if (result.isFailure) {
                logger.e(TAG, "Error getting data from server. Cause = ${result.exceptionOrNull()}")
            }

        }.map { it.toRequestResult() }

        //emit inProgress
        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())

        //union flows
        return merge(apiRequest, start)
            .map { result ->
                result.map { response ->
                    response.articles.map { it.toArticle() }
                }
            }
    }


    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val dbos = data.map { articleDTO -> articleDTO.toArticleDbo() }
        database.articleDao.insert(dbos)
    }


    fun fetchLatest(query: String): Flow<RequestResult<List<Article>>> {
        return getAllFromServer(query)
    }

    private companion object {
        const val TAG = "myLog"
    }


}


