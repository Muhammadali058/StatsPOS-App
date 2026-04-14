package com.example.statspos.presentation.viewmodels.utilities.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.accounts.ShiftReport
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.utilities.ShiftsRepository
import com.example.statspos.utils.EntryType
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getEntryType
import com.example.statspos.utils.getListOf
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
class ShiftsViewModel @Inject constructor(
    private val api: ShiftsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val username: String = "",
        val userId: Long = 0L,

        val userShifts: List<DropdownItem> = emptyList(),
        val selectedShift: DropdownItem = HP.getNoneDropdownItem(),
        val status: String = "",
        val cashInHand: String = "",
        val receivedAmount: String = "",
        val paidAmount: String = "",

        val shiftReport: List<ShiftReport>? = null,
        val totalReport: TotalReport? = null,

        val isLoading: Boolean = false,
        val error: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun beforeRequest() {
        state.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }
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

    fun onUserIdChange(value: Long) {
        state.update { it.copy(userId = value) }

        if (value == 0L) {
            state.update {
                it.copy(
                    userShifts = emptyList(),
                    selectedShift = HP.getNoneDropdownItem(),
                )
            }
        } else {
            loadUserShifts()
        }
    }

    fun onUserShiftChange(value: DropdownItem) {
        state.update { it.copy(selectedShift = value) }
    }

    fun onCashInHandChange(value: String) {
        state.update { it.copy(cashInHand = value) }
    }

    fun onReceivedAmountChange(value: String) {
        state.update { it.copy(receivedAmount = value) }
    }

    fun onPaidAmountChange(value: String) {
        state.update { it.copy(paidAmount = value) }
    }

    // endregion

    // region Button Clicks
    fun onShowReportClick(onSuccess: (List<ShiftReport>, TotalReport) -> Unit) {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else if (state.value.selectedShift.id == 0L) {
            showMessage("Select shift")
        } else {
            loadShiftReport(onSuccess)
        }
    }

    fun onOpenShiftClick() {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else {
            openShift()
        }
    }

    fun onCloseShiftClick() {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else {
            closeShift()
        }
    }

    fun onReceiptEntryClick(onSuccess: () -> Unit) {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else if (HP.getDoubleValue(state.value.receivedAmount) == 0.0) {
            showMessage("Enter amount")
        } else {
            postEntry(EntryType.RECEIPT, HP.getDoubleValue(state.value.receivedAmount)){
                onSuccess()
            }
        }
    }

    fun onPaymentEntryClick(onSuccess: () -> Unit) {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else if (HP.getDoubleValue(state.value.paidAmount) == 0.0) {
            showMessage("Enter amount")
        } else {
            postEntry(EntryType.PAYMENT, HP.getDoubleValue(state.value.paidAmount)){
                onSuccess()
            }
        }
    }

    // endregion

    // region Network calls
    private fun loadShiftReport(
        onSuccess: (List<ShiftReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("userId", state.value.userId)
                addProperty("currentShiftId", state.value.selectedShift.id)
            }

            when (val result = api.getShiftDetails(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val shiftReport =
                        Gson().getListOf<ShiftReport>(result.data.get("rows").asJsonArray)
                    if (shiftReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                shiftReport = shiftReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(shiftReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadUserShifts() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.loadUserShifts(state.value.userId)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val list = result.data.get("rows").asJsonArray.map { obj ->
                        DropdownItem(
                            id = obj.asJsonObject.get("id").asLong,
                            name = obj.asJsonObject.get("openDate").asString,
                        )
                    }

                    state.update { it.copy(userShifts = list) }

                    val user = Gson().get<Users>(result.data.get("user").asJsonObject)
                    if (user.currentShiftId == 0L) {
                        state.update { it.copy(status = "Closed") }
                    } else {
                        state.update { it.copy(status = "Opened") }
                    }
                }
            }
        }
    }

    private fun openShift() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.openShift(state.value.userId)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    loadUserShifts()
                }
            }
        }
    }

    private fun closeShift() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result =
                api.closeShift(state.value.userId, HP.getLongValue(state.value.cashInHand))) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    state.update {
                        it.copy(
                            status = "Closed",
                            cashInHand = "",
                        )
                    }
                }
            }
        }
    }

    private fun postEntry(entryType: EntryType, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("userId", state.value.userId)
                addProperty("entryType", getEntryType(entryType))
                addProperty("amount", amount)
            }

            when (val result = api.passEntry(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    state.update { it.copy(receivedAmount = "", paidAmount = "") }
                    onSuccess()
                }
            }
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
        state.update { it.copy(isLoading = false, error = null) }
    }
// endregion
}