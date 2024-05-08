package com.gmail.redballtoy.newsapi.utils

import okhttp3.Interceptor
import okhttp3.Response

internal class NewsApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .url(chain.request().url.newBuilder()
                    .addQueryParameter("apiKey",apiKey)
                    .build()
                )
                .build()
        )
    }
}