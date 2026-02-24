package com.example.statspos.presentation.viewmodels.accounts.entries.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Entries
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.utils.EntryType
import com.example.statspos.utils.FixedAccounts
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getEntryType
import com.example.statspos.utils.getFixedAccount
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
class NewStockEntryViewModel @Inject constructor(
    private val api: AccountsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val itemId: Long = 0L,
        val itemname: String = "",
        val qty: String = "",
        val crtn: String = "",
        val cost: Double = 0.0,
        val crtnSize: Int = 0,
        val amount: String = "",
        val naration: String = "",
        val date: LocalDate = LocalDate.now(),
        val mop: DropdownItem = HP.mop[0],
        val bank: DropdownItem = HP.getNoneDropdownItem(),
        val subBank: DropdownItem = HP.getNoneDropdownItem(),

        val expense: DropdownItem = HP.getNoneDropdownItem(),
        val subExpense: DropdownItem = HP.getNoneDropdownItem(),

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
    fun onItemnameChange(value: String) {
        state.update {
            it.copy(
                itemname = value,
                itemId = 0L,
            )
        }
    }

    fun onQtyChange(value: String) {
        state.update { it.copy(qty = value) }
        updateTotal()
        changeNaration()
    }

    fun onCrtnChange(value: String) {
        state.update { it.copy(crtn = value) }
        updateTotal()
        changeNaration()
    }

    fun onExpenseSelected(value: DropdownItem) {
        state.update { it.copy(expense = value) }
    }

    fun onSubExpenseSelected(value: DropdownItem) {
        state.update { it.copy(subExpense = value) }
        changeNaration()
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

    fun getItem(value: String) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (value.isEmpty())
                return@launch

//            beforeRequest()
            when (val result = itemsRepo.isBarcodeExists(value)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val isExists = result.data.get("isExists").asBoolean
                    if (isExists) {
                        val item = Gson().get<Items>(result.data.get("data").asJsonObject)
                        state.update {
                            it.copy(
                                itemname = item.itemname!!,
                                itemId = item.id!!,
                                cost = item.cost!!,
                                crtnSize = item.crtnSize!!,
                            )
                        }
                        updateTotal()
                    } else {
                        state.update {
                            it.copy(
                                itemId = 0L,
                                cost = 0.0,
                                crtnSize = 0,
                            )
                        }
                        updateTotal()
                        showSnackbar("Items not found")
                    }
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Entries {
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val stock = ((qty + (crtn * state.value.crtnSize)) * -1)

        return Entries(
            debitAccountId = state.value.subExpense.id,
            creditAccountId = if (state.value.mop.id == 1L) getFixedAccount(FixedAccounts.CASH) else state.value.subBank.id,
            amount = HP.getDoubleValue(state.value.amount),
            naration = state.value.naration,
            date = HP.getZonedDate(state.value.date),
            entryType = getEntryType(EntryType.STOCK),
            currentShiftId = HP.user.currentShiftId,
            isMopCashBank = state.value.mop.id == 1L,

            itemId = state.value.itemId,
            stock = stock,
        )
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                itemname = "",
                itemId = 0L,
                qty = "",
                crtn = "",
                cost = 0.0,
                crtnSize = 0,

                expense = HP.getNoneDropdownItem(),
                subExpense = HP.getNoneDropdownItem(),
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
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val stock = qty + (crtn * state.value.crtnSize)

        if (state.value.itemId == 0L) {
            showMessage("Please select item")
            return false
        }

        if (stock <= 0) {
            showMessage("Please enter quantity")
            return false
        }

        if (state.value.expense.id == 0L) {
            showMessage("Please select expense")
            return false
        }

        if (state.value.subExpense.id == 0L) {
            showMessage("Please select sub-expense")
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
        state.update { it.copy(isLoading = false) }
    }

    private fun updateTotal() {
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val stock = qty + (crtn * state.value.crtnSize)
        val total = stock * (state.value.cost)

        state.update { it.copy(amount = total.toString()) }
    }

    private fun changeNaration() {
        val qty = HP.getDoubleValue(state.value.qty)
        val crtn = HP.getIntValue(state.value.crtn)
        val stock = qty + (crtn * state.value.crtnSize)

        var naration = if (state.value.subExpense.id != 0L)
            "Loss of " + state.value.itemname + " (" + stock + ") " + state.value.subExpense.name + " Expense"
        else
            ""

        if (state.value.mop.id == 2L) {
            state.value.subBank.run {
                naration = (naration + " from " + state.value.subBank.name)
            }
        } else {
            naration = "$naration in Cash"
        }

        state.update { it.copy(naration = naration) }
    }

    // endregion
}