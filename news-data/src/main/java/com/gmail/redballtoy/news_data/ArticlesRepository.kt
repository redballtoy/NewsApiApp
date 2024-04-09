package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.newsapi.NewsApi
import com.gmail.redballtoy.newsapi.models.ArticleDTO
import com.gmail.redballtoy.newsapi.models.ResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach


class ArticlesRepository(
    private val database: NewsDatabase,
    private val api: NewsApi
) {

    fun getAll(
        mergeStrategy: MergeStrategy<RequestResult<List<Article>>> = DefaultMergeStrategy()
    ): Flow<RequestResult<List<Article>>> {

        //local cache
        val cachedAllArticles: Flow<RequestResult<List<Article>>> = getAllFromDatabase()
            .map { result ->
                result.map { articleDbos ->
                    articleDbos.map { it.toArticle() }
                }
            }

        //remote data
        val remoteArticles: Flow<RequestResult<List<Article>>> = getAllFromServer()
            .map { result ->
                result.map { response ->
                    response.articles.map { it.toArticle() }
                }
            }

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


    private fun getAllFromDatabase(): Flow<RequestResult<List<ArticleDBO>>> {
        val databaseRequest = database.articleDao::getAll.asFlow()
            .map { RequestResult.Success(it) }

        //emit inProgress
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())

        //union flows
        return merge(start, databaseRequest)
    }


    suspend fun search(query: String): Flow<Article> {
        api.everything(query)
        TODO("Not Implemented")
    }

    private fun getAllFromServer(): Flow<RequestResult<ResponseDTO<ArticleDTO>>> {

        val apiRequest = flow {
            emit(api.everything())
        }.onEach { result ->
            if (result.isSuccess) {
                saveNetResponseToCache(result.getOrThrow().articles)
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

}


