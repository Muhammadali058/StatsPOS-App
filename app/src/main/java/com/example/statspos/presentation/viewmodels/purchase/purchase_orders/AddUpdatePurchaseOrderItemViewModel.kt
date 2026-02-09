package com.example.statspos.presentation.viewmodels.purchase.purchase_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.purchase.PurchaseOrderItems
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.purchase.PurchaseOrderItemsRepository
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
class AddUpdatePurchaseOrderItemViewModel @Inject constructor(
    private val api: PurchaseOrderItemsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val purchaseOrderId: Long = 0L,
        val itemId: Long = 0L,

        val qty: String = "",
        val crtn: String = "",
        val cost: String = "",
        val total: String = "",

        // Extra
        val purchaseOrderName: String = "",
        val itemname: String = "",
        val crtnSize: Int = 0,

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
    fun onItemnameChange(value: String) {
        state.update {
            it.copy(
                itemname = value,
                itemId = 0L,
            )
        }
    }

    fun onPurchaseOrderNameChange(value: String) {
        state.update { it.copy(purchaseOrderName = value) }
    }

    fun onPurchaseOrderIdChange(value: Long) {
        state.update { it.copy(purchaseOrderId = value) }
    }

    fun onQtyChange(value: String) {
        state.update { it.copy(qty = value) }
        updateTotal()
    }

    fun onCrtnChange(value: String) {
        state.update { it.copy(crtn = value) }
        updateTotal()
    }

    fun onCostChange(value: String) {
        state.update { it.copy(cost = value) }
        updateTotal()
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

            val purchaseOrderItem = getFormData()

            val result = if (state.value.isUpdate) {
                purchaseOrderItem.id = state.value.updateId
                api.updatePurchaseOrderItem(purchaseOrderItem)
            } else {
                api.insertPurchaseOrderItem(purchaseOrderItem)
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

            when (val result = api.deletePurchaseOrderItem(id)) {
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

            when (val result = api.getPurchaseOrderItem(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val purchaseOrderItem = Gson().get<PurchaseOrderItems>(result.data.asJsonObject)
                    setFormData(purchaseOrderItem)
                    updateTotal()
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
                                itemId = item.id!!,
                                cost = if (item.lastCost == 0.0) item.cost.toString() else item.lastCost.toString(),
                                crtnSize = item.crtnSize!!,
                            )
                        }
                    } else {
                        state.update {
                            it.copy(
                                itemId = 0L,
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
    private fun getFormData(): PurchaseOrderItems {
        return PurchaseOrderItems(
            purchaseOrderId = state.value.purchaseOrderId,
            itemId = state.value.itemId,

            qty = HP.getDoubleValue(state.value.qty),
            crtn = HP.getIntValue(state.value.crtn),
            cost = HP.getDoubleValue(state.value.cost),
            total = HP.getDoubleValue(state.value.total),
        )
    }

    private fun setFormData(purchaseOrderItem: PurchaseOrderItems) {
        state.update {
            it.copy(
                itemname = purchaseOrderItem.itemname!!,
                itemId = purchaseOrderItem.itemId!!,

                qty = purchaseOrderItem.qty.toString(),
                crtn = purchaseOrderItem.crtn.toString(),
                cost = purchaseOrderItem.cost.toString(),
                total = purchaseOrderItem.total.toString(),

                crtnSize = purchaseOrderItem.crtnSize!!,
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
                cost = "",
                total = "",

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

        if (HP.getDoubleValue(state.value.cost) == 0.0) {
            showMessage("Please enter cost")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, purchaseOrderId: Long) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,
                purchaseOrderId = purchaseOrderId,
                purchaseOrderName = if (purchaseOrderId == 0L) "" else HP.getDropdownNameById(
                    purchaseOrderId,
                    HP.purchaseOrders
                )
            )
        }
    }

    private fun updateTotal() {
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val cost = HP.getDoubleValue(state.value.cost)

        val total = cost * (qty + (crtn * state.value.crtnSize))
        state.update {
            it.copy(
                total = total.toString()
            )
        }
    }
    // endregion
}