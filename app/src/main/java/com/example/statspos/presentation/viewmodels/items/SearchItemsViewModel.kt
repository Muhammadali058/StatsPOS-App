package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
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
class SearchItemsViewModel @Inject constructor(
    private val api: ItemsRepository
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val list: List<Items> = emptyList(),
        val totalItems: Int = 0,
        val page: Int = 1,
        val endReached: Boolean = false,
        val search: String = "",
        val category: DropdownItem = HP.getNoneDropdownItem(),
        val subCategory: DropdownItem = HP.getNoneDropdownItem(),
        val vendorName: String = "",
        val vendorId: Long = 0L,
        val selectedSearchBy: DropdownItem? = HP.itemFilters[0],

        val hasLoadedOnce: Boolean = false,

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
        loadItems()
    }

    // region onChangeMethods
    fun onSearchChange(value: String) {
        state.update { it.copy(search = value) }

        if(HP.appSettings.instantSearch == true)
            loadItems()
    }

    fun onCategoryChange(value: DropdownItem) {
        state.update { it.copy(category = value) }
    }

    fun onSubCategoryChange(value: DropdownItem) {
        state.update { it.copy(subCategory = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
    }

    fun onSelectedSearchByChange(value: DropdownItem) {
        state.update { it.copy(selectedSearchBy = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun loadItems() {
        viewModelScope.launch {
//            if (state.value.isLoading)
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

            when (val result = api.loadItems(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal = result.data.get("total").asJsonObject.get("totalItems").asInt
                    val resultList = Gson().getListOf<Items>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalItems = resultTotal,
                        )
                    }
                }
            }
        }
    }

    fun loadNextItems() {
        viewModelScope.launch {
            if (state.value.isLoadingNextPage)
                return@launch

            state.update {
                it.copy(
                    isLoadingNextPage = true,
                    error = null,
                    page = state.value.page + 1,
                )
            }

            val params = getSearchParams(state.value.page)

            when (val result = api.loadItems(params)) {
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

                    val resultTotal = result.data.get("total").asJsonObject.get("totalItems").asInt
                    val resultList = Gson().getListOf<Items>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = state.value.list + resultList,
                            totalItems = resultTotal,
                            endReached = resultList.isEmpty(),
                        )
                    }
                }
            }
        }
    }

    fun getItem(value: String) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (value.isEmpty())
                return@launch

            state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    page = 1,
                    endReached = true,
                )
            }

            when (val result = api.isBarcodeExists(value)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val isExists = result.data.get("isExists").asBoolean
                    if (isExists) {
                        val item = Gson().get<Items>(result.data.get("data").asJsonObject)
                        state.update {
                            it.copy(
                                list = listOf(item),
                                totalItems = 1,
                            )
                        }
                    } else {
                        state.update {
                            it.copy(
                                list = emptyList(),
                                totalItems = 0,
                            )
                        }
                        showSnackbar("Items not found")
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
        addProperty("searchBy", state.value.selectedSearchBy?.id ?: 0L)
        addProperty("categoryId", state.value.category.id)
        addProperty("subCategoryId", state.value.subCategory.id)
        addProperty("vendorId", state.value.vendorId)
        addProperty("text", state.value.search)
    }

    // endregion
}