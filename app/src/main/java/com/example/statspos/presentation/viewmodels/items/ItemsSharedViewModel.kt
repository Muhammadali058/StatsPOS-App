package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.viewmodels.items.ItemsViewModel.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ItemsSharedViewModel @Inject constructor() : ViewModel() {

    data class ScreenState(
        val itemChanged: Boolean = false,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun notifyItemChanged() {
        state.update { it.copy(itemChanged = true) }
    }

    fun consumeItemChanged() {
        state.update { it.copy(itemChanged = false) }
    }
}
