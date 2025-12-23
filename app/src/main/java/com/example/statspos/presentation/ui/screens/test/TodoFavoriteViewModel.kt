package com.example.statspos.presentation.ui.screens.test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoFavoriteViewModel: ViewModel() {

    private val _todos = MutableStateFlow((1..100).map {
        "Favorite $it"
    })

    val todos = _todos.asStateFlow()
}