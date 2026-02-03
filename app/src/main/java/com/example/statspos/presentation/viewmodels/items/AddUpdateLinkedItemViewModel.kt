package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.items.LinkedItems
import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.items.LinkedItemsRepository
import com.example.statspos.domain.repository.items.SubBarcodesRepository
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
class AddUpdateLinkedItemViewModel @Inject constructor(
    private val api: LinkedItemsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val itemId: Long = 0L,
        val linkedItemId: Long = 0L,

        val updateCost: Boolean = false,
        val updateRetail: Boolean = false,
        val updateWholesale: Boolean = false,
        val updateCrtnRate: Boolean = false,
        val updateMarketPrice: Boolean = true,
        val updateExpiry: Boolean = true,
        val rateFormula: String = "",

        // Extra
        val itemname: String = "",
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

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
    fun onUpdateCostChange(value: Boolean) {
        state.update { it.copy(updateCost = value) }
    }
    fun onUpdateRetailChange(value: Boolean) {
        state.update { it.copy(updateRetail = value) }
    }
    fun onUpdateWholesaleChange(value: Boolean) {
        state.update { it.copy(updateWholesale = value) }
    }
    fun onUpdateCrtnRateChange(value: Boolean) {
        state.update { it.copy(updateCrtnRate = value) }
    }
    fun onUpdateMarketPriceChange(value: Boolean) {
        state.update { it.copy(updateMarketPrice = value) }
    }
    fun onRateFormulaChange(value: String) {
        state.update { it.copy(rateFormula = value) }
    }
    fun onLinkedItemIdChange(value: Long) {
        state.update { it.copy(linkedItemId = value) }
    }
    fun onItemnameChange(value: String) {
        state.update { it.copy(
            itemname = value,
            linkedItemId = 0L,
        ) }
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

            val linkedItem = getFormData()

            val result = if (state.value.isUpdate) {
                linkedItem.id = state.value.updateId
                api.updateLinkedItem(linkedItem)
            } else {
                api.insertLinkedItem(linkedItem)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {
                    clearTextboxes()
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

            when (val result = api.deleteLinkedItem(id)) {
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

            when (val result = api.getLinkedItem(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val linkedItem = Gson().get<LinkedItems>(result.data.asJsonObject)
                    setFormData(linkedItem)
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

            beforeRequest()

            when (val result = itemsRepo.isBarcodeExists(value)) {
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
                                linkedItemId = item.id!!,
                            )
                        }
                    } else {
                        state.update {
                            it.copy(
                                linkedItemId = 0L,
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
    private fun getFormData(): LinkedItems {
        return LinkedItems(
            itemId = state.value.itemId,
            linkedItemId = state.value.linkedItemId,

            updateCost = state.value.updateCost,
            updateRetail = state.value.updateRetail,
            updateWholesale = state.value.updateWholesale,
            updateCrtnRate = state.value.updateCrtnRate,
            updateMarketPrice = state.value.updateMarketPrice,
            updateExpiry = state.value.updateExpiry,
            rateFormula = state.value.rateFormula,
        )
    }

    private fun setFormData(linkedItem: LinkedItems) {
        state.update {
            it.copy(
                itemname =  linkedItem.itemname!!,
                linkedItemId = linkedItem.linkedItemId!!,

                updateCost = linkedItem.updateCost!!,
                updateRetail = linkedItem.updateRetail!!,
                updateWholesale = linkedItem.updateWholesale!!,
                updateCrtnRate = linkedItem.updateCrtnRate!!,
                updateMarketPrice = linkedItem.updateMarketPrice!!,
                updateExpiry = linkedItem.updateExpiry!!,
                rateFormula = linkedItem.rateFormula!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                linkedItemId = 0L,

                updateCost =  false,
                updateRetail =  false,
                updateWholesale =  false,
                updateCrtnRate =  false,
                updateMarketPrice =  true,
                updateExpiry =  true,
                rateFormula =  "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.linkedItemId == 0L) {
            showMessage("Please select item")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, itemId: Long) {
        state.update { it.copy(
            isUpdate = isUpdate,
            updateId = updateId,
            itemId = itemId,
        ) }
    }

    // endregion
}