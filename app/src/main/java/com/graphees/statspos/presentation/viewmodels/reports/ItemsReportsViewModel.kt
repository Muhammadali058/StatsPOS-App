package com.graphees.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.items.Items
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.items.ItemsReport
import com.graphees.statspos.domain.repository.items.ItemsRepository
import com.graphees.statspos.domain.repository.reports.ItemsReportsRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.graphees.statspos.utils.getListOf
import com.graphees.statspos.utils.preloadImages
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
class ItemsReportsViewModel @Inject constructor(
    private val api: ItemsReportsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val itemId: Long = 0L,
        val itemname: String = "",
        val categoryName: String = "",
        val subCategoryName: String = "",
        val vendorName: String = "",

        val categoryId: Long = 0L,
        val subCategoryId: Long = 0L,
        val vendorId: Long = 0L,

        val itemsListType: DropdownItem = HP.itemsListType[0],

        val itemsReport: List<ItemsReport>? = null,
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
    fun onItemnameChange(value: String) {
        state.update {
            it.copy(
                itemname = value,
                itemId = 0L,
            )
        }
    }

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onListTypeChange(value: DropdownItem) {
        state.update { it.copy(itemsListType = value) }
    }

    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }

    fun onSubCategoryIdChange(value: Long) {
        state.update { it.copy(subCategoryId = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
    }

    // endregion

    // region Button Clicks
    fun onTotalItemsClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        loadItemsReport(getParams(), onSuccess)
    }

    fun onFilterItemsClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("itemId", state.value.itemId)
        params.addProperty("categoryId", state.value.categoryId)
        params.addProperty("subCategoryId", state.value.subCategoryId)
        params.addProperty("vendorId", state.value.vendorId)
        loadItemsReport(params, onSuccess)
    }

    fun onItemClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        if (state.value.itemId == 0L) {
            showMessage("Select item")
        } else {
            val params = getParams()
            params.addProperty("itemId", state.value.itemId)
            loadItemsReport(params, onSuccess)
        }
    }

    fun onCategoryClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        if (state.value.categoryId == 0L) {
            showMessage("Select category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.categoryId)
            loadItemsReport(params, onSuccess)
        }
    }

    fun onSubCategoryClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        if (state.value.subCategoryId == 0L) {
            showMessage("Select sub-category")
        } else {
            val params = getParams()
            params.addProperty("subCategoryId", state.value.subCategoryId)
            loadItemsReport(params, onSuccess)
        }
    }

    fun onVendorClick(onSuccess: (List<ItemsReport>, TotalReport) -> Unit) {
        if (state.value.vendorId == 0L) {
            showMessage("Select vendor")
        } else {
            val params = getParams()
            params.addProperty("vendorId", state.value.vendorId)
            loadItemsReport(params, onSuccess)
        }
    }

    // endregion

    // region Network calls
    private fun loadItemsReport(
        params: JsonObject,
        onSuccess: (List<ItemsReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.itemsList(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    val itemsReport =
                        Gson().getListOf<ItemsReport>(result.data.get("rows").asJsonArray)
                    if (itemsReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                itemsReport = itemsReport,
                                totalReport = totalReport,
                            )
                        }

                        if (state.value.itemsListType.id == 2L) {
                            val imageUrls = itemsReport.map {
                                it.imageUrl.toString()
                            }
                            preloadImages(imageUrls)
                        }

                        resultSuccess()
                        onSuccess(itemsReport, totalReport)
                    } else {
                        showMessage("Not record found")
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

//            beforeRequest()
            when (val result = itemsRepo.isBarcodeExists(value)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val isExists = result.data.get("isExists").asBoolean
                    if (isExists) {
                        val item = Gson().get<Items>(result.data.get("data").asJsonObject)
                        state.update {
                            it.copy(
                                itemname = item.itemname!!,
                                itemId = item.id!!,
                            )
                        }
                    } else {
                        state.update {
                            it.copy(
                                itemId = 0L,
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

    private fun getParams(): JsonObject {
        return JsonObject()
    }

// endregion
}
