package com.example.statspos.presentation.viewmodels.warehouse.gatepass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.warehouse.Gatepasses
import com.example.statspos.domain.repository.warehouse.GatepassesRepository
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddUpdateGatepassViewModel @Inject constructor(
    private val api: GatepassesRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val warehouseId: Long = 0L,
        val date: LocalDate = LocalDate.now(),
        val gatepassName: String = "",
        val remarks: String = "",

        // Extra
        val warehouseName: String = "",
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
    fun onWarehouseNameChange(value: String) {
        state.update { it.copy(warehouseName = value) }
    }

    fun onWarehouseIdChange(value: Long) {
        state.update { it.copy(warehouseId = value) }
    }

    fun onGatepassNameChange(value: String) {
        state.update { it.copy(gatepassName = value) }
    }

    fun onRemarksChange(value: String) {
        state.update { it.copy(remarks = value) }
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

            val gatepass = getFormData()

            val result = if (state.value.isUpdate) {
                gatepass.id = state.value.updateId
                api.updateGatepass(gatepass)
            } else {
                api.insertGatepass(gatepass)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
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

            when (val result = api.deleteGatepass(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

//                    HP.subExpenses = result.data.get("subExpenses").asJsonArray.map { obj ->
//                        DropdownItem(
//                            id = obj.asJsonObject.get("id").asLong,
//                            name = obj.asJsonObject.get("name").asString,
//                            mainId = obj.asJsonObject.get("expenseId").asLong,
//                        )
//                    }
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

            when (val result = api.getGatepass(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val gatepass = Gson().get<Gatepasses>(result.data.asJsonObject)
                    setFormData(gatepass)
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Gatepasses {
        return Gatepasses(
            warehouseId = state.value.warehouseId,
            date = HP.getZonedDate(state.value.date),
            gatepassName = state.value.gatepassName,
            remarks = state.value.remarks,
        )
    }

    private fun setFormData(gatepass: Gatepasses) {
        state.update {
            it.copy(
//                warehouseId = gatepass.warehouseId!!,
//                date = HP.toLocalDate(gatepass.date!!),
                gatepassName = gatepass.gatepassName!!,
                remarks = gatepass.remarks!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                gatepassName = "",
                remarks = "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.gatepassName.isEmpty()) {
            showMessage("Please enter gatepass name")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, warehouseId: Long, date: LocalDate) {
        state.update {
            it.copy(
                isUpdate = isUpdate,
                updateId = updateId,
                date = date,
                warehouseId = warehouseId,
                warehouseName = if (warehouseId == 0L) "" else HP.getDropdownNameById(
                    warehouseId,
                    HP.warehouses
                )
            )
        }
    }
    // endregion
}