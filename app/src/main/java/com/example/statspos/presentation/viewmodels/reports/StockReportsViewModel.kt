package com.example.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.reports.MainReport
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.stock.StockItemsReport
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.reports.StockReportsRepository
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StockReportsViewModel @Inject constructor(
    private val api: StockReportsRepository,
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

        val warehouse: DropdownItem = HP.getNoneDropdownItem(),

        val stockShowing: DropdownItem = HP.stockShowing[0],
        val stockAt: DropdownItem = HP.stockAt[0],
        val stockExpiry: DropdownItem = HP.stockExpiry[0],
        val stockType: DropdownItem = HP.stockType[2],

        val costHeading: String = "Cost",

        val mainReport: MainReport = MainReport(),
        val stockItemsReport: List<StockItemsReport>? = null,
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

    init {
        loadMainReport()
    }

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

    fun onWarehouseChange(value: DropdownItem) {
        state.update { it.copy(warehouse = value) }
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

    fun onStockShowingChange(value: DropdownItem) {
        state.update { it.copy(stockShowing = value) }
    }

    fun onStockAtChange(value: DropdownItem) {
        state.update { it.copy(stockAt = value) }
        state.update {
            it.copy(
                costHeading = when (value.id) {
                    1L -> "Cost"
                    2L -> "Retail"
                    3L -> "Wholesale"
                    else -> "Cost"
                }
            )
        }
    }

    fun onStockExpiryChange(value: DropdownItem) {
        state.update { it.copy(stockExpiry = value) }
    }

    fun onStockTypeChange(value: DropdownItem) {
        state.update { it.copy(stockType = value) }
    }

    // endregion

    // region Button Clicks
    fun onTotalClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        loadStockReport(getParams(), onSuccess)
    }

    fun onFilterClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("itemId", state.value.itemId)
        params.addProperty("categoryId", state.value.categoryId)
        params.addProperty("subCategoryId", state.value.subCategoryId)
        params.addProperty("vendorId", state.value.vendorId)
        loadStockReport(params, onSuccess)
    }

    fun onItemClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        if (state.value.itemId == 0L) {
            showMessage("Select item")
        } else {
            val params = getParams()
            params.addProperty("itemId", state.value.itemId)
            loadStockReport(params, onSuccess)
        }

    }

    fun onCategoryClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        if (state.value.categoryId == 0L) {
            showMessage("Select category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.categoryId)
            loadStockReport(params, onSuccess)
        }

    }

    fun onSubCategoryClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        if (state.value.subCategoryId == 0L) {
            showMessage("Select sub-category")
        } else {
            val params = getParams()
            params.addProperty("subCategoryId", state.value.subCategoryId)
            loadStockReport(params, onSuccess)
        }

    }

    fun onVendorClick(onSuccess: (List<StockItemsReport>, TotalReport) -> Unit) {
        if (state.value.vendorId == 0L) {
            showMessage("Select vendor")
        } else {
            val params = getParams()
            params.addProperty("vendorId", state.value.vendorId)
            loadStockReport(params, onSuccess)
        }

    }

    // endregion

    // region Network calls
    private fun loadMainReport() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch
//
//            beforeRequest()

            val params = JsonObject().apply {
                addProperty("date", HP.getZonedDate(LocalDate.now()))
            }

            when (val result = api.mainReport(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
//                    resultSuccess()

                    val mainReport = Gson().get<MainReport>(result.data.asJsonObject)

                    state.update {
                        it.copy(
                            mainReport = mainReport,
                        )
                    }
                }
            }
        }
    }

    private fun loadStockReport(
        params: JsonObject,
        onSuccess: (List<StockItemsReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.stockReport(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val stockItemsReport =
                        Gson().getListOf<StockItemsReport>(result.data.get("rows").asJsonArray)
                    if (stockItemsReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                stockItemsReport = stockItemsReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(stockItemsReport, totalReport)
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
        return JsonObject().apply {
            addProperty("warehouseId", state.value.warehouse.id)
            addProperty("isOpeningStock", state.value.stockShowing.id != 1L)
            addProperty("stockAt", getStockAt())
            addProperty("stockExpiry", getStockExpiry())
            addProperty("stockType", getStockType())
            addProperty("expiryDays", 7)
        }
    }

    private fun getStockAt(): String {
        return when (state.value.stockAt.id) {
            1L -> "cost"
            2L -> "retail"
            3L -> "wholesale"
            else -> "cost"
        }
    }

    private fun getStockExpiry(): String {
        return when (state.value.stockExpiry.id) {
            1L -> "all"
            2L -> "expirable"
            3L -> "expired"
            else -> "all"
        }
    }

    private fun getStockType(): String {
        return when (state.value.stockType.id) {
            1L -> "all"
            2L -> "zero"
            3L -> "notZero"
            4L -> "under"
            5L -> "over"
            else -> "all"
        }
    }

// endregion
}