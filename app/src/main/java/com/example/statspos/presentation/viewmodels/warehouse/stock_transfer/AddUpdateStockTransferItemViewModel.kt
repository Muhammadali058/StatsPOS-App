package com.example.statspos.presentation.viewmodels.warehouse.stock_transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.warehouse.StockEntries
import com.example.statspos.domain.repository.warehouse.StockEntriesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateStockTransferItemViewModel @Inject constructor(
    private val api: StockEntriesRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val warehouseId: Long = 0L,
        val itemId: Long = 0L,
        val qty: String = "",
        val crtn: String = "",

        val stockPcs: Double = 0.0,
        val stockCrtn: Long = 0L,
        val wStockPcs: Double = 0.0,
        val wStockCrtn: Long = 0L,

        // Extra
        val warehouseName: String = "",
        val itemname: String = "",
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val message: String? = null,
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

            is UiEvent.ShowMessage -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowMessage(event.message))
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

    fun showMessage(message: String?) {
        showSnackbar(message ?: "")

//        state.update { it.copy(isLoading = false, error = null, message = message) }
//        onEvent(UiEvent.ShowMessage(message ?: ""))
    }

    fun showError(error: String?) {
        state.update { it.copy(error = error) }
        onEvent(UiEvent.ShowError(error ?: ""))
    }

    // endregion

    // region onChangeMethods
    fun onItemnameChange(value: String) {
        state.update {
            it.copy(
                itemname = value,
                itemId = 0L,

                stockPcs = 0.0,
                stockCrtn = 0L,
                wStockPcs = 0.0,
                wStockCrtn = 0L,
            )
        }
    }

    fun onWarehouseNameChange(value: String) {
        state.update { it.copy(warehouseName = value) }
    }

    fun onWarehouseIdChange(value: Long) {
        state.update { it.copy(warehouseId = value) }
    }

    fun onQtyChange(value: String) {
        state.update { it.copy(qty = value) }
    }

    fun onCrtnChange(value: String) {
        state.update { it.copy(crtn = value) }
    }
    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun insertOrUpdateData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val stockEntry = getFormData()

            val result = if (state.value.isUpdate) {
                stockEntry.id = state.value.updateId
                api.updateStockEntry(stockEntry)
            } else {
                api.insertStockEntry(stockEntry)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {
//                    clearTextboxes()
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            beforeRequest()

            when (val result = api.deleteStockEntry(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    onSuccess()
                }
            }
        }
    }

    fun editData(id: Long) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.getStockEntry(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val stockEntry = Gson().get<StockEntries>(result.data.asJsonObject)
                    setFormData(stockEntry)
                }
            }
        }
    }

    fun getItem(value: String) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (value.isEmpty())
                return@launch

//            beforeRequest()
            when (val result = api.isBarcodeExists(state.value.warehouseId, value)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val isExists = result.data.get("isExists").asBoolean
                    if (isExists) {
                        val item = Gson().get<Items>(result.data.get("data").asJsonObject)
                        state.update {
                            it.copy(
                                itemname = item.itemname!!,
                                itemId = item.id!!,

                                stockPcs = item.stockPcs!!,
                                stockCrtn = item.stockCrtn!!,
                                wStockPcs = item.wStockPcs!!,
                                wStockCrtn = item.wStockCrtn!!,
                            )
                        }
                    } else {
                        state.update {
                            it.copy(
                                itemId = 0L,

                                stockPcs = 0.0,
                                stockCrtn = 0L,
                                wStockPcs = 0.0,
                                wStockCrtn = 0L,
                            )
                        }
                        showSnackbar("Items not found")
                    }
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): StockEntries {
        return StockEntries(
            warehouseId = state.value.warehouseId,
            itemId = state.value.itemId,
            qty = HP.getDoubleValue(state.value.qty),
            crtn = HP.getIntValue(state.value.crtn),
        )
    }

    private fun setFormData(stockEntry: StockEntries) {
        state.update {
            it.copy(
                itemname = stockEntry.itemname!!,
                itemId = stockEntry.itemId!!,
                qty = stockEntry.qty.toString(),
                crtn = stockEntry.crtn.toString(),

                stockPcs = stockEntry.stockPcs!!,
                stockCrtn = stockEntry.stockCrtn!!,
                wStockPcs = stockEntry.wStockPcs!!,
                wStockCrtn = stockEntry.wStockCrtn!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                itemId = 0L,
                qty = "",
                crtn = "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.itemId == 0L) {
            showMessage("Please select item")
            return false
        }

        if (HP.getDoubleValue(state.value.qty) == 0.0 && HP.getIntValue(state.value.crtn) == 0) {
            showMessage("Please enter quantity")
            return false
        }

        return true
    }
    // endregion

    // region Others
    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false) }
        showError(error)
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        showMessage(message)
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false) }
    }

    fun updateInitialState(isUpdate: Boolean, updateId: Long, warehouseId: Long) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,
                warehouseId = warehouseId,
                warehouseName = if (warehouseId == 0L) "" else HP.getDropdownNameById(
                    warehouseId,
                    HP.warehouses
                )
            )
        }
    }
    // endregion
}