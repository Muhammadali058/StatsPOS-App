package com.example.statspos.presentation.viewmodels.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.showToast
import com.example.statspos.utils.showToast1
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiEvent{
    data object Idle: UiEvent()
    data class ShowSnackbar(val message: String): UiEvent()
}

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val clientsRepository: ClientsRepository,
    private val context: Application
) : ViewModel() {

    var state = MutableStateFlow(ScreenState())
        private set

    private val _event = Channel<UiEvent>()
    var event = _event.receiveAsFlow()

    fun onEvent(event: UiEvent) {
        when(event){
            is UiEvent.ShowSnackbar -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowSnackbar(event.message))
                }
            }
            else -> {}
        }
    }

    // Screen State
     data class ScreenState(
        val username: String = "",
        val password: String = "",

        val isLoading: Boolean = false,
        val error: String? = null,
        val infoMessage: String? = null,
    )

    // region onChangeMethods
    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        state.update { it.copy(password = value) }
    }
    // endregion

    fun initState() {
        state.value = state.value.copy(
            isLoading = true,
            error = null,
            infoMessage = null,
        )
    }

    fun clientLogin(onSuccess: (clientId: Long) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if(!validation()){
                return@launch
            }

            initState()

            val params = JsonObject().apply {
                addProperty("username", state.value.username)
                addProperty("password", state.value.password)
            }

            when (val result = clientsRepository.clientLogin(params)) {
                is Resource.Error -> {
                    state.value = ScreenState(error = result.message)
                }

                is Resource.Information -> {
                    state.value = ScreenState(infoMessage = result.infoMessage)
                }

                is Resource.Success -> {
                    state.value = state.value.copy(isLoading = false)

                    if (result.data.get("isExists").asBoolean) {
                        val clientId = result.data.getAsJsonObject("data").get("id").asLong
                        onSuccess(clientId)
                    } else {
                        state.value = state.value.copy(
                            infoMessage = "Username or password incorrect",
                        )
                    }
                }
            }
        }
    }

    fun validation(): Boolean{
        if(state.value.username.isEmpty()){
            context.showToast1("Enter username msg")
            return false
        }else if (state.value.password.isEmpty()){
            context.showToast("Enter password")
            return false
        }else
            return true
    }
}