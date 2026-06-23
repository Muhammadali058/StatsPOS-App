package com.example.statspos.presentation.viewmodels.sales.sales_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.domain.repository.firebase.FirebaseRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.itextpdf.styledxmlparser.jsoup.select.Collector.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class SalesOrdersViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val orders: List<SalesOrders> = emptyList(),
        val statusList: List<DropdownItem> = listOf(
            DropdownItem(1L, "New"),
            DropdownItem(2L, "Processing"),
            DropdownItem(3L, "Delivered"),
        ),
        val selectedStatus: DropdownItem = DropdownItem(1L, "New"),
        val date: LocalDate = LocalDate.now(),

        val hasLoadedOnce: Boolean = false,

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
    fun onSelectedStatusChange(value: DropdownItem) {
        state.update { it.copy(selectedStatus = value) }
        loadOrders()
    }

    fun onDateChange(value: LocalDate) {
        state.update { it.copy(date = value) }
        loadOrders()
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }
    // endregion

    init {
        loadOrders()
    }

    // region Network calls
    fun loadOrders() {
        viewModelScope.launch {
            firebaseRepo.loadOrdersRealtime(
                state.value.selectedStatus.name,
                HP.getFormatedDate(state.value.date)
            ).collect { result ->
                when (result) {
                    is Resource.Error -> resultError(result.error)
                    is Resource.Information -> resultInformation(result.message)
                    is Resource.Success -> {
                        resultSuccess()

                        state.update { it.copy(orders = result.data) }
                    }
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
        state.update { it.copy(isLoading = false) }
    }
    // endregion
}
