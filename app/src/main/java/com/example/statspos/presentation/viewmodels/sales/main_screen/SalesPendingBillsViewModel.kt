package com.example.statspos.presentation.viewmodels.sales.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.accounts.EntryVoucher
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBill
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.domain.repository.sales.SalesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
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
class SalesPendingBillsViewModel @Inject constructor(
    private val api: SalesRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<SalesBills> = emptyList(),
        val totalBills: Int = 0,

        val search: String = "",

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

    init {
        loadData()
    }

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

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("text", state.value.search)
            }

            when (val result = api.loadPendingBills(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalBills").asInt
                    val resultList =
                        Gson().getListOf<SalesBills>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalBills = resultTotal,
                        )
                    }
                }
            }
        }
    }

    fun makeNewBill(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.getInvoiceId()) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val invoiceId = result.data.get("invoiceId").asLong
                    tempClose(invoiceId) {
                        onSuccess(invoiceId)
                    }
                }
            }
        }
    }

    fun tempClose(invoiceId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val sale = Sales(
                salesId = invoiceId,

                date = HP.getZonedDate(LocalDate.now()),
                dueDate = HP.getZonedDate(LocalDate.now().plusDays(7)),
                isDiscRsPer = HP.settings.isDefaultDiscRs == true,
                isRetail = HP.settings.isDefaultRateRetail == true,

                isPendingBill = false,
            )

            when (val result = api.tempClose(sale)) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    onSuccess()
                }
            }
        }
    }

    fun getBill(invoiceId:Long, onSuccess: (bill: List<SalesBill>, List<AccountReport>?) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("id", invoiceId)
                addProperty("billType", 3)
                addProperty("isPendingBill", true)
            }

            when (val result = api.getBill(params)) {
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
    // endregion
}