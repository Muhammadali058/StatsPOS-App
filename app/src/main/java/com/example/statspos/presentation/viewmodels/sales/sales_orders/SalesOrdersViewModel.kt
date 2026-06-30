package com.example.statspos.presentation.viewmodels.sales.sales_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.domain.repository.firebase.FirebaseRepository
import com.example.statspos.domain.repository.sales.SalesOrdersRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.getListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.itextpdf.styledxmlparser.jsoup.select.Collector.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.time.LocalDate
import kotlin.text.get

@HiltViewModel
class SalesOrdersViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepository,
    private val api: SalesOrdersRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val orders: List<SalesOrders> = emptyList(),
        val statusList: List<DropdownItem> = listOf(
            DropdownItem(1L, "New"),
            DropdownItem(2L, "Processing"),
            DropdownItem(3L, "Delivered"),
            DropdownItem(4L, "Cancelled"),
        ),
        val selectedStatus: DropdownItem = DropdownItem(1L, "New"),
        val date: LocalDate = LocalDate.now(),

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val updatingOrderId: Long? = null,
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
        loadOrdersFB()
    }

    // region Button Clicks
    fun onAccept(salesOrderId: Long) {
        updateStatus(salesOrderId, "processing")
    }

    fun onDelivered(salesOrderId: Long) {
        updateStatus(salesOrderId, "delivered")
    }

    fun onCancel(salesOrderId: Long) {
        updateStatus(salesOrderId, "cancelled")
    }
    // endregion

    // region Network calls
    fun loadOrders() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("status", state.value.selectedStatus.name)
                addProperty("date", HP.getZonedDate(state.value.date))
                addProperty("loadAll", true)
            }

            when (val result = api.loadSalesOrders(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val orders = Gson().getListOf<SalesOrders>(result.data.get("rows").asJsonArray)
                    state.update { it.copy(orders = orders) }
                }
            }
        }
    }

    fun loadOrdersFB() {
        viewModelScope.launch {
            beforeRequest()

            firebaseRepo.loadOrdersRealtime(
                state.value.selectedStatus.name,
                HP.getFormatedDate(LocalDate.now())
            ).collect { result ->
                loadOrders()
            }
        }
    }

    fun updateStatus(salesOrderId: Long, status: String) {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch

            state.update { it.copy(updatingOrderId = salesOrderId) }

            val salesOrder = SalesOrders(
                id = salesOrderId,
                status = status,
            )
            val result = api.updateSalesOrder(salesOrder)

            state.update { it.copy(updatingOrderId = null) }

            when (result) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    loadOrders()
                }
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    fun signOut(onSuccess: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        onSuccess()
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
