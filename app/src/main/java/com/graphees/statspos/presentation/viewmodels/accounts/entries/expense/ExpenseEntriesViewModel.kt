package com.graphees.statspos.presentation.viewmodels.accounts.entries.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Entries
import com.graphees.statspos.domain.repository.accounts.AccountsRepository
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.getEntryType
import com.graphees.statspos.utils.getListOf
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
class ExpenseEntriesViewModel @Inject constructor(
    private val api: AccountsRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<Entries> = emptyList(),
        val totalEntries: Int = 0,

        val search: String = "",
        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),
        val selectedMOP: DropdownItem? = null,

//        Extras
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
        loadEntries()
    }

    // region onChangeMethods
    fun onSearchChange(value: String) {
        state.update { it.copy(search = value) }

        if(HP.appSettings.instantSearch == true)
            loadEntries()
    }

    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
    }

    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
    }

    fun onSelectedMOPChange(value: DropdownItem) {
        state.update { it.copy(selectedMOP = value) }
    }

    // endregion

    // region Network calls
    fun loadEntries() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("entryType", getEntryType(EntryType.EXPENSE))
                addProperty("fromDate", HP.getZonedDate(state.value.fromDate))
                addProperty("toDate", HP.getZonedDate(state.value.toDate))
                addProperty("mop", state.value.selectedMOP?.id ?: 0)
                addProperty("text", state.value.search)
            }

            when (val result = api.loadEntries(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalEntries").asInt
                    val resultList =
                        Gson().getListOf<Entries>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalEntries = resultTotal,
                        )
                    }
                }
            }
        }
    }

    fun deleteEntry(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (id == 0L)
                return@launch

            beforeRequest()

            when (val result = api.deleteEntry(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    state.update {
                        it.copy(
                            list = state.value.list.filter { it.id != id },
                            totalEntries = state.value.totalEntries - 1,
                        )
                    }

                    onSuccess()
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