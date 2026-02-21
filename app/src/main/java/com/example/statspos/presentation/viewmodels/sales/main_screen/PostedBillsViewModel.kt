package com.example.statspos.presentation.viewmodels.sales.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.domain.repository.sales.SalesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PostedBillsViewModel @Inject constructor(
    private val api: SalesRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<SalesBills> = emptyList(),
        val totalBills: Int = 0,
        val page: Int = 1,
        val endReached: Boolean = false,

        val search: String = "",
        val searchBy: DropdownItem = HP.salesPostedBillsSearchBy[0],
        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),
        val user: DropdownItem = HP.getNoneDropdownItem(),
        val salesType: DropdownItem = HP.getNoneDropdownItem("Both"),
        val salesOn: DropdownItem = HP.getNoneDropdownItem("Both"),
        val salesMop: DropdownItem = HP.getNoneDropdownItem("Both"),
        val salesRetailType: DropdownItem = HP.getNoneDropdownItem("Both"),

        val isLoading: Boolean = false,
        val isLoadingNextPage: Boolean = false,
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

    init {
        loadData()
    }

    // region onChangeMethods
    fun onSearchChange(value: String) {
        state.update { it.copy(search = value) }
    }
    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
    }
    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
    }
    fun onUserChange(value: DropdownItem) {
        state.update { it.copy(user = value) }
    }
    fun onSearchByChange(value: DropdownItem) {
        state.update { it.copy(searchBy = value) }
    }
    fun onSalesTypeChange(value: DropdownItem) {
        state.update { it.copy(salesType = value) }
    }
    fun onSalesOnChange(value: DropdownItem) {
        state.update { it.copy(salesOn = value) }
    }
    fun onSalesMOPChange(value: DropdownItem) {
        state.update { it.copy(salesMop = value) }
    }
    fun onSalesRetailTypeChange(value: DropdownItem) {
        state.update { it.copy(salesRetailType = value) }
    }
    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isLoadingNextPage)
                return@launch

            state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    page = 1,
                    endReached = false,
                )
            }

            val params = getSearchParams(1)

            when (val result = api.loadPostedBills(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal = result.data.get("total").asJsonObject.get("totalBills").asInt
                    val resultList = Gson().getListOf<SalesBills>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalBills = resultTotal,
                        )
                    }
                }
            }
        }
    }

    fun loadNextItems() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isLoadingNextPage)
                return@launch

            if(state.value.list.size < HP.itemsPerPage)
                return@launch

            state.update {
                it.copy(
                    isLoadingNextPage = true,
                    error = null,
                    page = state.value.page + 1,
                )
            }

            val params = getSearchParams(state.value.page)

            when (val result = api.loadPostedBills(params)) {
                is Resource.Error -> {
                    state.update { it.copy(isLoadingNextPage = false, error = result.error) }
                    result.error?.let { onEvent(UiEvent.ShowError(result.error)) }
                }

                is Resource.Information -> {
                    state.update { it.copy(isLoadingNextPage = false) }
                    result.message?.let { showSnackbar(result.message) }
                }

                is Resource.Success -> {
                    state.update { it.copy(isLoadingNextPage = false, error = null) }

                    val resultTotal = result.data.get("total").asJsonObject.get("totalBills").asInt
                    val resultList = Gson().getListOf<SalesBills>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = state.value.list + resultList,
                            totalBills = resultTotal,
                            endReached = resultList.isEmpty(),
                        )
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
        state.update { it.copy(isLoading = false, error = null) }
    }

    private fun getSearchParams(page: Int): JsonObject = JsonObject().apply {
        addProperty("page", page)
        addProperty("itemsPerPage", HP.itemsPerPage)
        addProperty("fromDate", HP.getZonedDateWithFromTime(state.value.fromDate))
        addProperty("toDate", HP.getZonedDateWithToTime(state.value.toDate))
        addProperty("salesOn", state.value.salesOn.id)
        addProperty("salesType", state.value.salesType.id)
        addProperty("mop", state.value.salesMop.id)
        addProperty("type", state.value.salesRetailType.id)
        addProperty("itemId", 0)
        addProperty("userId", state.value.user.id)
        addProperty("searchBy", state.value.searchBy.id)
        addProperty("text", state.value.search)
    }

    // endregion
}