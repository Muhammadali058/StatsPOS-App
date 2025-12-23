package com.example.statspos.presentation.ui.screens.test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoDetailsViewModel(
    private val todo: String
): ViewModel() {

    private val _state = MutableStateFlow(TodoDetailState(todo))
    val state = _state.asStateFlow()
}

data class TodoDetailState(
    val todo: String
)