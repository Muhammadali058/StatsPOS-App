package com.graphees.statspos.presentation.viewmodels.main.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.graphees.statspos.domain.models.main.Branches
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.domain.models.main.LocalBranches
import com.graphees.statspos.domain.repository.main.ClientsRepository
import com.graphees.statspos.utils.LocalDataStore
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.graphees.statspos.utils.getListOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateClientViewModel @Inject constructor(
    private val clientsRepository: ClientsRepository,
    private var dataStore: LocalDataStore,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val client: Clients = Clients(),

        val businessName: String = "",
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",

        val localBranches: List<LocalBranches> = emptyList(),

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
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
    fun onBusinessNameChange(value: String) {
        state.update { it.copy(businessName = value) }
    }

    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        state.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        state.update { it.copy(confirmPassword = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateClient(onSuccess: (client: Clients) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (!isValid()) {
                return@launch
            }

            beforeRequest()

            val client = Clients(
                id = state.value.client.id!!,
                clientName = state.value.businessName,
                username = state.value.username,
                password = state.value.password,
            )

            when (val result = clientsRepository.updateClient(client)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val client = Gson().get<Clients>(result.data.get("client").asJsonObject)
                    val branches = Gson().getListOf<Branches>(result.data.get("branches").asJsonArray)

                    dataStore.setClient(client, branches)
                    onSuccess(client)
                }
            }
        }
    }

    // endregion

    // region Others

    private fun isValid(): Boolean {
        if (state.value.businessName.isEmpty()) {
            showSnackbar("Enter Business Name")
            return false
        } else if (state.value.username.isEmpty()) {
            showSnackbar("Enter username")
            return false
        } else if (state.value.password.isEmpty()) {
            showSnackbar("Enter password")
            return false
        } else if (state.value.confirmPassword.isEmpty()) {
            showSnackbar("Re-enter password")
            return false
        } else if (state.value.password != state.value.confirmPassword) {
            showSnackbar("Password didn't match")
            return false
        } else
            return true
    }

    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false, error = error) }
        error?.let { onEvent(UiEvent.ShowError(it)) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showSnackbar(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }

    fun updateInitialState(client: Clients) {
        state.update { it.copy(
            client = client,
            businessName = client.clientName!!,
            username = client.username!!,
        ) }
    }

    // endregion
}