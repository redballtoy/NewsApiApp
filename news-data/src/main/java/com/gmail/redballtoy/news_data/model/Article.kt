package com.gmail.redballtoy.news_data.model

import java.util.Date

data class Article(
    val id: Long=0,
    val source: Source?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: Date?,
    val content: String?,
)

data class Source(
    val id: String?,
    val name: String,
)
