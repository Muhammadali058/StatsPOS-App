package com.graphees.statspos.presentation.viewmodels.accounts.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.repository.accounts.CustomersRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.getListOf
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
class CustomersViewModel @Inject constructor(
    private val api: CustomersRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<Accounts> = emptyList(),
        val totalCustomers: Int = 0,
        val page: Int = 1,
        val endReached: Boolean = false,

        val search: String = "",
        val categoryName: String = "",
        val categoryId: Long = 0L,
        val selectedSearchType: DropdownItem? = null,

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

        if(HP.appSettings.instantSearch == true)
            loadData()
    }

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }

    fun onSelectedSearchTypeChange(value: DropdownItem) {
        state.update { it.copy(selectedSearchType = value) }
    }
    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch
//
//            if (state.value.isLoadingNextPage)
//                return@launch

            state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    page = 1,
                    endReached = false,
                )
            }

            val params = getSearchParams(1)

            when (val result = api.loadCustomers(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal = result.data.get("total").asJsonObject.get("totalCustomers").asInt
                    val resultList = Gson().getListOf<Accounts>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalCustomers = resultTotal,
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

            when (val result = api.loadCustomers(params)) {
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

                    val resultTotal = result.data.get("total").asJsonObject.get("totalCustomers").asInt
                    val resultList = Gson().getListOf<Accounts>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = state.value.list + resultList,
                            totalCustomers = resultTotal,
                            endReached = resultList.isEmpty(),
                        )
                    }
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (id == 0L)
                return@launch

            beforeRequest()

            when (val result = api.deleteCustomer(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    state.update {
                        it.copy(
                            list = state.value.list.filter { it.id != id },
                            totalCustomers = state.value.totalCustomers - 1,
                        )
                    }

                    HP.customers =
                        Gson().getListOf<DropdownItem>(result.data.get("customers").asJsonArray)
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

    private fun getSearchParams(page: Int): JsonObject = JsonObject().apply {
        addProperty("page", page)
        addProperty("itemsPerPage", HP.ITEMS_PER_PAGE)
        addProperty("searchType", state.value.selectedSearchType?.id ?: 0L)
        addProperty("categoryId", state.value.categoryId)
        addProperty("text", state.value.search)
    }

    // endregion
}