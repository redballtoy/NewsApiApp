package com.gmail.redballtoy.newsapiapp

import android.content.Context
import com.gmail.redballtoy.database.NewsDatabase
import com.gmail.redballtoy.news_common.AppDispatchers
import com.gmail.redballtoy.newsapi.NewsApi
import com.gmail.redballtoy.newsapi.newApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providedNewsApi(): NewsApi {
        return newApi(
            baseUrl = BuildConfig.NEWS_API_BASE_URL,
            apiKey = BuildConfig.NEWS_API_KEY
        )
    }

    @Provides
    @Singleton
    fun providedDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAppCoroutineDispatcher(): AppDispatchers = AppDispatchers()
}