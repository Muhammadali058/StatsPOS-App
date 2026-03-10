package com.example.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.reports.MainReport
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.repository.reports.AccountReportsRepository
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
class AccountReportsViewModel @Inject constructor(
    private val api: AccountReportsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val customerName: String = "",
        val vendorName: String = "",
        val bank: DropdownItem = HP.getNoneDropdownItem(),
        val subBank: DropdownItem = HP.getNoneDropdownItem(),
        val expense: DropdownItem = HP.getNoneDropdownItem(),
        val subExpense: DropdownItem = HP.getNoneDropdownItem(),
        val customerCategory: DropdownItem = HP.getNoneDropdownItem(),
        val vendorCategory: DropdownItem = HP.getNoneDropdownItem(),
        val supplierName: String = "",
        val username: String = "",

        val vendorId: Long = 0L,
        val customerId: Long = 0L,
        val supplierId: Long = 0L,
        val userId: Long = 0L,

        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),

        val listType: DropdownItem = HP.listType[0],
        val mop: DropdownItem = HP.getNoneDropdownItem("Both"),

        val mainReport: MainReport = MainReport(),
        val accountReport: List<AccountReport>? = null,
        val totalReport: TotalReport? = null,

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
    fun onCustomerNameChange(value: String) {
        state.update { it.copy(customerName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onBankChange(value: DropdownItem) {
        state.update { it.copy(bank = value) }
    }

    fun onSubBankChange(value: DropdownItem) {
        state.update { it.copy(subBank = value) }
    }

    fun onExpenseChange(value: DropdownItem) {
        state.update { it.copy(expense = value) }
    }

    fun onSubExpenseChange(value: DropdownItem) {
        state.update { it.copy(subExpense = value) }
    }

    fun onCustomerCategoryChange(value: DropdownItem) {
        state.update { it.copy(customerCategory = value) }
    }

    fun onVendorCategoryChange(value: DropdownItem) {
        state.update { it.copy(vendorCategory = value) }
    }

    fun onSupplierNameChange(value: String) {
        state.update { it.copy(supplierName = value) }
    }

    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onCustomerIdChange(value: Long) {
        state.update { it.copy(customerId = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
    }

    fun onSupplierIdChange(value: Long) {
        state.update { it.copy(supplierId = value) }
    }

    fun onUserIdChange(value: Long) {
        state.update { it.copy(userId = value) }
    }

    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
    }

    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
    }

    fun onListTypeChange(value: DropdownItem) {
        state.update { it.copy(listType = value) }
    }

    fun onMopChange(value: DropdownItem) {
        state.update { it.copy(mop = value) }
    }

    // endregion

    // region Button Clicks
    fun onCustomerLedgerClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.customerId == 0L) {
            showMessage("Select customer")
        } else {
            val params = getParams()
            params.addProperty("accountId", state.value.customerId)
            loadLedger(params, onSuccess)
        }
    }

    fun onVendorLedgerClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.vendorId == 0L) {
            showMessage("Select vendor")
        } else {
            val params = getParams()
            params.addProperty("accountId", state.value.vendorId)
            loadLedger(params, onSuccess)
        }
    }

    fun onBankStatementClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.bank.id == 0L) {
            showMessage("Select bank")
        } else {
            if (state.value.subBank.id == 0L) {
                showMessage("Select bank account")
            } else {
                val params = getParams()
                params.addProperty("accountId", state.value.subBank.id)
                loadLedger(params, onSuccess)
            }
        }
    }

    fun onTotalExpensesClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        loadExpenses(params, onSuccess)
    }

    fun onExpensesClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.expense.id == 0L) {
            showMessage("Select expense")
        } else {
            val params = getParams()
            params.addProperty("expenseId", state.value.expense.id)
            loadExpenses(params, onSuccess)
        }
    }

    fun onSubExpensesClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.expense.id == 0L) {
            showMessage("Select expense")
        } else {
            if (state.value.subExpense.id == 0L) {
                showMessage("Select sub-expense")
            } else {
                val params = getParams()
                params.addProperty("expenseId", state.value.expense.id)
                params.addProperty("subExpenseId", state.value.subExpense.id)
                loadExpenses(params, onSuccess)
            }
        }
    }

    fun onReceiptsClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("userId", state.value.userId)
        params.addProperty("mop", state.value.mop.id)
        loadReceipts(params, onSuccess)
    }

    fun onPaymentsClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("userId", state.value.userId)
        params.addProperty("mop", state.value.mop.id)
        loadPayments(params, onSuccess)
    }

    fun onCustomersClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        if (state.value.listType.id == 1L)
            loadDebtors(params, onSuccess)
        else
            loadCustomersBalanceList(params, onSuccess)
    }

    fun onVendorsClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        loadCreditors(params, onSuccess)
    }

    fun onSuppliersClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.supplierId == 0L) {
            showMessage("Select supplier")
        } else {
            val params = getParams()
            params.addProperty("supplierId", state.value.supplierId)

            if (state.value.listType.id == 1L)
                loadDebtors(params, onSuccess)
            else
                loadCustomersBalanceList(params, onSuccess)
        }
    }

    fun onCustomerCategoryClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.customerCategory.id == 0L) {
            showMessage("Select customer category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.customerCategory.id)

            if (state.value.listType.id == 1L)
                loadDebtors(params, onSuccess)
            else
                loadCustomersBalanceList(params, onSuccess)
        }
    }

    fun onVendorCategoryClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        if (state.value.vendorCategory.id == 0L) {
            showMessage("Select vendor category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.vendorCategory.id)
            loadCreditors(params, onSuccess)
        }
    }

    // endregion

    // region Network calls
    private fun loadLedger(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.ledger(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadExpenses(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.expenses(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadReceipts(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.receipts(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadPayments(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.payments(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadDebtors(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.debtors(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadCreditors(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.creditors(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadCustomersBalanceList(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.customersBalanceList(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val accountReport =
                        Gson().getListOf<AccountReport>(result.data.get("rows").asJsonArray)
                    if (accountReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                accountReport = accountReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(accountReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
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

    private fun getParams(): JsonObject {
        return JsonObject().apply {
            addProperty("fromDate", HP.getZonedDateWithFromTime(state.value.fromDate))
            addProperty("toDate", HP.getZonedDateWithToTime(state.value.toDate))
        }
    }

// endregion
}