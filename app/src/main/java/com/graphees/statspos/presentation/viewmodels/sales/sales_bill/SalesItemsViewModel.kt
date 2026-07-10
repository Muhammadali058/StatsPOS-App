package com.graphees.statspos.presentation.viewmodels.sales.sales_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.domain.models.sales.SalesBill
import com.graphees.statspos.domain.models.sales.SalesBillItems
import com.graphees.statspos.domain.repository.sales.SalesItemsRepository
import com.graphees.statspos.domain.repository.sales.SalesRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.getListOf
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
    private val salesRepo: SalesRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<SalesBillItems> = emptyList(),
        val totalItems: Int = 0,

        val totalBill: Double = 0.0,
        val totalCost: Double = 0.0,
        val totalProfit: Double = 0.0,
        val totalQty: Double = 0.0,
        val totalCrtn: Int = 0,
        val totalItemDisc: Double = 0.0,

        // Extras
        val search: String = "",
        val invoiceId: Long = 0L,
        val isPostedBill: Boolean = false,

        val hasLoadedOnce: Boolean = false,

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
    fun onSearchChange(value: String, updateTotal: (Double, Double) -> Unit) {
        state.update { it.copy(search = value) }

        if(HP.appSettings.instantSearch == true)
            loadData(updateTotal)
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun loadData(updateTotal: (Double, Double) -> Unit, onSuccess: () -> Unit = {}) {
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

                    val totalBill =
                        result.data.get("total").asJsonObject.get("grandTotal").asDouble
                    val totalCost =
                        result.data.get("total").asJsonObject.get("totalCost").asDouble
                    val totalProfit =
                        result.data.get("total").asJsonObject.get("totalProfit").asDouble
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

                            totalBill = totalBill,
                            totalCost = totalCost,
                            totalProfit = totalProfit,
                            totalQty = totalQty,
                            totalCrtn = totalCrtn,
                            totalItemDisc = totalDisc,
                        )
                    }

                    updateTotal(totalBill, totalCost)
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (id == 0L)
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

    fun getBill(
        invoiceId:Long,
        billType:Int,
        isPendingBill:Boolean,
        onSuccess: (bill: List<SalesBill>, List<AccountReport>?) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("id", invoiceId)
                addProperty("billType", billType)
                addProperty("isPendingBill", isPendingBill)
            }

            when (val result = salesRepo.getBill(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val bill = Gson().getListOf<SalesBill>(result.data.get("bill").asJsonArray)
                    var ledger: List<AccountReport>? = null

                    if(HP.settings.showLedgerInBill == true){
                        ledger = Gson().getListOf<AccountReport>(result.data.get("ledger").asJsonArray)
                    }

                    onSuccess(bill,ledger)
                }
            }
        }
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

    fun updateInitialState(invoiceId: Long, isPostedBill: Boolean) {
        state.update {
            it.copy(
                invoiceId = invoiceId,
                isPostedBill = isPostedBill,
            )
        }
    }
    // endregion
}