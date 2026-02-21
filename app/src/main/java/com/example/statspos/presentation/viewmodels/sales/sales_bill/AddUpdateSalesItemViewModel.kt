package com.example.statspos.presentation.viewmodels.sales.sales_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesItems
import com.example.statspos.domain.repository.sales.SalesItemsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
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
class AddUpdateSalesItemViewModel @Inject constructor(
    private val api: SalesItemsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val invoiceId: Long = 0L,
        val itemId: Long = 0L,
        val itemname: String = "",
        val urduname: String = "",

        val qty: String = "",
        val crtn: String = "",
        val cost: Double = 0.0,
        val rate: String = "",
        val crtnRate: String = "",
        val retail: Double = 0.0,
        val wholesale: Double = 0.0,
        val rate3: Double = 0.0,
        val rate4: Double = 0.0,
        val crtnSize: Int = 0,
        val itemNo: Int = 0,

        var isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs == true,
        val disc: String = "",
        var calculatedDisc: Double = 0.0,
        var totalDisc: Double = 0.0,
        val isRetail: Boolean = false,

        var total: Double = 0.0,
        var totalCost: Double = 0.0,
        var profit: Double = 0.0,


        // Extra
        val isPostedBill: Boolean = false,
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,
        val sales: Sales = Sales(),

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

    fun onQtyChange(value: String) {
        state.update { it.copy(qty = value) }
        updateTotal()
    }

    fun onCrtnChange(value: String) {
        state.update { it.copy(crtn = value) }
        updateTotal()
    }

    fun onRateChange(value: String) {
        state.update { it.copy(rate = value) }
        updateTotal()
    }

    fun onCrtnRateChange(value: String) {
        state.update { it.copy(crtnRate = value) }
        updateTotal()
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
        updateTotal()
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
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

            val salesItem = getFormData()
            salesItem.isPostedBill = state.value.isPostedBill
            salesItem.salesType = state.value.sales.salesType!!
            salesItem.isEstimatedBill = state.value.sales.isEstimatedBill!!

            val result = if (state.value.isUpdate) {
                salesItem.id = state.value.updateId
                api.updateSalesItem(salesItem)
            } else {
                api.insertSalesItem(salesItem)
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

            when (val result = api.deleteSalesItem(id, state.value.isPostedBill)) {
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

            when (val result = api.getSalesItem(id, state.value.isPostedBill)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val salesItem = Gson().get<SalesItems>(result.data.asJsonObject)
                    setFormData(salesItem)
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

            val body = JsonObject().apply {
                addProperty("barcode", value)
                addProperty("salesId", state.value.invoiceId)
                addProperty("isPostedBill", state.value.isPostedBill)
                addProperty("customerId", state.value.sales.customerId!!)
                addProperty("isEstimatedBill", state.value.sales.isEstimatedBill!!)
                addProperty("salesType", state.value.sales.salesType!!)
            }
            when (val result = api.isBarcodeExists(body)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val isExists = result.data.get("isExists").asBoolean
                    if (isExists) {
                        val item = Gson().get<Items>(result.data.get("data").asJsonObject)
                        showDataIntoTextboxes(item)
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
    private fun showDataIntoTextboxes(item: Items) {
        state.update {
            it.copy(
                itemname = item.itemname!!,
                itemId = item.id!!,
                cost = item.cost!!,
                rate = item.retail.toString(),
            )
        }
    }

    private fun getFormData(): SalesItems {
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val cost = state.value.cost
        val crtnSize = state.value.crtnSize
        val total = state.value.total
        val totalCost = cost * (qty + (crtn * crtnSize))
        val profit = total - totalCost

        val salesItem = SalesItems(
            salesId = state.value.invoiceId,
            invoiceNo = state.value.sales.invoiceNo,
            itemId = state.value.itemId,
            itemname = state.value.itemname,
            urduname = state.value.urduname,

            qty = qty,
            crtn = crtn,
            rate = HP.getDoubleValue(state.value.rate),
            retail = state.value.retail,
            wholesale = state.value.wholesale,
            rate3 = state.value.rate3,
            rate4 = state.value.rate4,
            crtnRate = HP.getDoubleValue(state.value.crtnRate),
            crtnSize = state.value.crtnSize,

            isDiscRsPer = state.value.isDiscRsPer,
            disc = HP.getDoubleValue(state.value.disc),
            calculatedDisc = state.value.calculatedDisc,
            totalDisc = state.value.totalDisc,
            isRetail = state.value.isRetail,

            total = total,
            totalCost = totalCost,
            profit = profit,
        )

        if(!state.value.isUpdate){
            salesItem.itemNo = state.value.sales.totalItems!! + 1
        }

        return salesItem
    }

    private fun setFormData(salesItem: SalesItems) {
        state.update {
            it.copy(
                itemname = salesItem.itemname!!,
                itemId = salesItem.itemId!!,
                qty = salesItem.qty.toString(),
                rate = salesItem.rate.toString(),
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                itemId = 0L,
                qty = "",
                rate = "",

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

        if (HP.getDoubleValue(state.value.qty) == 0.0) {
            showMessage("Please enter quantity")
            return false
        }

        if (HP.getDoubleValue(state.value.rate) == 0.0) {
            showMessage("Please enter rate")
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

    fun updateInitialState(
        isUpdate: Boolean,
        updateId: Long,
        sales:Sales,
    ) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,

                sales = sales,
                invoiceId = sales.id!!,
                isPostedBill = sales.isPostedBill!!,
                isRetail = sales.isRetail!!,
            )
        }
    }

    private fun updateTotal() {
        val qty = HP.getDoubleValue(state.value.qty)
        val rate = HP.getDoubleValue(state.value.rate)

        state.update {
            it.copy(
                total = (qty * rate)
            )
        }
    }
    // endregion
}