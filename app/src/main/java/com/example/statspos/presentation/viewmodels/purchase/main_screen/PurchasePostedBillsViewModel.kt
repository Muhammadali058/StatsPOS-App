package com.example.statspos.presentation.viewmodels.purchase.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.domain.repository.purchase.PurchaseRepository
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
class PurchasePostedBillsViewModel @Inject constructor(
    private val api: PurchaseRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<PurchaseBills> = emptyList(),
        val totalBills: Int = 0,
        val page: Int = 1,
        val endReached: Boolean = false,

        val search: String = "",
        val searchBy: DropdownItem = HP.purchasePostedBillsSearchBy[0],
        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),
        val user: DropdownItem = HP.getDropdownById(HP.user.id!!, HP.users),
        val purchaseType: DropdownItem = HP.getNoneDropdownItem("Both"),
        val purchaseOn: DropdownItem = HP.getNoneDropdownItem("Both"),
        val mop: DropdownItem = HP.getNoneDropdownItem("Both"),

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
    fun onPurchaseTypeChange(value: DropdownItem) {
        state.update { it.copy(purchaseType = value) }
    }
    fun onPurchaseOnChange(value: DropdownItem) {
        state.update { it.copy(purchaseOn = value) }
    }
    fun onMOPChange(value: DropdownItem) {
        state.update { it.copy(mop = value) }
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
                    val resultList = Gson().getListOf<PurchaseBills>(result.data.get("rows").asJsonArray)
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

            if(state.value.list.size < HP.ITEMS_PER_PAGE)
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
                    val resultList = Gson().getListOf<PurchaseBills>(result.data.get("rows").asJsonArray)
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
        addProperty("itemsPerPage", HP.ITEMS_PER_PAGE)
        addProperty("fromDate", HP.getZonedDate(state.value.fromDate))
        addProperty("toDate", HP.getZonedDate(state.value.toDate))
        addProperty("purchaseOn", state.value.purchaseOn.id)
        addProperty("purchaseType", state.value.purchaseType.id)
        addProperty("mop", state.value.mop.id)
        addProperty("itemId", 0)
        addProperty("userId", state.value.user.id)
        addProperty("searchBy", state.value.searchBy.id)
        addProperty("text", state.value.search)
    }

    // endregion
}