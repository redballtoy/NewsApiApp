package com.gmail.redballtoy.news_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.redballtoy.news_data.RequestResult
import com.gmail.redballtoy.news_data.model.Article
import com.gmail.redballtoy.news_main.usecases.GetAllArticlesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class NewsMainViewModel(
    private val getAllArticlesUseCase: GetAllArticlesUseCase
) : ViewModel() {

    //get new readonly state flow
    val state: StateFlow<State> = getAllArticlesUseCase()
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)




}


private fun RequestResult<List<Article>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error()
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(checkNotNull(data))
    }
}

sealed class State {
    object None : State() //default state
    class Loading(val articles: List<Article>?) : State()
    class Error : State()
    class Success(val articles: List<Article>) : State()

}