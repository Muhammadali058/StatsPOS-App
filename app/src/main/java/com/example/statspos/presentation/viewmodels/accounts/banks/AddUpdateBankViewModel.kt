package com.example.statspos.presentation.viewmodels.accounts.banks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Banks
import com.example.statspos.domain.repository.accounts.BanksRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateBankViewModel @Inject constructor(
    private val api: BanksRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val bankName: String = "",
        val remarks: String = "",

        // Extra
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

        val hasLoadedOnce: Boolean = false,

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
    fun onBankNameChange(value: String) {
        state.update { it.copy(bankName = value) }
    }
    fun onRemarksChange(value: String) {
        state.update { it.copy(remarks = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
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

            val bank = getFormData()

            val result = if (state.value.isUpdate) {
                bank.id = state.value.updateId
                api.updateBank(bank)
            } else {
                api.insertBank(bank)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {
                    HP.banks = Gson().getListOf<DropdownItem>(result.data.get("banks").asJsonArray)
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

            when (val result = api.deleteBank(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.banks = Gson().getListOf<DropdownItem>(result.data.get("banks").asJsonArray)
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

            when (val result = api.getBank(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val bank = Gson().get<Banks>(result.data.asJsonObject)
                    setFormData(bank)
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Banks {
        return Banks(
            bankName = state.value.bankName,
            remarks = state.value.remarks,
        )
    }

    private fun setFormData(bank: Banks) {
        state.update {
            it.copy(
                bankName =  bank.bankName.toString(),
                remarks =  bank.remarks.toString(),
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                bankName = "",
                remarks = "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.bankName.isEmpty()) {
            showMessage("Please enter bank name")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long) {
        state.update { it.copy(
            isUpdate = isUpdate,
            updateId = updateId,
        ) }
    }

    // endregion
}