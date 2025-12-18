package com.example.statspos.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.UiEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val clientsRepository: ClientsRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val username: String = "",
        val password: String = "",

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
            is UiEvent.ShowSnackbar -> {}
            is UiEvent.ShowError -> {}
            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _event.send(UiEvent.ShowSnackbar(message))
        }
    }

    fun showError(message: String) {
        viewModelScope.launch {
            _event.send(UiEvent.ShowError(message))
        }
    }
    // endregion

    // region onChangeMethods
    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        state.update { it.copy(password = value) }
    }
    // endregion

    // region Network calls
    fun clientLogin(onSuccess: (clientId: Long) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            if (!validation()) {
                return@launch
            }
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("username", state.value.username)
                addProperty("password", state.value.password)
            }

            when (val result = clientsRepository.clientLogin(params)) {
                is Resource.Error -> resultError(result.message)
                is Resource.Information -> resultInformation(result.infoMessage)
                is Resource.Success -> {
                    resultSuccess()
                    if (result.data.get("isExists").asBoolean) {
                        val clientId = result.data.getAsJsonObject("data").get("id").asLong
                        onSuccess(clientId)
                    } else {
                        showSnackbar("Username or password incorrect")
                    }
                }
            }
        }
    }

    // endregion

    // region Others
    private fun validation(): Boolean {
        if (state.value.username.isEmpty()) {
            showSnackbar("Enter username")
            return false
        } else if (state.value.password.isEmpty()) {
            showSnackbar("Enter password")
            return false
        } else
            return true
    }

    private fun resultError(message: String?) {
        state.update { it.copy(isLoading = false, error = message) }
        message?.let { showError(it) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showSnackbar(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}