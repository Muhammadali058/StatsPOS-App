package com.example.statspos.presentation.viewmodels.items

import android.util.Log
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

        val cost: String = "",
        val retail: String = "",
        val wholesale: String = "",
        val rate3: String = "",
        val rate4: String = "",
        val crtnRate: String = "",
        val crtnSize: String = "",
        val marketPrice: String = "",

        val isDiscRsPer: Boolean = false,
        val disc: String = "",
        val expirable: Boolean = false,
        val expiry: String = "",

        val stockWarningMin: String = "",
        val stockWarningMax: String = "",
        val maxSalePcs: String = "",
        val maxSaleCrtn: String = "",

        val openingCost: Double = 0.0,
        val openingStockPcs: String = "",
        val openingStockCrtn: String = "",
        val openingCrtnSize: Int = 0,

        val currentStockPcs: String = "",
        val currentStockCrtn: String = "",

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
        state.update { it.copy(cost = value) }
    }

    fun onRetailChange(value: String) {
        state.update { it.copy(retail = value) }
    }

    fun onWholesaleChange(value: String) {
        state.update { it.copy(wholesale = value) }
    }

    fun onRate3Change(value: String) {
        state.update { it.copy(rate3 = value) }
    }

    fun onRate4Change(value: String) {
        state.update { it.copy(rate4 = value) }
    }

    fun onCrtnRateChange(value: String) {
        state.update { it.copy(crtnRate = value) }
    }

    fun onCrtnSizeChange(value: String) {
        state.update { it.copy(crtnSize = value) }
    }

    fun onMarketPriceChange(value: String) {
        state.update { it.copy(marketPrice = value) }
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

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onStockWarningMinChange(value: String) {
        state.update { it.copy(stockWarningMin = value) }
    }

    fun onStockWarningMaxChange(value: String) {
        state.update { it.copy(stockWarningMax = value) }
    }

    fun onMaxSalePcsChange(value: String) {
        state.update { it.copy(maxSalePcs = value) }
    }

    fun onMaxSaleCrtnChange(value: String) {
        state.update { it.copy(maxSaleCrtn = value) }
    }

    fun onOpeningStockPcsChange(value: String) {
        state.update { it.copy(openingStockPcs = value) }
    }

    fun onOpeningStockCrtnChange(value: String) {
        state.update { it.copy(openingStockCrtn = value) }
    }

    fun onCurrentStockPcsChange(value: String) {
        state.update { it.copy(currentStockPcs = value) }
    }

    fun onCurrentStockCrtnChange(value: String) {
        state.update { it.copy(currentStockCrtn = value) }
    }

    fun onExpirableChange(value: Boolean) {
        state.update { it.copy(expirable = value) }
    }

    fun onExpiryChange(value: String) {
        state.update { it.copy(expiry = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
    }

    fun onPackingChange(value: String) {
        state.update { it.copy(packing = value) }
    }

    fun onLocationChange(value: String) {
        state.update { it.copy(location = value) }
    }

    fun onChangeableChange(value: Boolean) {
        state.update { it.copy(changeable = value) }
    }

    fun onRepeatableChange(value: Boolean) {
        state.update { it.copy(repeatable = value) }
    }

    fun onLockPcsChange(value: Boolean) {
        state.update { it.copy(lockPcs = value) }
    }

    fun onLockCrtnChange(value: Boolean) {
        state.update { it.copy(lockCrtn = value) }
    }

    fun onButtonChange(value: Boolean) {
        state.update { it.copy(button = value) }
    }

    fun onSearchableChange(value: Boolean) {
        state.update { it.copy(searchable = value) }
    }

    fun onSaleUnderStockChange(value: Boolean) {
        state.update { it.copy(saleUnderStock = value) }
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