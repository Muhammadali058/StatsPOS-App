package com.example.statspos.presentation.viewmodels.accounts.banks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.accounts.Banks
import com.example.statspos.domain.models.accounts.Expenses
import com.example.statspos.domain.repository.accounts.BanksRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BanksViewModel @Inject constructor(
    private val api: BanksRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<Banks> = emptyList(),
        val totalBanks: Int = 0,

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

        if(HP.appSettings.instantSearch == true)
            loadData()
    }
    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("text", state.value.search)
            }

            when (val result = api.loadBanks(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalBanks").asInt
                    val resultList =
                        Gson().getListOf<Banks>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalBanks = resultTotal,
                        )
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
    // endregion
}