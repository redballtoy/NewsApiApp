package com.gmail.redballtoy.news_main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class NewsMainViewModel(

) : ViewModel() {

    //get new readonly state flow
    private val _state = MutableStateFlow(State.None)
    val state: StateFlow<State>
        get() = _state.asStateFlow()
}

sealed class State {
    object None : State() //default state
    class Loading : State()
    class Error : State()
    class Success(val articles: Articles) : State()

}