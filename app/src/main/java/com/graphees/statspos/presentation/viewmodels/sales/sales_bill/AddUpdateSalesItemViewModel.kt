package com.graphees.statspos.presentation.viewmodels.sales.sales_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.items.Items
import com.graphees.statspos.domain.models.sales.Sales
import com.graphees.statspos.domain.models.sales.SalesItems
import com.graphees.statspos.domain.repository.sales.SalesItemsRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
        val rates: List<String> = emptyList(),
        val retail: Double = 0.0,
        val wholesale: Double = 0.0,
        val rate3: Double = 0.0,
        val rate4: Double = 0.0,
        val crtnSize: Int = 0,
        val itemNo: Int = 0,
        val marketPrice: Double = 0.0,

        var isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs == true,
        val disc: String = "",
        var calculatedDisc: Double = 0.0,
        var totalDisc: Double = 0.0,
        val isRetail: Boolean = false,

        var total: Double = 0.0,
        var totalCost: Double = 0.0,
        var profit: Double = 0.0,


        // Extra
        val stockPcs: Double = 0.0,
        val stockCrtn: Long = 0L,
        val warehouseStock: String = "",
        val lastRate: Double = 0.0,
        val lastCrtnRate: Double = 0.0,
        val lockPcs: Boolean = false,
        val lockCrtn: Boolean = false,
        val qtyEnabled: Boolean = true,
        val crtnEnabled: Boolean = true,
        val rateEnabled: Boolean = true,
        val crtnRateEnabled: Boolean = true,

        val isExists: Boolean = false,
        val isExistsResult: Boolean = false,
        val expirable: Boolean = false,
        val expiry: LocalDate = LocalDate.now(),
        val expirableResult: Boolean = false,


        val isPostedBill: Boolean = false,
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,
        val sales: Sales = Sales(),

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

            is UiEvent.ShowConfirmDialog -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowConfirmDialog(event.message, event.type))
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

    fun showConfirmDialog(message: String, type: Int) {
        onEvent(UiEvent.ShowConfirmDialog(message, type))
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

        if (value.isEmpty())
            clearTextboxes()
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
                    val item = Gson().get<Items>(result.data.get("item").asJsonObject)

                    setFormData(salesItem, item)
                    updateTotal()
                }
            }
        }
    }

    fun getItem(value: String, onSuccess: (Items) -> Unit) {
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
                        onSuccess(item)
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
                isUpdate = false,
                updateId = 0L,

                itemname = item.itemname!!,
                urduname = item.urduname!!,
                itemId = item.id!!,

                cost = item.cost!!,
                rate = if (state.value.isRetail) item.retail.toString() else item.wholesale.toString(),
                retail = item.retail!!,
                wholesale = item.wholesale!!,
                rate3 = item.rate3!!,
                rate4 = item.rate4!!,
                crtnRate = item.crtnRate.toString(),
                crtnSize = item.crtnSize!!,

//                isDiscRsPer = item.isDiscRsPer!!,
//                disc = item.disc.toString(),

                // Extras
                stockPcs = if (HP.settings.showItemStock == true) item.stockPcs!! else 0.0,
                stockCrtn = if (HP.settings.showItemStock == true) item.stockCrtn!! else 0,
//                lastRate = if (HP.settings.showCustomerLastRate == true && state.value.sales.customerId != 0L) item.lastRate!! else 0.0,
//                lastCrtnRate = if (HP.settings.showCustomerLastRate == true && state.value.sales.customerId != 0L) item.lastCrtnRate!! else 0.0,
                lockPcs = item.lockPcs!!,
                lockCrtn = item.lockCrtn!!,
                isExists = item.isExists!!,
                expirable = item.expirable!!,
                expiry = HP.toLocalDate(item.expiry!!),

                marketPrice = item.marketPrice!!,
            )
        }

        // Customer Discount
        if (state.value.sales.customerId != 0L) {
            val disc = HP.getDoubleValue(item.customerDisc.toString())

            if (disc > 0) {
                state.update {
                    it.copy(
                        isDiscRsPer = item.customerIsDiscRsPer!!,
                        disc = item.customerDisc.toString(),
                    )
                }
            } else {
                state.update {
                    it.copy(
                        isDiscRsPer = item.isDiscRsPer!!,
                        disc = item.disc.toString(),
                    )
                }
            }
        } else {
            state.update {
                it.copy(
                    isDiscRsPer = item.isDiscRsPer!!,
                    disc = item.disc.toString(),
                )
            }
        }

        // If always use last rate
        if (!state.value.isUpdate && HP.settings.alwaysUseLastRate == true) {
            if (state.value.sales.customerId != 0L) {
                if (item.lastRate!! > 0.0) {
                    state.update {
                        it.copy(
                            rate = item.lastRate.toString()
                        )
                    }
                }
                if (item.lastCrtnRate!! > 0.0) {
                    state.update {
                        it.copy(
                            crtnRate = item.lastCrtnRate.toString()
                        )
                    }
                }
            }
        }

        showSellingRatesIntoList()
        showCustomerLastRate(item)
        showWarehouseStock(item.warehouseStock.toString())
        updateTotal()
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

            cost = state.value.cost,
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

            marketPrice = state.value.marketPrice,
        )

