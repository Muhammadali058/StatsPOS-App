package com.example.statspos.presentation.viewmodels.warehouse.stock_transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.warehouse.StockEntries
import com.example.statspos.domain.repository.warehouse.StockEntriesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockTransferEntriesViewModel @Inject constructor(
    private val api: StockEntriesRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<StockEntries> = emptyList(),
        val totalStockEntries: Int = 0,

        val warehouse: DropdownItem = HP.getNoneDropdownItem(),

        val isLoading: Boolean = false,
        val error: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun beforeRequest() {
        state.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }
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
    fun onWarehouseSelected(value: DropdownItem) {
        state.update { it.copy(warehouse = value) }
    }
    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("warehouseId", state.value.warehouse.id)
            }

            when (val result = api.loadStockEntries(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalStockEntries").asInt
                    val resultList =
                        Gson().getListOf<StockEntries>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalStockEntries = resultTotal,
                        )
                    }
                }
            }
        }
    }

    fun transferStock(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (!isValid())
                return@launch

            beforeRequest()

            when (val result = api.stockTransferToWarehouse(state.value.warehouse.id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    onSuccess()
                    loadData()
                }
            }
        }
    }

    fun receiveStock(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (!isValid())
                return@launch

            beforeRequest()

            when (val result = api.stockReceiveFromWarehouse(state.value.warehouse.id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    onSuccess()
                    loadData()
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun isValid(): Boolean {
        if (state.value.warehouse.id == 0L) {
            showMessage("Please select warehouse")
            return false
        }

        if (state.value.list.isEmpty()) {
            showMessage("Please enter items to transfer")
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
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}