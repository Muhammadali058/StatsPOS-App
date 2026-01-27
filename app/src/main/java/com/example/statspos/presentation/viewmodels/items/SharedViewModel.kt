package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    data class ScreenState(
        val dataChanged: Boolean = false,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun notifyDataChanged() {
        state.update { it.copy(dataChanged = true) }
    }

    fun consumeDataChanged() {
        state.update { it.copy(dataChanged = false) }
    }
}
