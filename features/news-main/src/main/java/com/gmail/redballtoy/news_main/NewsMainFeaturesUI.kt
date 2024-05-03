package com.gmail.redballtoy.news_main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.news_data.model.Source
import java.util.Date

@Composable
fun NewsMain() {
    NewsMain(vm = viewModel())
}

@Composable
internal fun NewsMain(vm: NewsMainViewModel) {
    val state by vm.state.collectAsState()
    when (val currentState = state) {
        is State.Success -> Articles(currentState)
        is State.Error -> TODO()
        is State.Loading -> TODO()
        State.None -> TODO()
    }
}

@Composable
private fun Articles(state: State.Success) {
    LazyColumn {
        items(state.articles) { article ->
            key(article.id) {
                Article(article)

            }

        }

    }
}

@Preview
@Composable
internal fun Article(
    @PreviewParameter(ArticlePreviewProvider::class)
    article: Article
) {
    Column {
        Text(text = article.title, style = MaterialTheme.typography.headlineMedium, maxLines = 1)
        Text(text = article.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
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
        )
    )
}
