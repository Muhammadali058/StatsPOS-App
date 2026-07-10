package com.graphees.statspos.presentation.viewmodels.accounts.entries.receipt

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
import com.graphees.statspos.utils.get
import com.graphees.statspos.utils.getEntryType
import com.graphees.statspos.utils.getFixedAccount
import com.google.gson.Gson
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
class NewReceiptEntryViewModel @Inject constructor(
    private val api: AccountsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val amount: String = "",
        val naration: String = "",
        val remarks: String = "",
        val date: LocalDate = LocalDate.now(),
        val mop: DropdownItem = HP.mop[0],
        val bank: DropdownItem = HP.getNoneDropdownItem(),
        val subBank: DropdownItem = HP.getNoneDropdownItem(),

        val isVendor: Boolean = false,
        val customerId: Long = 0L,
        val vendorId: Long = 0L,
        val customerName: String = "",
        val vendorName: String = "",
        val balance: String = "Balance: 0 (R)",

//        Extras
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
    fun onCustomerIdChange(value: Long) {
        state.update { it.copy(customerId = value) }
        getBalance(value)
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
        getBalance(value)
    }

    fun onCustomerNameChange(value: String) {
        state.update { it.copy(customerName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
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

    fun onIsVendorChange(value: Boolean) {
        state.update {
            it.copy(
                isVendor = value,

                customerId = 0L,
                vendorId = 0L,
                customerName = "",
                vendorName = "",
                balance = "Balance: 0 (R)",
                remarks = "",
            )
        }
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

    private fun getBalance(accountId: Long) {
        viewModelScope.launch {
            if (accountId != 0L) {
                when (val result = api.getBalance(accountId)) {
                    is Resource.Error -> showError(result.error)
                    is Resource.Information -> showMessage(result.message)
                    is Resource.Success -> {
                        val balance = HP.getBalanceWithLabel(result.data.get("balance").asDouble)
                        val account = Gson().get<Accounts>(result.data.get("data").asJsonObject)

                        state.update {
                            it.copy(
                                balance = balance,
                                remarks = account.remarks!!,
                            )
                        }

                        changeNaration()
                    }
                }
            } else {
                state.update {
                    it.copy(
                        balance = "Balance: 0 (R)",
                        remarks = "",
                    )
                }

                changeNaration()
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Entries {
        return Entries(
            debitAccountId = if (state.value.mop.id == 1L) getFixedAccount(FixedAccounts.CASH) else state.value.subBank.id,
            creditAccountId = if (state.value.isVendor) state.value.vendorId else state.value.customerId,
            amount = HP.getDoubleValue(state.value.amount),
            naration = state.value.naration,
            date = HP.getZonedDate(state.value.date),
            entryType = getEntryType(EntryType.RECEIPT),
            currentShiftId = HP.user.currentShiftId,
            isMopCashBank = state.value.mop.id == 1L
        )
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                customerId = 0L,
                vendorId = 0L,
                customerName = "",
                vendorName = "",
                balance = "Balance: 0 (R)",
                remarks = "",
                amount = "",
                date = LocalDate.now(),
                naration = "",
                mop = HP.mop[0],
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.isVendor) {
            if (state.value.vendorId == 0L) {
                showMessage("Please select vendor")
                return false
            }
        } else {
            if (state.value.customerId == 0L) {
                showMessage("Please select customer")
                return false
            }
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
        state.update { it.copy(isLoading = false) }
    }

    private fun changeNaration() {
        var naration = if (state.value.isVendor) {
            if (state.value.vendorId != 0L)
                "Cash Received from " + state.value.vendorName
            else
                ""
        } else {
            if (state.value.customerId != 0L)
                "Cash Received from " + state.value.customerName
            else
                ""
        }

        if (state.value.mop.id == 2L) {
            state.value.subBank.run {
                naration = (naration + " in " + state.value.subBank.name)
            }
        }

        state.update { it.copy(naration = naration) }
    }

    // endregion
}