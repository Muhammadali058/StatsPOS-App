package com.example.statspos.presentation.viewmodels.reports.manage_cash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Entries
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.models.reports.accounts.ShiftReport
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.domain.repository.reports.AccountReportsRepository
import com.example.statspos.domain.repository.utilities.ShiftsRepository
import com.example.statspos.utils.EntryType
import com.example.statspos.utils.FixedAccounts
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getEntryType
import com.example.statspos.utils.getFixedAccount
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
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ManageCashEntryViewModel @Inject constructor(
    private val api: AccountsRepository,
    private val accountReportsRepo: AccountReportsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val account: DropdownItem = HP.getNoneDropdownItem(),
        val date: LocalDate = LocalDate.now(),
        val amount: String = "",
        val naration: String = "",
        val entryTypes: List<DropdownItem> = listOf(
            DropdownItem(1L, "Cash In"),
            DropdownItem(2L, "Cash Out"),
        ),
        val entryType: DropdownItem = entryTypes[0],

        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),

        val accountReport: List<AccountReport>? = null,
        val totalReport: TotalReport? = null,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
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
    fun onAccountChange(value: DropdownItem) {
        state.update { it.copy(account = value) }
    }

    fun onDateChange(value: LocalDate) {
        state.update { it.copy(date = value) }
    }

    fun onAmountChange(value: String) {
        state.update { it.copy(amount = value) }
    }

    fun onNarationChange(value: String) {
        state.update { it.copy(naration = value) }
    }

    fun onEntryTypeChange(value: DropdownItem) {
        state.update { it.copy(entryType = value) }
    }

    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
    }

    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
    }

    // endregion

    // region Button Clicks
    fun onCashAccountClick(onSuccess: (List<AccountReport>, TotalReport) -> Unit) {
        val params = getParams()
        loadCashAccount(params, onSuccess)
    }

    // endregion

    // region Network calls
    fun passEntry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val entry = getFormData()
            val result = api.passEntry(entry)

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

    private fun loadCashAccount(
        params: JsonObject,
        onSuccess: (List<AccountReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = accountReportsRepo.cashAccount(params)) {
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

    // region Methods
    private fun getFormData(): Entries {
        return Entries(
            debitAccountId = if (state.value.entryType.id == 1L) getFixedAccount(FixedAccounts.CASH) else state.value.account.id,
            creditAccountId = if (state.value.entryType.id == 2L) getFixedAccount(FixedAccounts.CASH) else state.value.account.id,
            amount = HP.getDoubleValue(state.value.amount),
            naration = state.value.naration,
            date = HP.getZonedDate(state.value.date),
            entryType = getEntryType(EntryType.JOURNAL),
            currentShiftId = HP.user.currentShiftId,
            isMopCashBank = true
        )
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                account = HP.getNoneDropdownItem(),
                date = LocalDate.now(),
                amount = "",
                naration = "",
            )
        }
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

        if (state.value.naration.isEmpty()) {
            showMessage("Please enter naration")
            return false
        } else
            return isValidDate()
    }

    private fun isValidDate(): Boolean {
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
            addProperty("fromDate", HP.getZonedDateWithFromTime(state.value.fromDate))
            addProperty("toDate", HP.getZonedDateWithToTime(state.value.toDate))
        }
    }

// endregion
}