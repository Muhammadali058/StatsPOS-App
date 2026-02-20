package com.example.statspos.presentation.viewmodels.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBillItems
import com.example.statspos.domain.repository.sales.SalesItemsRepository
import com.example.statspos.domain.repository.sales.SalesRepository
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
class SalesItemsViewModel @Inject constructor(
    private val api: SalesItemsRepository,
    private val salesRepo: SalesRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<SalesBillItems> = emptyList(),
        val totalItems: Int = 0,

        val grandTotal: Double = 0.0,
        val totalCost: Double = 0.0,
        val totalQty: Double = 0.0,
        val totalCrtn: Int = 0,
        val totalItemDisc: Double = 0.0,


        val total: Double = 0.0,
        val cost: Double = 0.0,
        val profit: Double = 0.0,
        val totalDisc: Double = 0.0,

        // Extras
        val search: String = "",
        val invoiceId: Long = 0L,
        val isPostedBill: Boolean = false,
        val sales: Sales? = null,

        val isLoading: Boolean = false,
        val isDeleting: Boolean = false,
        val isPosting: Boolean = false,
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
    fun onSearchChange(value: String) {
        state.update { it.copy(search = value) }
    }
    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.invoiceId == 0L)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("salesId", state.value.invoiceId)
                addProperty("isPostedBill", state.value.isPostedBill)
                addProperty("text", state.value.search)
            }

            when (val result = api.loadSalesItems(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalItems").asInt

                    val grandTotal =
                        result.data.get("total").asJsonObject.get("grandTotal").asDouble
                    val totalCost =
                        result.data.get("total").asJsonObject.get("totalCost").asDouble
                    val totalQty =
                        result.data.get("total").asJsonObject.get("totalQty").asDouble
                    val totalCrtn =
                        result.data.get("total").asJsonObject.get("totalCrtn").asInt
                    val totalDisc =
                        result.data.get("total").asJsonObject.get("totalDisc").asDouble

                    val resultList =
                        Gson().getListOf<SalesBillItems>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalItems = resultTotal,

                            grandTotal = grandTotal,
                            totalCost = totalCost,
                            totalQty = totalQty,
                            totalCrtn = totalCrtn,
                            totalItemDisc = totalDisc,
                        )
                    }
                    updateTotal()
                }
            }
        }
    }

    fun postBill(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isDeleting)
                return@launch

            if (state.value.isPosting)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isPosting = true) }

            val sale = getFormData()

            val result = if (state.value.isPostedBill) {
                sale.salesId = state.value.invoiceId
                salesRepo.updateSales(sale)
            } else {
                sale.salesId = state.value.invoiceId
                salesRepo.insertSales(sale)
            }

            state.update { it.copy(isPosting = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isDeleting)
                return@launch

            state.update { it.copy(isDeleting = true) }
            val result = salesRepo.deleteSales(id, state.value.isPostedBill)
            state.update { it.copy(isDeleting = false) }

            when (result) {
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

    // region Methods
    private fun getFormData(): Sales {
        val sale = Sales(
            total = state.value.total,
            cost = state.value.cost,
            profit = state.value.profit,
            isDiscRsPer = state.value.sales!!.isDiscRsPer,
            disc = state.value.sales!!.disc,
            totalDisc = state.value.totalDisc,

            payment = state.value.sales!!.payment,
            change = state.value.sales!!.change,
            salesOn = state.value.sales!!.salesOn!!,
            salesType = state.value.sales!!.salesType!!,
            isMopCashBank = state.value.sales!!.isMopCashBank!!,

            customerId = state.value.sales!!.customerId,
            customerName = state.value.sales!!.customerName,
            bankId = state.value.sales!!.bankId,
            subBankId = state.value.sales!!.subBankId,
            supplierId = state.value.sales!!.supplierId,

            isRetail = state.value.sales!!.isRetail,
            date = state.value.sales!!.date.toString(),
            dueDate = state.value.sales!!.dueDate.toString(),
            isEstimatedBill = state.value.sales!!.isEstimatedBill,
            remarks = state.value.sales!!.remarks,
        )


        if (state.value.isPostedBill) {
            val localDate = HP.toLocalDate(state.value.sales!!.date.toString())
            val localTime = HP.toLocalTime(state.value.sales!!.date.toString())
            sale.date = HP.getZonedDateWithTime(localDate, localTime)
        } else {
            sale.currentShiftId = HP.user.currentShiftId
        }

        return sale
    }

    private fun isValid(): Boolean {
        if (HP.settings.isPaymentNecessary == true && state.value.sales!!.salesOn == 1) {
            val payment = state.value.sales!!.payment!!
            val total = state.value.total

            if (payment == 0) {
                showMessage("Please enter payment")
                return false
            }

            if (payment > 0 && payment < total) {
                showMessage("Less payment entered")
                return false
            }
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

    fun updateInitialState(isPostedBill: Boolean, sales: Sales) {
        state.update {
            it.copy(
                invoiceId = sales.id!!,
                isPostedBill = isPostedBill,
                sales = sales,
            )
        }
    }

    private fun updateTotal() {
        val totalBill = state.value.grandTotal
        val totalCost = state.value.totalCost
        val totalDisc = getTotalDisc()
        val total = totalBill - totalDisc
        val profit = total - totalCost

        state.update {
            it.copy(
                total = total,
                cost = totalCost,
                profit = profit,
                totalDisc = totalDisc,
            )
        }
    }

    private fun getTotalDisc(): Double {
        state.value.sales?.run {
            val total = state.value.grandTotal

            val totalDisc = if (total > 0.0) {
                if (isDiscRsPer!!)
                    disc!!
                else {
                    val totalDisc = (disc!! / 100) * total
                    totalDisc
                }
            } else
                0.0

            return totalDisc
        }

        return 0.0
    }
    // endregion
}