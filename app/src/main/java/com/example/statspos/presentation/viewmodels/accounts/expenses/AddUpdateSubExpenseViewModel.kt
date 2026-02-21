package com.example.statspos.presentation.viewmodels.accounts.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.repository.accounts.ExpensesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateSubExpenseViewModel @Inject constructor(
    private val api: ExpensesRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val expenseId: Long = 0L,
        val subExpenseName: String = "",

        // Extra
        val expenseName: String = "",
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

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
    fun onExpenseNameChange(value: String) {
        state.update { it.copy(expenseName = value) }
    }

    fun onExpenseIdChange(value: Long) {
        state.update { it.copy(expenseId = value) }
    }

    fun onSubExpenseNameChange(value: String) {
        state.update { it.copy(subExpenseName = value) }
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

            val subExpense = getFormData()

            val result = if (state.value.isUpdate) {
                subExpense.id = state.value.updateId
                api.updateSubExpense(subExpense)
            } else {
                api.insertSubExpense(subExpense)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {
                    HP.subExpenses = result.data.get("subExpenses").asJsonArray.map { obj ->
                        DropdownItem(
                            id = obj.asJsonObject.get("id").asLong,
                            name = obj.asJsonObject.get("name").asString,
                            mainId = obj.asJsonObject.get("expenseId").asLong,
                        )
                    }

//                    clearTextboxes()
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

            when (val result = api.deleteSubExpense(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.subExpenses = result.data.get("subExpenses").asJsonArray.map { obj ->
                        DropdownItem(
                            id = obj.asJsonObject.get("id").asLong,
                            name = obj.asJsonObject.get("name").asString,
                            mainId = obj.asJsonObject.get("expenseId").asLong,
                        )
                    }
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

            when (val result = api.getSubExpense(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val expense = Gson().get<Accounts>(result.data.asJsonObject)
                    setFormData(expense)
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Accounts {
        return Accounts(
            expenseId = state.value.expenseId,
            accountName = state.value.subExpenseName,
        )
    }

    private fun setFormData(expense: Accounts) {
        state.update {
            it.copy(
                subExpenseName = expense.accountName!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                subExpenseName = "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.subExpenseName.isEmpty()) {
            showMessage("Please enter sub-expense name")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, expenseId: Long) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,
                expenseId = expenseId,
                expenseName = if (expenseId == 0L) "" else HP.getDropdownNameById(
                    expenseId,
                    HP.expenses
                )
            )
        }
    }
    // endregion
}