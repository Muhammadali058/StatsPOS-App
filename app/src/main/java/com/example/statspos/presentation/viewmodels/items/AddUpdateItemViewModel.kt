package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateItemViewModel @Inject constructor(
    private val itemsRepo: ItemsRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val itemname: String = "",
        val barcode: String = "",
        val urduname: String = "",
        val refCode: String = "",

        val cost: Double = 0.0,
        val retail: Double = 0.0,
        val wholesale: Double = 0.0,
        val rate3: Double = 0.0,
        val rate4: Double = 0.0,
        val crtnRate: Double = 0.0,
        val crtnSize: Int = 0,
        val marketPrice: Double = 0.0,

        val isDistRsPer: Boolean = false,
        val disc: Double = 0.0,
        val isExpirable: Boolean = false,
        val expiry: String = "",

        val stockWarningMin: Int = 0,
        val stockWarningMax: Int = 0,
        val maxSalePcs: Int = 0,
        val maxSaleCrtn: Int = 0,

        val openingCost: Double = 0.0,
        val openingStockPcs: Double = 0.0,
        val openingStockCrtn: Long = 0L,
        val openingCrtnSize: Int = 0,

        val currentStockPcs: Double = 0.0,
        val currentStockCrtn: Long = 0L,

        val repeatable: Boolean = false,
        val searchable: Boolean = true,
        val changeable: Boolean = false,
        val button: Boolean = false,
        val lockPcs: Boolean = false,
        val lockCrtn: Boolean = false,
        val saleUnderStock: Boolean = true,

        val categoryId: Long = 0L,
        val subCategoryId: Long = 0L,
        val vendorId: Long = 0L,

        val packing: String = "",
        val location: String = "",
        val imageUrl: String = "",

        // Extras
        val categoryName: String = "",
        val subCategoryName: String = "",
        val vendorName: String = "",

        val isLoading: Boolean = false,
        val error: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun beforeRequest() {
        state.update { it.copy(isLoading = true, error = null) }
    }

    private val _event = Channel<UiEvent>()
    var event = _event.receiveAsFlow()
    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowSnackbar(event.message, event.type))
                }
            }

            is UiEvent.ShowError -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowError(event.error))
                }
            }

            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        onEvent(UiEvent.ShowSnackbar(message, type))
    }

    // endregion

    // region onChangeMethods
    fun onItemnameChange(value: String) {
        state.update { it.copy(itemname = value) }
    }

    fun onUrdunameChange(value: String) {
        state.update { it.copy(urduname = value) }
    }

    fun onBarcodeChange(value: String) {
        state.update { it.copy(barcode = value) }
    }

    fun onRefCodeChange(value: String) {
        state.update { it.copy(refCode = value) }
    }

    fun onCostChange(value: String) {
        state.update { it.copy(cost = HP.getDoubleValue(value)) }
    }

    fun onRetailChange(value: String) {
        state.update { it.copy(retail = HP.getDoubleValue(value)) }
    }

    fun onWholesaleChange(value: String) {
        state.update { it.copy(wholesale = HP.getDoubleValue(value)) }
    }

    fun onRate3Change(value: String) {
        state.update { it.copy(rate3 = HP.getDoubleValue(value)) }
    }

    fun onRate4Change(value: String) {
        state.update { it.copy(rate4 = HP.getDoubleValue(value)) }
    }

    fun onCrtnRateChange(value: String) {
        state.update { it.copy(crtnRate = HP.getDoubleValue(value)) }
    }

    fun onCrtnSizeChange(value: String) {
        state.update { it.copy(crtnSize = HP.getIntValue(value)) }
    }

    fun onMarketPriceChange(value: String) {
        state.update { it.copy(marketPrice = HP.getDoubleValue(value)) }
    }

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }

    fun onSubCategoryIdChange(value: Long) {
        state.update { it.copy(subCategoryId = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
    }

    // endregion

    // region Network calls
    fun deleteItem(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = itemsRepo.deleteItem(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    onSuccess()
                }
            }
        }
    }

    // endregion

    // region Others
    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false, error = error) }
        error?.let { onEvent(UiEvent.ShowError(it)) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showSnackbar(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}