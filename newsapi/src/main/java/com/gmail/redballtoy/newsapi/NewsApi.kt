package com.gmail.redballtoy.newsapi

import kotlinx.serialization.SerialName
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface NewsApi {

    @GET("/everything")
    fun everything(
        @Query("q") query: String? = null,
        @Query("from") from: Date? = null,
        @Query("to") to: Date? = null,
        @Query("language") language: String? = null,
        @Query("sortBy") sortBy: SortBy? = null,

        )
}

enum class SortBy {
    @SerialName("relevancy")
    RELEVANCY,

    @SerialName("popularity")
    POPULARITY,

    @SerialName("publishedAt")
    PUBLISHED_AT,
}
