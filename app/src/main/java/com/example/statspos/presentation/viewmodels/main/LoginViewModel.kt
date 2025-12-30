package com.example.statspos.presentation.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val username: String = "",
        val password: String = "",
        val remember: Boolean = false,

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
            is UiEvent.ShowMessage -> {}
            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showMessage(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        viewModelScope.launch {
            _event.send(UiEvent.ShowMessage(message, type))
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

    fun onRememberCheckedChange(value: Boolean) {
        state.update { it.copy(remember = value) }
    }
    // endregion

    // region Network calls
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            if (!loginValidation()) {
                return@launch
            }
            beforeRequest()

            when (val result = usersRepository.login(state.value.username, state.value.password)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    if (result.data.get("isExists").asBoolean) {
                        Log.d("TAG Users", result.data.toString())
//                        val clientId = result.data.getAsJsonObject("data").get("id").asLong
                        onSuccess()
                    } else {
                        showMessage("Username or password incorrect")
                    }
                }
            }
        }
    }

    // endregion

    // region Others
    private fun loginValidation(): Boolean {
        if (state.value.username.isEmpty()) {
            showMessage("Enter username")
            return false
        } else if (state.value.password.isEmpty()) {
            showMessage("Enter password")
            return false
        } else
            return true
    }

    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false, error = error) }
        error?.let { showMessage(it, SnackbarType.ERROR) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showMessage(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}