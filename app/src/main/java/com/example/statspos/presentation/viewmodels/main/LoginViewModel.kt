package com.example.statspos.presentation.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.preloadImages
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
class LoginViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val mainRepository: MainRepository
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
    fun test() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            beforeRequest()

            val params = JsonObject().apply {
                addProperty("clientId", 1)
                addProperty("branchId", 1)
            }

//            when (val result = usersRepository.loadUsers(params)) {
//                is Resource.Error -> resultError(result.error)
//                is Resource.Information -> resultInformation(result.message)
//                is Resource.Success -> {
//                    resultSuccess()
//                    Log.d("TAG Users", result.data.toString())
//                }
//            }
        }
    }

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
                        HP.user = Gson().get<Users>(result.data.get("user").asJsonObject)
                        HP.userRights = Gson().get<UserRights>(result.data.get("userRights").asJsonObject)

                        HP.clientId = HP.user.clientId!!
                        HP.branchId = HP.user.branchId!!
                        HP.branchGroupId = HP.user.branchGroupId!!

                        loadMainData {
                            onSuccess()
                        }
                    } else {
                        showSnackbar("Username or password incorrect")
                    }
                }
            }
        }
    }

    fun loadMainData(onSuccess: () -> Unit){
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            beforeRequest()

            when (val result = mainRepository.loadData()) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.setDropdowns(result.data)
                    preloadImages(listOf(HP.getImageUrl(HP.printSettings.imageUrl.toString())))

                    onSuccess()
                }
            }
        }
    }

    // endregion

    // region Others
    private fun loginValidation(): Boolean {
        if (state.value.username.isEmpty()) {
            showSnackbar("Enter username")
            return false
        } else if (state.value.password.isEmpty()) {
            showSnackbar("Enter password")
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
    // endregion
}