//        if (!state.value.isUpdate) {
//            salesItem.itemNo = state.value.sales.totalItems!! + 1
//        }

        return salesItem
    }

    private fun setFormData(salesItem: SalesItems, item: Items) {
        state.update {
            it.copy(
                itemname = salesItem.itemname!!,
                urduname = salesItem.urduname!!,
                itemId = salesItem.itemId!!,

                cost = salesItem.cost!!,
                qty = HP.formatDecimal(salesItem.qty),
                crtn = salesItem.crtn.toString(),
                rate = salesItem.rate.toString(),
                retail = salesItem.retail!!,
                wholesale = salesItem.wholesale!!,
                rate3 = salesItem.rate3!!,
                rate4 = salesItem.rate4!!,
                crtnRate = salesItem.crtnRate.toString(),
                crtnSize = salesItem.crtnSize!!,
                marketPrice = salesItem.marketPrice!!,

                isDiscRsPer = salesItem.isDiscRsPer!!,
                disc = salesItem.disc.toString(),
                calculatedDisc = salesItem.calculatedDisc!!,
                totalDisc = salesItem.totalDisc!!,
                isRetail = salesItem.isRetail!!,

                total = salesItem.total!!,
                totalCost = salesItem.totalCost!!,
                profit = salesItem.profit!!,

                // Extras
                stockPcs = if (HP.settings.showItemStock == true) salesItem.stockPcs!! else 0.0,
                stockCrtn = if (HP.settings.showItemStock == true) salesItem.stockCrtn!! else 0,
                lockPcs = salesItem.lockPcs!!,
                lockCrtn = salesItem.lockCrtn!!,
            )
        }

        showSellingRatesIntoList()
        showWarehouseStock(item.warehouseStock.toString())
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                urduname = "",
                itemId = 0L,

                cost = 0.0,
                qty = "",
                crtn = "",
                rate = "",
                retail = 0.0,
                wholesale = 0.0,
                rate3 = 0.0,
                rate4 = 0.0,
                crtnRate = "",
                crtnSize = 0,
                marketPrice = 0.0,

                isDiscRsPer = HP.settings.isDefaultDiscRs == true,
                disc = "",
                calculatedDisc = 0.0,
                totalDisc = 0.0,
                isRetail = state.value.sales.isRetail!!,

                total = 0.0,
                totalCost = 0.0,
                profit = 0.0,

                // Extras
                isUpdate = false,
                updateId = 0L,

                stockPcs = 0.0,
                stockCrtn = 0L,
                warehouseStock = "",
                lastRate = 0.0,
                lastCrtnRate = 0.0,
                lockPcs = false,
                lockCrtn = false,
                qtyEnabled = true,
                crtnEnabled = true,
                rateEnabled = true,
                crtnRateEnabled = true,

                isExists = false,
                isExistsResult = false,
                expirable = false,
                expiry = LocalDate.now(),
                expirableResult = false,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.itemId == 0L) {
            showMessage("Please select item")
            return false
        }

        if (state.value.total == 0.0) {
            showMessage("Please enter quantity or rate")
            return false
        }

        if (!state.value.isUpdate && HP.settings.itemExistsInSalesWarning == true) {
            if (state.value.isExists) {
                if (!state.value.isExistsResult) {
                    showConfirmDialog("Item already exists. You want to add?", 1)
                    return false
                }
            }
        }

        if (!state.value.isUpdate && state.value.expirable) {
            val today = LocalDate.now()
            val date = state.value.expiry

            val days = ChronoUnit.DAYS.between(today, date)
            if (days <= 0) {
                if (!state.value.expirableResult) {
                    showConfirmDialog("Item expired. You want to add?", 2)
                    return false
                }
            }
        }

        return true
    }

    private fun showSellingRatesIntoList() {
        val list = if (HP.settings.fourRateSystem == true) {
            listOf(
                state.value.retail.toString(),
                state.value.wholesale.toString(),
                state.value.rate3.toString(),
                state.value.rate4.toString(),
            )
        } else {
            listOf(
                state.value.retail.toString(),
                state.value.wholesale.toString(),
            )
        }

        state.update {
            it.copy(
                rates = list,
            )
        }

        // Lock
        if (state.value.lockPcs) {
            state.update {
                it.copy(
                    qty = "",
                    qtyEnabled = false,
                    rateEnabled = false,
                )
            }
        } else {
            state.update {
                it.copy(
                    qtyEnabled = true,
                    rateEnabled = true,
                )
            }
        }

        if (state.value.lockCrtn) {
            state.update {
                it.copy(
                    crtn = "",
                    crtnEnabled = false,
                    crtnRateEnabled = false,
                )
            }
        } else {
            state.update {
                it.copy(
                    crtnEnabled = true,
                    crtnRateEnabled = true,
                )
            }
        }
    }

    private fun showCustomerLastRate(item: Items) {
        if (HP.settings.showCustomerLastRate == true && state.value.sales.customerId != 0L) {
            if(HP.settings.alwaysUseLastRate == true){
                state.update {
                    it.copy(
                        lastRate = if (state.value.isRetail) item.retail!! else item.wholesale!!,
                        lastCrtnRate = item.crtnRate!!,
                    )
                }
            }else {
                state.update {
                    it.copy(
                        lastRate = item.lastRate!!,
                        lastCrtnRate = item.lastCrtnRate!!,
                    )
                }
            }
        }
    }

    private fun showWarehouseStock(warehouseStock: String) {
        val stock = Gson().get<JsonArray>(warehouseStock).map {
            it.toString()
        }
        val formattedText = stock
            .joinToString("\n")
            .replace("\"", "")
            .replace(" \\n", ":")

        state.update { it.copy(warehouseStock = formattedText) }
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
        sales: Sales,
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
        val crtn = HP.getIntValue(state.value.crtn)
//        val cost = state.value.cost
//        val crtnSize = state.value.crtnSize
        val rate = HP.getDoubleValue(state.value.rate)
        val crtnRate = HP.getDoubleValue(state.value.crtnRate)

        var total = qty * rate
        total += crtn * crtnRate

        val totalDisc = getDiscount()
        total -= totalDisc

//        val totalCost = cost * (qty + (crtn * crtnSize))
//        val profit = total - totalCost

        state.update {
            it.copy(
                total = total,
                totalDisc = totalDisc,
//                totalCost = totalCost,
//                profit = profit,
            )
        }
    }

    private fun getDiscount(): Double {
        if (state.value.itemId == 0L)
            return 0.0
        else {
            val disc = HP.getDoubleValue(state.value.disc)
            val qty = HP.getDoubleValue(state.value.qty)
            if (state.value.isDiscRsPer) {
                val totalDisc = qty * disc
                state.update { it.copy(calculatedDisc = disc) }
                return totalDisc
            } else {
                val rate = HP.getDoubleValue(state.value.rate)
                val totalDisc = (disc / 100) * rate
                state.update { it.copy(calculatedDisc = totalDisc) }

                return totalDisc * qty
            }
        }
    }

    fun setIsExistsResult(value: Boolean) {
        state.update { it.copy(isExistsResult = value) }
    }

    fun setExpirableResult(value: Boolean) {
        state.update { it.copy(expirableResult = value) }
    }
    // endregion
}