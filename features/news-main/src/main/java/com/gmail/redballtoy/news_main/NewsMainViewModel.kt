package com.gmail.redballtoy.news_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.redballtoy.news_data.RequestResult
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.news_main.usecases.GetAllArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    getAllArticlesUseCase: Provider<GetAllArticlesUseCase>,
) : ViewModel() {

    // get new readonly state flow
    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke(query = "android")
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)

    // fun forceUpdate() {}
}

private fun RequestResult<List<Article>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error(data)
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(data)
    }
}

sealed class State(val articles: List<Article>?) {
    object None : State(articles = null) // default state
    class Loading(articles: List<Article>? = null) : State(articles)
    class Error(articles: List<Article>? = null) : State(articles)
    class Success(articles: List<Article>) : State(articles)
}