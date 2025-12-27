package com.example.statspos.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.main.Clients
import com.example.statspos.domain.models.main.LocalBranches
import com.example.statspos.domain.models.main.LocalClients
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
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
class ClientsViewModel @Inject constructor(
    private val clientsRepository: ClientsRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val businessName: String = "",
        val contact: String = "",
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",

        val localBranches: List<LocalBranches> = emptyList(),

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
//            is UiEvent.ShowError -> {}
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

//    fun showError(message: String) {
//        viewModelScope.launch {
//            _event.send(UiEvent.ShowError(message))
//        }
//    }
    // endregion

    // region onChangeMethods
    fun onBusinessNameChange(value: String) {
        state.update { it.copy(businessName = value) }
    }

    fun onContactChange(value: String) {
        state.update { it.copy(contact = value) }
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
    // endregion

    // region Network calls
    fun clientLogin(onSuccess: (clientId: Int) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            if (!loginValidation()) {
                return@launch
            }
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("username", state.value.username)
                addProperty("password", state.value.password)
            }

            when (val result = clientsRepository.clientLogin(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    if (result.data.get("isExists").asBoolean) {
                        val clientId = result.data.getAsJsonObject("data").get("id").asInt
                        onSuccess(clientId)
                    } else {
                        showMessage("Username or password incorrect")
                    }
                }
            }
        }
    }

    fun clientSignup(onSuccess: (clientId: Int) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            if (!signupValidation()) {
                return@launch
            }
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("clientName", state.value.businessName)
                addProperty("contact", state.value.contact)
                addProperty("username", state.value.username)
                addProperty("password", state.value.password)
            }

            when (val result = clientsRepository.clientSignup(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val client = Gson().fromJson(result.data, Clients::class.java)
                    onSuccess(client.id)
                }
            }
        }
    }

    fun localClientLogin() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            if (!loginValidation()) {
                return@launch
            }
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("username", state.value.username)
                addProperty("password", state.value.password)
            }

            when (val result = clientsRepository.localClientLogin(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    if (result.data.get("isExists").asBoolean) {
                        HP.localClient = Gson().fromJson(
                            result.data.getAsJsonObject("localClient"),
                            LocalClients::class.java
                        )

                        val jsonArray = result.data.getAsJsonArray("localBranches") ?: emptyList()
                        val localBranches = mutableListOf<LocalBranches>()
                        for (a in jsonArray) {
                            val branch = Gson().fromJson(a, LocalBranches::class.java)
                            localBranches.add(branch)
                        }

                        state.update {
                            it.copy(
                                localBranches = localBranches
                            )
                        }
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

    private fun signupValidation(): Boolean {
        if (state.value.businessName.isEmpty()) {
            showMessage("Enter Business Name")
            return false
        } else if (state.value.contact.isEmpty()) {
            showMessage("Enter contact")
            return false
        } else if (state.value.username.isEmpty()) {
            showMessage("Enter username")
            return false
        } else if (state.value.password.isEmpty()) {
            showMessage("Enter password")
            return false
        } else if (state.value.confirmPassword.isEmpty()) {
            showMessage("Re-enter password")
            return false
        } else if (state.value.password != state.value.confirmPassword) {
            showMessage("Password didn't match")
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