package com.graphees.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.models.accounts.Entries
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.domain.repository.accounts.AccountsRepository
import com.graphees.statspos.domain.repository.reports.AccountReportsRepository
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.FixedAccounts
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.graphees.statspos.utils.getEntryType
import com.graphees.statspos.utils.getFixedAccount
import com.graphees.statspos.utils.getListOf
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
class LedgerViewModel @Inject constructor(
    private val api: AccountReportsRepository,
    private val entryApi: AccountsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val account: Accounts = Accounts(0L, accountName = ""),

        val fromDate: LocalDate = LocalDate.now().minusMonths(1),
        val toDate: LocalDate = LocalDate.now(),

        val entryType: EntryType = EntryType.RECEIPT,

        val amount: String = "",
        val naration: String = "",
        val date: LocalDate = LocalDate.now(),
        val mop: DropdownItem = HP.mop[0],
        val bank: DropdownItem = HP.getNoneDropdownItem(),
        val subBank: DropdownItem = HP.getNoneDropdownItem(),

        val accountReport: List<AccountReport> = emptyList(),
        val totalReport: TotalReport = TotalReport(totalEntries = 0),

        val hasLoadedOnce: Boolean = false,

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
    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
        loadLedger()
    }

    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
        loadLedger()
    }

    fun onAmountChange(value: String) {
        state.update { it.copy(amount = value) }
    }

    fun onNarationChange(value: String) {
        state.update { it.copy(naration = value) }
    }

    fun onDateChange(value: LocalDate) {
        state.update { it.copy(date = value) }
    }

    fun onMOPChange(value: DropdownItem) {
        state.update {
            it.copy(
                mop = value,
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
            )
        }
        changeNaration()
    }

    fun onBankSelected(value: DropdownItem) {
        state.update { it.copy(bank = value) }
    }

    fun onSubBankSelected(value: DropdownItem) {
        state.update { it.copy(subBank = value) }
        changeNaration()
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun loadLedger() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = getParams()
            params.addProperty("accountId", state.value.account.id)

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
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    fun passEntry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (!isValid())
                return@launch

            beforeRequest()

            val entry = getFormData()
            when (val result = entryApi.passEntry(entry)) {
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
    private fun getFormData(): Entries {
        return Entries(
            debitAccountId = if (state.value.entryType == EntryType.RECEIPT) {
                if (state.value.mop.id == 1L)
                    getFixedAccount(FixedAccounts.CASH)
                else
                    state.value.subBank.id
            } else
                state.value.account.id!!,
            creditAccountId = if (state.value.entryType == EntryType.RECEIPT)
                state.value.account.id!!
            else {
                if (state.value.mop.id == 1L)
                    getFixedAccount(FixedAccounts.CASH)
                else
                    state.value.subBank.id
            },
            amount = HP.getDoubleValue(state.value.amount),
            naration = state.value.naration,
            date = HP.getZonedDate(state.value.date),
            entryType = getEntryType(state.value.entryType),
            currentShiftId = HP.user.currentShiftId,
            isMopCashBank = state.value.mop.id == 1L
        )
    }

    private fun isValid(): Boolean {
        if (state.value.account.id == 0L) {
            showMessage("Please select account")
            return false
        }

        if (HP.getDoubleValue(state.value.amount) == 0.0) {
            showMessage("Please enter amount")
            return false
        }

        if (state.value.mop.id == 2L) {
            if (state.value.bank.id == 0L) {
                showMessage("Please select bank")
                return false
            } else {
                if (state.value.subBank.id == 0L) {
                    showMessage("Please select bank account")
                    return false
                } else
                    return isValidDate()
            }
        } else
            return isValidDate()
    }

    fun isValidDate(): Boolean {
        val today = LocalDate.now()
        val date = state.value.date

        val days = ChronoUnit.DAYS.between(date, today)
        if (days < 0) {
            showMessage("Future date cannot be selected")
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

    private fun getParams(): JsonObject {
        return JsonObject().apply {
            addProperty("fromDate", HP.getZonedDate(state.value.fromDate))
            addProperty("toDate", HP.getZonedDate(state.value.toDate))
        }
    }

    private fun changeNaration() {
        var naration = if (state.value.entryType == EntryType.RECEIPT) {
            "Cash Received from " + state.value.account.accountName
        } else {
            "Cash Paid to " + state.value.account.accountName
        }

        if (state.value.entryType == EntryType.RECEIPT) {
            if (state.value.mop.id == 2L) {
                state.value.subBank.run {
                    naration = (naration + " in " + state.value.subBank.name)
                }
            }
        } else {
            if (state.value.mop.id == 2L) {
                state.value.subBank.run {
                    naration = (naration + " from " + state.value.subBank.name)
                }
            }
        }


        state.update { it.copy(naration = naration) }
    }

    fun updateInitialState(account: Accounts) {
        state.update {
            it.copy(
                account = account,
            )
        }
    }

    fun updateInitialStateForEntry(account: Accounts, entryType: EntryType) {
        state.update {
            it.copy(
                account = account,
                entryType = entryType,
            )
        }
        changeNaration()
    }
// endregion
}