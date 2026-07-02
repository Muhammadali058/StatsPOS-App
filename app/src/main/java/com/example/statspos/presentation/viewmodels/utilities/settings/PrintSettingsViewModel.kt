package com.example.statspos.presentation.viewmodels.utilities.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.utilities.settings.PrintSettings
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
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
class PrintSettingsViewModel @Inject constructor(
    private val api: SettingsRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,

        val shopName: String = "",
        val contact: String = "",
        val address: String = "",

        val showUrdu: Boolean = false,
        val showLogo: Boolean = false,
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
    fun onShopNameChange(value: String) {
        state.update { it.copy(shopName = value) }
    }

    fun onContactChange(value: String) {
        state.update { it.copy(contact = value) }
    }

    fun onAddressChange(value: String) {
        state.update { it.copy(address = value) }
    }

    fun onShowUrduChange(value: Boolean) {
        state.update { it.copy(showUrdu = value) }
    }

    fun onShowLogoChange(value: Boolean) {
        state.update { it.copy(showLogo = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updatePrintSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            state.update { it.copy(isSaving = true) }

            val printSettings = getFormData()
            val result = api.updatePrintSettings(printSettings)

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {

                    HP.printSettings =
                        Gson().get<PrintSettings>(result.data.toString())

                    onSuccess()
                }
            }
        }
    }

    fun editData() {
        viewModelScope.launch {
            setFormData(HP.printSettings)
        }
    }

    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            if (state.value.isLoading)
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
                    state.update { it.copy(
                        isUploadingImage = false,
                        imageUrl = fileName,
                    ) }
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
    private fun getFormData(): PrintSettings {
        return PrintSettings(
            shopName = state.value.shopName,
            contact = state.value.contact,
            address = state.value.address,

            showUrdu = state.value.showUrdu,
            showLogo = state.value.showLogo,
            imageUrl = state.value.imageUrl,
        )
    }

    private fun setFormData(printSettings: PrintSettings) {
        state.update {
            it.copy(
                shopName = printSettings.shopName!!,
                contact = printSettings.contact!!,
                address = printSettings.address!!,

                showUrdu = printSettings.showUrdu!!,
                showLogo = printSettings.showLogo!!,
                imageUrl = printSettings.imageUrl!!,
            )
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
        state.update { it.copy(isLoading = false) }
    }

    // endregion
}