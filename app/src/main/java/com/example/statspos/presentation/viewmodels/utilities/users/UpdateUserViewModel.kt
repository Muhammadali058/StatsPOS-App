package com.example.statspos.presentation.viewmodels.utilities.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
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
import okhttp3.MultipartBody
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val api: UsersRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val contact: String = "",
        val email: String = "",
        val dateOfBirth: LocalDate = LocalDate.now(),
        val address: String = "",
        val imageUrl: String = "",

        // Extras
        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isUploadingImage: Boolean = false,
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
    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        state.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        state.update { it.copy(confirmPassword = value) }
    }

    fun onContactChange(value: String) {
        state.update { it.copy(contact = value) }
    }

    fun onEmailChange(value: String) {
        state.update { it.copy(email = value) }
    }

    fun onAddressChange(value: String) {
        state.update { it.copy(address = value) }
    }

    fun onDateOfBirthChange(value: LocalDate) {
        state.update { it.copy(dateOfBirth = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val user = getFormDataUsers()
            val userRights = getFormDataUserRights()

            val result = api.updateUser(HP.user.id!!, user, userRights)

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    val updateUser =
                        Gson().get<Users>(result.data.get("user").asJsonObject)

                    HP.users =
                        Gson().getListOf<DropdownItem>(result.data.get("users").asJsonArray)

                    HP.user.username = updateUser.username
                    HP.user.imageUrl = updateUser.imageUrl

//                    clearTextboxes()
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

            when (val result = api.getUser(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val user = Gson().get<Users>(result.data.get("user").asJsonObject)
                    setFormDataUser(user)
                }
            }
        }
    }

    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            state.update { it.copy(isUploadingImage = true) }

            when (val result = mainRepo.uploadImage(multipart)) {
                is Resource.Error -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showError(result.error)
                }

                is Resource.Information -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showMessage(result.message)
                }

                is Resource.Success -> {
                    val fileName = result.data.asJsonObject.get("fileName").asString
                    state.update {
                        it.copy(
                            isUploadingImage = false,
                            imageUrl = fileName,
                        )
                    }
                }
            }
        }
    }

    fun deleteImage(imageUrl: String) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if(imageUrl.isEmpty())
                return@launch

            beforeRequest()

            when (val result = mainRepo.deleteImage(imageUrl)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    state.update { it.copy(imageUrl = "") }
                }
            }
        }
    }

    // endregion

    // region Methods
    private fun getFormDataUsers(): Users {
        return Users(
            username = state.value.username,
            password = state.value.password,
            contact = state.value.contact,
            email = state.value.email,
            address = state.value.address,
            dateOfBirth = HP.getZonedDate(state.value.dateOfBirth),

            imageUrl = state.value.imageUrl,
        )
    }

    private fun getFormDataUserRights(): UserRights {
        return UserRights(
            userId = HP.user.id!!
        )
    }

    private fun setFormDataUser(user: Users) {
        state.update {
            it.copy(
                username = user.username.toString(),
                password = user.password.toString(),
                confirmPassword = user.password.toString(),
                contact = user.contact.toString(),
                email = user.email.toString(),
                address = user.address.toString(),
                dateOfBirth = HP.toLocalDate(user.dateOfBirth.toString()),

                imageUrl = user.imageUrl!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                username = "",
                password = "",
                confirmPassword = "",
                contact = "",
                email = "",
                address = "",
                dateOfBirth = LocalDate.now(),

                imageUrl = "",
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.username.isEmpty()) {
            showMessage("Please enter username")
            return false
        }

        if (state.value.password.isEmpty()) {
            showMessage("Please enter password")
            return false
        }

        if (state.value.confirmPassword.isEmpty()) {
            showMessage("Re-enter password")
            return false
        }

        if (state.value.password != state.value.confirmPassword) {
            showMessage("Password didn't match")
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
    // endregion
}