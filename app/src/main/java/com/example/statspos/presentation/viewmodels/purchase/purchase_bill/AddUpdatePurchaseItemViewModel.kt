package com.example.statspos.presentation.viewmodels.purchase.purchase_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.models.purchase.PurchaseItems
import com.example.statspos.domain.repository.purchase.PurchaseItemsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
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
import javax.inject.Inject

@HiltViewModel
class AddUpdatePurchaseItemViewModel @Inject constructor(
    private val api: PurchaseItemsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val invoiceId: Long = 0L,
        val invoiceNo: Int = 0,
        val itemId: Long = 0L,
        val itemname: String = "",

        val cost: String = "",
        val finalCost: Double = 0.0,
        val qty: String = "",
        val crtn: String = "",
        val retail: String = "",
        val wholesale: String = "",
        val rate3: String = "",
        val rate4: String = "",
        val crtnRate: String = "",
        val crtnSize: Int = 0,
        val marketPrice: String = "",

        var isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs == true,
        val disc: String = "",
        var calculatedDisc: Double = 0.0,
        var totalDisc: Double = 0.0,

        var tax: String = "",
        var calculatedTax: Double = 0.0,
        var totalTax: Double = 0.0,

        var total: Double = 0.0,
        var grossTotal: Double = 0.0,

        var expiry: LocalDate = LocalDate.now(),
        var isNewStock: Boolean = false,
        val lockPcs: Boolean = false,
        val lockCrtn: Boolean = false,

        // Extra
        val costCrtn: Double = 0.0,
        val itemCost: Double = 0.0,
        val stockPcs: Double = 0.0,
        val stockCrtn: Long = 0L,
        val stockWarningMax: Int = 0,
        val warehouseStock: String = "",
        val oldRates: String = "",
        val qtyEnabled: Boolean = true,
        val crtnEnabled: Boolean = true,
        val retailEnabled: Boolean = true,
        val wholesaleEnabled: Boolean = true,
        val crtnRateEnabled: Boolean = true,
        val freezeDisc: Boolean = true,
        val freezeTax: Boolean = true,

        val isExists: Boolean = false,
        val isExistsResult: Boolean = false,
        val stockWarningResult: Boolean = false,

        val isPostedBill: Boolean = false,
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,
        val purchase: Purchase = Purchase(),

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

    fun onCostChange(value: String) {
        state.update { it.copy(cost = value) }
        updateTotal()
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

    fun onMarketPriceChange(value: String) {
        state.update { it.copy(marketPrice = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
        updateTotal()
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
        updateTotal()
    }

    fun onTaxChange(value: String) {
        state.update { it.copy(tax = value) }
        updateTotal()
    }

    fun onIsNewStockChange(value: Boolean) {
        state.update { it.copy(isNewStock = value) }
    }

    fun onLockPcsChange(value: Boolean) {
        state.update { it.copy(lockPcs = value) }
    }

    fun onLockCrtnChange(value: Boolean) {
        state.update { it.copy(lockCrtn = value) }
    }

    fun onExpiryChange(value: LocalDate) {
        state.update { it.copy(expiry = value) }
    }

    fun onFreezeDiscChange(value: Boolean) {
        state.update { it.copy(freezeDisc = value) }
    }

    fun onFreezeTaxChange(value: Boolean) {
        state.update { it.copy(freezeTax = value) }
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

            val purchaseItem = getFormData()
            purchaseItem.isPostedBill = state.value.isPostedBill

            val result = if (state.value.isUpdate) {
                purchaseItem.id = state.value.updateId
                api.updatePurchaseItem(purchaseItem)
            } else {
                api.insertPurchaseItem(purchaseItem)
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

            when (val result = api.deletePurchaseItem(id, state.value.isPostedBill)) {
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

            when (val result = api.getPurchaseItem(id, state.value.isPostedBill)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val purchaseItem = Gson().get<PurchaseItems>(result.data.asJsonObject)
                    val item = Gson().get<Items>(result.data.get("item").asJsonObject)

                    setFormData(purchaseItem, item)
                    updateTotal()
                }
            }
        }
    }

    fun getItem(value: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (value.isEmpty())
                return@launch

//            beforeRequest()

            val body = JsonObject().apply {
                addProperty("barcode", value)
                addProperty("purchaseId", state.value.invoiceId)
                addProperty("isPostedBill", state.value.isPostedBill)
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
                        onSuccess()
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
                itemId = item.id!!,

                cost = if (item.lastCost!! == 0.0) item.cost.toString() else item.lastCost.toString(),
                retail = item.retail.toString(),
                wholesale = item.wholesale.toString(),
                rate3 = item.rate3.toString(),
                rate4 = item.rate4.toString(),
                crtnRate = item.crtnRate.toString(),
                crtnSize = item.crtnSize!!,
                marketPrice = item.marketPrice.toString(),

                isDiscRsPer = item.isDiscRsPer!!,
                disc = item.disc.toString(),

                stockPcs = if (HP.settings.showItemStock == true) item.stockPcs!! else 0.0,
                stockCrtn = if (HP.settings.showItemStock == true) item.stockCrtn!! else 0,
                stockWarningMax = item.stockWarningMax!!,
                lockPcs = item.lockPcs!!,
                lockCrtn = item.lockCrtn!!,
                expiry = HP.toLocalDate(item.expiry!!),

                // Extras
                itemCost = item.cost!!,
                isExists = item.isExists!!,
            )
        }

        changeCartonOptions()
        showOldRates(item.oldRates.toString())
        showWarehouseStock(item.warehouseStock.toString())
        updateTotal()
    }

    private fun getFormData(): PurchaseItems {
        val purchaseItem = PurchaseItems(
            purchaseId = state.value.invoiceId,
            invoiceNo = state.value.purchase.invoiceNo,
            itemId = state.value.itemId,
            itemname = state.value.itemname,

            cost = HP.getDoubleValue(state.value.cost),
            finalCost = state.value.finalCost,
            qty = HP.getDoubleValue(state.value.qty),
            crtn = HP.getIntValue(state.value.crtn),

            retail = HP.getDoubleValue(state.value.retail),
            wholesale = HP.getDoubleValue(state.value.wholesale),
            rate3 = HP.getDoubleValue(state.value.rate3),
            rate4 = HP.getDoubleValue(state.value.rate4),
            crtnRate = HP.getDoubleValue(state.value.crtnRate),
            crtnSize = state.value.crtnSize,
            marketPrice = HP.getDoubleValue(state.value.marketPrice),

            isDiscRsPer = state.value.isDiscRsPer,
            disc = HP.getDoubleValue(state.value.disc),
            calculatedDisc = state.value.calculatedDisc,
            totalDisc = state.value.totalDisc,

            tax = HP.getDoubleValue(state.value.tax),
            totalTax = state.value.totalTax,

            total = state.value.total,

            expiry = HP.getZonedDate(state.value.expiry),
            isNewStock = state.value.isNewStock,
            lockPcs = state.value.lockPcs,
            lockCrtn = state.value.lockCrtn,

            )

        return purchaseItem
    }

    private fun setFormData(purchaseItem: PurchaseItems, item: Items) {
        state.update {
            it.copy(
                itemname = purchaseItem.itemname!!,
                itemId = purchaseItem.itemId!!,

                cost = purchaseItem.cost.toString(),
                finalCost = purchaseItem.finalCost!!,
                qty = HP.formatDecimal(purchaseItem.qty),
                crtn = purchaseItem.crtn.toString(),

                retail = purchaseItem.retail.toString(),
                wholesale = purchaseItem.wholesale.toString(),
                rate3 = purchaseItem.rate3.toString(),
                rate4 = purchaseItem.rate4.toString(),
                crtnRate = purchaseItem.crtnRate.toString(),
                crtnSize = purchaseItem.crtnSize!!,
                marketPrice = purchaseItem.marketPrice.toString(),

                isDiscRsPer = purchaseItem.isDiscRsPer!!,
                disc = purchaseItem.disc.toString(),
                calculatedDisc = purchaseItem.calculatedDisc!!,
                totalDisc = purchaseItem.totalDisc!!,

                tax = purchaseItem.tax.toString(),
                totalTax = purchaseItem.totalTax!!,

                total = purchaseItem.total!!,
                expiry = HP.toLocalDate(purchaseItem.expiry.toString()),
                isNewStock = purchaseItem.isNewStock!!,
                lockPcs = purchaseItem.lockPcs!!,
                lockCrtn = purchaseItem.lockCrtn!!,

                // Extras
                itemCost = purchaseItem.cost!!,
                stockPcs = item.stockPcs!!,
                stockCrtn = item.stockCrtn!!,
                stockWarningMax = item.stockWarningMax!!,
            )
        }

        changeCartonOptions()
        showOldRates(item.oldRates.toString())
        showWarehouseStock(item.warehouseStock.toString())
        updateTotal()
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                itemId = 0L,

                cost = "",
                finalCost = 0.0,
                qty = "",
                crtn = "",

                retail = "",
                wholesale = "",
                rate3 = "",
                rate4 = "",
                crtnRate = "",
                crtnSize = 0,
                marketPrice = "",

                isDiscRsPer = HP.settings.isDefaultDiscRs == true,
//                disc = "",
                calculatedDisc = 0.0,
                totalDisc = 0.0,

//                tax = "",
                calculatedTax = 0.0,
                totalTax = 0.0,

                grossTotal = 0.0,
                total = 0.0,
                expiry = LocalDate.now(),
                isNewStock = false,
                lockPcs = false,
                lockCrtn = false,

                // Extras
                isUpdate = false,
                updateId = 0L,

                costCrtn = 0.0,
                itemCost = 0.0,
                stockPcs = 0.0,
                stockCrtn = 0L,
                stockWarningMax = 0,
                warehouseStock = "",
                oldRates = "",
                qtyEnabled = true,
                crtnEnabled = true,
                retailEnabled = true,
                wholesaleEnabled = true,
                crtnRateEnabled = true,

                isExists = false,
                isExistsResult = false,
            )
        }

        if (!state.value.freezeDisc) {
            state.update { it.copy(disc = "") }
        }

        if (!state.value.freezeTax) {
            state.update { it.copy(tax = "") }
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

        if (!state.value.isUpdate) {
            if (state.value.isExists) {
                if (!state.value.isExistsResult) {
                    showConfirmDialog("Item already exists. You want to add?", 1)
                    return false
                }
            }
        }

        if (!state.value.isUpdate) {
            val stock = state.value.stockPcs + (state.value.stockCrtn * state.value.crtnSize)
            val qtyPurchased =
                HP.getDoubleValue(state.value.qty) + (HP.getDoubleValue(state.value.crtn) * state.value.crtnSize)
            val totalStock = stock + qtyPurchased

            if (state.value.stockWarningMax > 0 && totalStock >= state.value.stockWarningMax) {
                if (!state.value.stockWarningResult) {
                    showConfirmDialog(
                        "Item stock will be over from limit, You want to purchase?",
                        2
                    )
                    return false
                }
            }
        }

        return isValidCartonSize()
    }

    private fun isValidCartonSize(): Boolean {
        val retail = HP.getDoubleValue(state.value.retail)
        val wholesale = HP.getDoubleValue(state.value.wholesale)
        val crtnRate = HP.getDoubleValue(state.value.crtnRate)
        val crtnSize = state.value.crtnSize

        if (crtnSize == 0)
            return true
        else if (crtnSize == 1) {
            if (crtnRate == 0.0) {
                showMessage("Please enter carton rate")
                return false
            }
        } else {
            if (crtnRate == 0.0) {
                showMessage("Please enter carton rate")
                return false
            } else if (retail == 0.0 && wholesale == 0.0) {
                showMessage("Please enter sale rate")
                return false;
            }
        }

        return true;
    }

    private fun showOldRates(oldRates: String) {
        val values = Gson().get<JsonArray>(oldRates).map {
            it.toString().replace("\"", "")
        }

        var result = ""
        for (value in values) {
            val temp = value.split(" | ")
            result += "${temp[0]} ${temp[3]}\n${temp[1]}\n${temp[2]}\n\n"
        }

        state.update { it.copy(oldRates = result) }
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
        purchase: Purchase,
    ) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,

                purchase = purchase,
                invoiceId = purchase.id!!,
                isPostedBill = purchase.isPostedBill!!,
            )
        }
    }

    private fun updateTotal() {
        val cost = HP.getDoubleValue(state.value.cost)
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val crtnSize = state.value.crtnSize
        val totalQty = qty + (crtn * crtnSize)
        val disc = HP.getDoubleValue(state.value.disc)
        val tax = HP.getDoubleValue(state.value.tax)

        // Set Discount Labels
        val calculatedDisc = if (state.value.isDiscRsPer)
            disc
        else
            (disc / 100) * cost

        // Set Tax Labels
        val calculatedTax = (tax / 100) * (cost - calculatedDisc)

        // Set Final Cost Labels
        val finalCost = (cost - calculatedDisc) + calculatedTax

        // Set Total Label
        var total = cost * totalQty
        val grossTotal = cost * totalQty
        if (totalQty != 0.0)
            total = (total - (calculatedDisc * totalQty)) + (calculatedTax * totalQty)

        state.update {
            it.copy(
                // Discount
                calculatedDisc = calculatedDisc,
                totalDisc = if (totalQty != 0.0) (calculatedDisc * totalQty) else 0.0,

                // Tax
                calculatedTax = calculatedTax,
                totalTax = if (totalQty != 0.0) (calculatedTax * totalQty) else 0.0,

                // Final Cost
                finalCost = finalCost,
                costCrtn = (finalCost * crtnSize),

                // Total
                total = total,
                grossTotal = grossTotal,
            )
        }
    }

    fun setIsExistsResult(value: Boolean) {
        state.update { it.copy(isExistsResult = value) }
    }

    fun setStockWarningResult(value: Boolean) {
        state.update { it.copy(stockWarningResult = value) }
    }

    private fun changeCartonOptions() {
        if (HP.settings.saleCartons == true) {
            if (state.value.crtnSize == 0) {
                state.update {
                    it.copy(
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

            if (state.value.crtnSize == 1) {
                state.update {
                    it.copy(
                        qtyEnabled = false,
                        retailEnabled = false,
                        wholesaleEnabled = false,
                    )
                }
            } else {
                state.update {
                    it.copy(
                        qtyEnabled = true,
                        retailEnabled = true,
                        wholesaleEnabled = true,
                    )
                }
            }
        } else {
            state.update {
                it.copy(
                    crtnEnabled = false,
                    crtnRateEnabled = false,
                )
            }
        }
    }

    // endregion
}