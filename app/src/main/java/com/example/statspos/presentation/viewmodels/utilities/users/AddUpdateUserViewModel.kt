package com.example.statspos.presentation.viewmodels.utilities.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Accounts
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
import javax.inject.Inject

@HiltViewModel
class AddUpdateUserViewModel @Inject constructor(
    private val api: UsersRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val userType: DropdownItem = HP.userTypes[0],
        val shift: DropdownItem = HP.shifts[0],

        val imageUrl: String = "",

        // User Rights
        val items: Boolean = true,
        val sales: Boolean = true,
        val purchase: Boolean = true,
        val categories: Boolean = true,
        val warehouse: Boolean = true,
        // Accounts
        val customers: Boolean = true,
        val vendors: Boolean = true,
        val suppliers: Boolean = true,
        val expenses: Boolean = true,
        val banks: Boolean = true,
        // Utilities
        val users: Boolean = true,
        val settings: Boolean = true,
        val barcodeLabels: Boolean = true,
        val employees: Boolean = true,
        // Reports
        val salesReports: Boolean = true,
        val purchaseReports: Boolean = true,
        val profitReports: Boolean = true,
        val stockReports: Boolean = true,
        val accountReports: Boolean = true,
        val itemsReports: Boolean = true,
        val auditReports: Boolean = true,
        // Others
        val dateWiseEntry: Boolean = true,
        val dateWisePurchase: Boolean = true,
        val printDuplicates: Boolean = true,
        val deleteAnything: Boolean = true,
        // POS
        val changeRates: Boolean = true,
        val seeMargin: Boolean = true,
        val salesReturn: Boolean = true,
        val creditBill: Boolean = true,
        val editSalesBill: Boolean = true,

        // Extras
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

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

    fun onUserTypeSelected(value: DropdownItem) {
        state.update { it.copy(userType = value) }
    }

    fun onShiftSelected(value: DropdownItem) {
        state.update { it.copy(shift = value) }
    }

//    User Rights
    fun onItemsChange(value: Boolean) {
        state.update { it.copy(items = value) }
    }

    // endregion

    // region Network calls
    fun insertOrUpdateData(onSuccess: () -> Unit) {
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

            val result = if (state.value.isUpdate) {
                api.updateUser(state.value.updateId, user, userRights)
            } else {
                api.insertUser(user, userRights)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    HP.users =
                        Gson().getListOf<DropdownItem>(result.data.get("users").asJsonArray)
                    clearTextboxes()
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            beforeRequest()

            when (val result = api.deleteUser(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.users =
                        Gson().getListOf<DropdownItem>(result.data.get("users").asJsonArray)
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
                    val userRights = Gson().get<UserRights>(result.data.get("userRights").asJsonObject)

                    setFormDataUser(user)
                    setFormDataUserRights(userRights)
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
    // endregion

    // region Methods
    private fun getFormDataUsers(): Users {
        return Users(
            username = state.value.username,
            password = state.value.password,
            userType = state.value.userType.id.toInt(),
            shift = state.value.shift.id.toInt(),

            imageUrl = state.value.imageUrl,
        )
    }

    private fun getFormDataUserRights(): UserRights {
        return UserRights(
            items = state.value.items,
        )
    }

    private fun setFormDataUser(user: Users) {
        state.update {
            it.copy(
                username = user.username.toString(),
                password = user.password.toString(),
                confirmPassword = user.password.toString(),
                userType = HP.getDropdownById(user.userType?.toLong() ?: 0, HP.userTypes)!!,
                shift = HP.getDropdownById(user.shift?.toLong() ?: 0, HP.shifts)!!,

                imageUrl = user.imageUrl!!,
            )
        }
    }

    private fun setFormDataUserRights(userRights: UserRights) {
        state.update {
            it.copy(
                items = userRights.items!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                username = "",
                password = "",
                confirmPassword = "",
                userType = HP.userTypes[0],
                shift = HP.shifts[0],

                imageUrl = "",

                // Extras
                isUpdate = false,
                updateId = 0L,
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long) {
        state.update { it.copy(isUpdate = isUpdate, updateId = updateId) }
    }

    // endregion
}