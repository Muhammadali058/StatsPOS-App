package com.example.statspos.presentation.viewmodels.items.sub_barcodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.domain.repository.items.SubBarcodesRepository
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
import javax.inject.Inject

@HiltViewModel
class AddUpdateSubBarcodeViewModel @Inject constructor(
    private val api: SubBarcodesRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val subBarcode: String = "",
        val isCrtn: Boolean = false,
        val itemId: Long = 0L,

        // Extra
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
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
    fun onSubBarcodeChange(value: String) {
        state.update { it.copy(subBarcode = value) }
    }
    fun onIsCrtnChange(value: Boolean) {
        state.update { it.copy(isCrtn = value) }
    }
    // endregion

    // region Network calls
    fun insertOrUpdateData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val subBarcode = getFormData()

            val result = if (state.value.isUpdate) {
                subBarcode.id = state.value.updateId
                api.updateSubBarcode(subBarcode)
            } else {
                api.insertSubBarcode(subBarcode)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> {
                    if(result.data != null){
                        val message = result.message + " ${result.data.get("data").asJsonObject.get("itemname").asString}"
                        showMessage(message)
                    }else{
                        showMessage(result.message)
                    }
                }
                is Resource.Success -> {
//                    clearTextboxes()
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

            beforeRequest()

            when (val result = api.deleteSubBarcode(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

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

            when (val result = api.getSubBarcode(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val subBarcode = Gson().get<SubBarcodes>(result.data.asJsonObject)
                    setFormData(subBarcode)
                }
            }
        }
    }

    // endregion

    // region Methods
    private fun getFormData(): SubBarcodes {
        return SubBarcodes(
            itemId = state.value.itemId,
            subBarcode = state.value.subBarcode,
            isCrtn = state.value.isCrtn,
        )
    }

    private fun setFormData(subBarcodeTemp: SubBarcodes) {
        state.update {
            it.copy(
                subBarcode = subBarcodeTemp.subBarcode.toString(),
                isCrtn = subBarcodeTemp.isCrtn!!,

            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                subBarcode = "",
                isCrtn = false,

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.subBarcode.isEmpty()) {
            showMessage("Please enter barcode")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, itemId: Long) {
        state.update { it.copy(
            isUpdate = isUpdate,
            updateId = updateId,
            itemId = itemId,
        ) }
    }

    // endregion
}