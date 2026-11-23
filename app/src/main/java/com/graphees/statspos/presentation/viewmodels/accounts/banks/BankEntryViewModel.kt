package com.graphees.statspos.presentation.viewmodels.accounts.banks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.models.accounts.Entries
import com.graphees.statspos.domain.repository.accounts.AccountsRepository
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.FixedAccounts
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.getEntryType
import com.graphees.statspos.utils.getFixedAccount
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
class BankEntryViewModel @Inject constructor(
    private val api: AccountsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val account: Accounts = Accounts(0L, accountName = ""),

        val entryType: EntryType = EntryType.RECEIPT,

        val amount: String = "",
        val naration: String = "",
        val date: LocalDate = LocalDate.now(),
        val mop: DropdownItem = HP.mop[0],
        val bank: DropdownItem = HP.getNoneDropdownItem(),

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
            )
        }
        changeNaration()
    }

    fun onBankSelected(value: DropdownItem) {
        state.update { it.copy(bank = value) }
        changeNaration()
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun passEntry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (!isValid())
                return@launch

            beforeRequest()

            val entry = getFormData()
            when (val result = api.passEntry(entry)) {
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
                    state.value.bank.id
            } else
                state.value.account.id!!,
            creditAccountId = if (state.value.entryType == EntryType.RECEIPT)
                state.value.account.id!!
            else {
                if (state.value.mop.id == 1L)
                    getFixedAccount(FixedAccounts.CASH)
                else
                    state.value.bank.id
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
            } else
                return isValidDate()
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

    private fun changeNaration() {
        var naration = if (state.value.entryType == EntryType.RECEIPT) {
            "Transferred from " + state.value.account.accountName
        } else {
            "Received in " + state.value.account.accountName
        }

        naration = if (state.value.entryType == EntryType.RECEIPT) {
            if (state.value.mop.id == 2L) {
                naration + " to " + state.value.bank.name
            }else{
                naration + " to Cash"
            }
        } else {
            if (state.value.mop.id == 2L) {
                naration + " from " + state.value.bank.name
            } else {
                naration + " from Cash"
            }
        }


        state.update { it.copy(naration = naration) }
    }

    fun updateInitialState(account: Accounts, entryType: EntryType) {
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