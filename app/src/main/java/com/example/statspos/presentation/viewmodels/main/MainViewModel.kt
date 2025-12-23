package com.example.statspos.presentation.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.repository.utilities.UsersRepository
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
class MainViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
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

    // region Network calls
    fun login(clientId:Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
//            if (!loginValidation()) {
//                return@launch
//            }
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("clientId", clientId)
//                addProperty("username", state.value.username)
//                addProperty("password", state.value.password)
            }

            when (val result = usersRepository.login(params)) {
                is Resource.Error -> resultError(result.message)
                is Resource.Information -> resultInformation(result.infoMessage)
                is Resource.Success -> {
                    resultSuccess()
                    if (result.data.get("isExists").asBoolean) {
                        Log.d("TAG Users", result.data.toString())
//                        val clientId = result.data.getAsJsonObject("data").get("id").asLong
                        onSuccess()
                    } else {
                        showSnackbar("Username or password incorrect")
                    }
                }
            }
        }
    }

    // endregion

    // region Others
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