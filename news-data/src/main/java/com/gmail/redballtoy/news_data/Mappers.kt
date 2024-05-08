package com.gmail.redballtoy.news_data

import com.gmail.redballtoy.database.models.ArticleDBO
import com.gmail.redballtoy.database.models.SourceDBO
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.news_data.model.Source
import com.gmail.redballtoy.newsapi.models.ArticleDTO
import com.gmail.redballtoy.newsapi.models.SourceDTO

internal fun ArticleDBO.toArticle():Article{
    return Article(
        source=source?.toSource(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

internal fun ArticleDTO.toArticle():Article{
   return Article(
    source=source?.toSource(),
       author = author,
       title = title,
       description = description,
       url = url,
       urlToImage = urlToImage,
       publishedAt = publishedAt,
       content = content
   )
}


internal fun ArticleDBO.toArticleDto():ArticleDTO{
    return ArticleDTO(
        source=source?.toSourceDto(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
    )
}

internal fun ArticleDTO.toArticleDbo():ArticleDBO{
    return ArticleDBO(
        source=source?.toSourceDbo(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
    )
}

internal fun SourceDTO.toSource():Source{
    return Source(id = id?:name, name=name)

}

internal fun SourceDBO.toSource():Source{
    return Source(id = id?:name, name=name)

}

internal fun SourceDTO.toSourceDbo():SourceDBO{
    return SourceDBO(id = id?:name, name=name)

}

internal fun SourceDBO.toSourceDto():SourceDTO{
    return SourceDTO(id = id?:name, name=name)

}
