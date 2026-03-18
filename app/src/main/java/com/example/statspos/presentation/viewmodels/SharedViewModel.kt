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
        val billSaved: Boolean = false,
        val billPosted: Boolean = false,
        val item: Items? = null,

        // When branch change
        val refreshItemsScreen: Boolean = false,
        val refreshSalesScreen: Boolean = false,
        val refreshPurchaseScreen: Boolean = false,
        val refreshReportsScreen: Boolean = false,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun setItem(item: Items) {
        state.update { it.copy(item = item) }
    }

    fun notifyDataChanged() {
        state.update { it.copy(dataChanged = true) }
    }

    fun notifyBillPosted() {
        state.update { it.copy(billPosted = true) }
    }

    fun notifyBillSaved() {
        state.update { it.copy(billSaved = true) }
    }

    fun consumeDataChanged() {
        state.update {
            it.copy(
                dataChanged = false,
                item = null,
            )
        }
    }

    fun consumeBillPosted() {
        state.update {
            it.copy(
                billPosted = false,
            )
        }
    }

    fun consumeBillSaved() {
        state.update {
            it.copy(
                billSaved = false,
            )
        }
    }

    fun notifyBranchChanged() {
        state.update {
            it.copy(
                refreshItemsScreen = true,
                refreshSalesScreen = true,
                refreshPurchaseScreen = true,
                refreshReportsScreen = true,
            )
        }
    }

    fun consumeRefreshItemsScreen() {
        state.update { it.copy(refreshItemsScreen = false) }
    }

    fun consumeRefreshSalesScreen() {
        state.update { it.copy(refreshSalesScreen = false) }
    }

    fun consumeRefreshPurchaseScreen() {
        state.update { it.copy(refreshPurchaseScreen = false) }
    }

    fun consumeRefreshReportsScreen() {
        state.update { it.copy(refreshReportsScreen = false) }
    }
}