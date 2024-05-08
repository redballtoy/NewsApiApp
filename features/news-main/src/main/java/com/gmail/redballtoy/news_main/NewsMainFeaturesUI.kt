package com.gmail.redballtoy.news_main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.news_data.model.Source
import java.util.Date

@Composable
fun NewsMainScreen() {
    NewsMainScreen(vm = viewModel())
}

@Composable
internal fun NewsMainScreen(vm: NewsMainViewModel) {
    val state by vm.state.collectAsState()
    when (val currentState = state) {
        is State.Success -> Articles(currentState.articles)
        is State.Error -> ArticlesWithError(currentState.articles)
        is State.Loading -> ArticlesDuringUpdate(currentState.articles)
        State.None -> NewsEmpty()
    }
}

@Composable
internal fun ArticlesWithError(articles: List<Article>?) {
    Column {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error during update.", color = MaterialTheme.colorScheme.onError)
        }
        if (articles != null) {
            Articles(articles = articles)
        }
    }

}

@Composable
@Preview
internal fun ArticlesDuringUpdate(
    @PreviewParameter(ArticlesPreviewProvider::class, limit = 1)
    articles: List<Article>?
) {
    Column {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        if (articles != null) {
            Articles(articles = articles)
        }
    }

}

@Composable
internal fun NewsEmpty() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "No News")
    }

}

@Preview
@Composable
private fun Articles(
    @PreviewParameter(ArticlesPreviewProvider::class, limit = 1)
    articles: List<Article>
) {
    LazyColumn {
        items(articles) { article ->
            key(article.id) {
                Article(article)

            }

        }

    }
}

@Preview
@Composable
internal fun Article(
    @PreviewParameter(ArticlePreviewProvider::class, limit = 1)
    article: Article
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(text = article.title ?:"No Title", style = MaterialTheme.typography.headlineMedium, maxLines = 1)
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = article.description ?:"No Description", style = MaterialTheme.typography.bodyMedium, maxLines = 3)
    }
}

private class ArticlePreviewProvider : PreviewParameterProvider<Article> {
    override val values = sequenceOf(
        Article(
            1, Source("1", "source"), "II_1", "Android Studio_1", "Best IDE", "http:", "",
            Date("20240101"), ""
        ),
        Article(
            2, Source("2", "source"), "II_2", "Android Studio_2", "Best IDE", "http:", "",
            Date("20240101"), ""
        ),
        Article(
            3, Source("3", "source"), "II_3", "Android Studio_3", "Best IDE", "http:", "",
            Date("20240101"), ""
        ),
    )
}


private class ArticlesPreviewProvider : PreviewParameterProvider<List<Article>> {

    private val articleProvider = ArticlePreviewProvider()

    override val values = sequenceOf(
        articleProvider.values.toList()
    )
}
