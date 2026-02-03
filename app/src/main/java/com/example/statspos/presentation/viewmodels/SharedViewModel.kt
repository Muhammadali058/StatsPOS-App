package com.example.statspos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.statspos.domain.models.items.Items
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    data class ScreenState(
        val dataChanged: Boolean = false,
        val item: Items? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun setItem(item: Items) {
        state.update { it.copy(item = item) }
    }

    fun notifyDataChanged() {
        state.update { it.copy(dataChanged = true) }
    }

    fun consumeDataChanged() {
        state.update { it.copy(
            dataChanged = false,
            item = null,
        ) }
    }
}