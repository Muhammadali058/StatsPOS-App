package com.example.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.reports.ChartReport
import com.example.statspos.domain.models.reports.MainReport
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.purchase.PurchaseBillWiseReport
import com.example.statspos.domain.models.reports.purchase.PurchaseItemsReport
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.reports.PurchaseReportsRepository
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
class PurchaseReportsViewModel @Inject constructor(
    private val api: PurchaseReportsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val itemId: Long = 0L,
        val itemname: String = "",
        val categoryName: String = "",
        val subCategoryName: String = "",
        val vendorName: String = "",
        val accountCategoryName: String = "",
        val warehouseName: String = "",
        val supplierName: String = "",
        val username: String = "",

        val categoryId: Long = 0L,
        val subCategoryId: Long = 0L,
        val vendorId: Long = 0L,
        val accountCategoryId: Long = 0L,
        val warehouseId: Long = 0L,
        val supplierId: Long = 0L,
        val userId: Long = 0L,

        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),
        val purchaseType: DropdownItem = HP.getNoneDropdownItem("Both"),
        val purchaseOn: DropdownItem = HP.getNoneDropdownItem("Both"),
        val mop: DropdownItem = HP.getNoneDropdownItem("Both"),
        val sum: Boolean = false,

        val chartDuration: DropdownItem = HP.chartDurations[0],

        val mainReport: MainReport = MainReport(),
        val chartReport: List<ChartReport> = emptyList(),
        val purchaseBillWiseReport: List<PurchaseBillWiseReport>? = null,
        val purchaseItemsReport: List<PurchaseItemsReport>? = null,
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
        loadChartReport()
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

    fun onAccountCategoryNameChange(value: String) {
        state.update { it.copy(accountCategoryName = value) }
    }

    fun onWarehouseNameChange(value: String) {
        state.update { it.copy(warehouseName = value) }
    }

    fun onSupplierNameChange(value: String) {
        state.update { it.copy(supplierName = value) }
    }

    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
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

    fun onAccountCategoryIdChange(value: Long) {
        state.update { it.copy(accountCategoryId = value) }
    }

    fun onWarehouseIdChange(value: Long) {
        state.update { it.copy(warehouseId = value) }
    }

    fun onSupplierIdChange(value: Long) {
        state.update { it.copy(supplierId = value) }
    }

    fun onUserIdChange(value: Long) {
        state.update { it.copy(userId = value) }
    }

    fun onFromDateChange(value: LocalDate) {
        state.update { it.copy(fromDate = value) }
    }

    fun onToDateChange(value: LocalDate) {
        state.update { it.copy(toDate = value) }
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

    fun onSumChange(value: Boolean) {
        state.update { it.copy(sum = value) }
    }

    fun onChartDurationChange(value: DropdownItem) {
        state.update { it.copy(chartDuration = value) }
        loadChartReport()
    }

    // endregion

    // region Button Clicks
    fun onTotalBillsClick(onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit) {
        loadBillWiseReport(getParams(), onSuccess)
    }

    fun onTotalItemsClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        loadItemsReport(getParams(), onSuccess)
    }

    fun onFilterClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("itemId", state.value.itemId)
        params.addProperty("categoryId", state.value.categoryId)
        params.addProperty("subCategoryId", state.value.subCategoryId)
        params.addProperty("vendorId", state.value.vendorId)
        params.addProperty("vendorCategoryId", state.value.accountCategoryId)
        params.addProperty("warehouseId", state.value.warehouseId)
        params.addProperty("supplierId", state.value.supplierId)
        params.addProperty("userId", state.value.userId)
        loadItemsReport(params, onSuccess)
    }

    fun onItemClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        if (state.value.itemId == 0L) {
            showMessage("Select item")
        } else {
            val params = getParams()
            params.addProperty("itemId", state.value.itemId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onCategoryClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        if (state.value.categoryId == 0L) {
            showMessage("Select category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.categoryId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onSubCategoryClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        if (state.value.subCategoryId == 0L) {
            showMessage("Select sub-category")
        } else {
            val params = getParams()
            params.addProperty("subCategoryId", state.value.subCategoryId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onVendorClick(onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit) {
        if (state.value.vendorId == 0L) {
            showMessage("Select vendor")
        } else {
            val params = getParams()
            params.addProperty("vendorId", state.value.vendorId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onAccountCategoryClick(onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.accountCategoryId == 0L) {
            showMessage("Select vendor category")
        } else {
            val params = getParams()
            params.addProperty("vendorCategoryId", state.value.accountCategoryId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onWarehouseClick(onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.warehouseId == 0L) {
            showMessage("Select warehouse")
        } else {
            val params = getParams()
            params.addProperty("warehouseId", state.value.warehouseId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onSupplierClick(onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.supplierId == 0L) {
            showMessage("Select supplier")
        } else {
            val params = getParams()
            params.addProperty("supplierId", state.value.supplierId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onUserClick(onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.userId == 0L) {
            showMessage("Select user")
        } else {
            val params = getParams()
            params.addProperty("userId", state.value.userId)
            loadBillWiseReport(params, onSuccess)
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

    private fun loadChartReport() {
        viewModelScope.launch {
//            if (state.value.isLoading)
//                return@launch
//
//            beforeRequest()

            val params = getChartParams()
            val result = when (state.value.chartDuration.id) {
                1L -> {
                    params.addProperty("days", 7)
                    api.chartDaily(params)
                }

                2L -> {
                    params.addProperty("weeks", 7)
                    api.chartWeekly(params)
                }

                3L -> {
                    params.addProperty("months", 7)
                    api.chartMonthly(params)
                }

                else -> {
                    params.addProperty("years", 7)
                    api.chartYearly(params)
                }
            }


            when (result) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
//                    resultSuccess()

                    val chartReport = result.data.asJsonArray.map { obj ->
                        val date = obj.asJsonObject.get("date").asString
                        ChartReport(
                            total = obj.asJsonObject.get("total").asDouble,
                            date = when (state.value.chartDuration.id) {
                                1L -> {
                                    date.split("/")[0]
                                }

                                2L -> {
                                    "W" + date.split("-")[1]
                                }

                                3L -> {
                                    "M" + date.split("-")[0]
                                }

                                else -> {
                                    date.substring(2, 4)
                                }
                            },
                        )
                    }

                    state.update {
                        it.copy(
                            chartReport = chartReport,
                        )
                    }
                }
            }
        }
    }

    private fun loadBillWiseReport(
        params: JsonObject,
        onSuccess: (List<PurchaseBillWiseReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.billWiseReport(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val purchaseBillWiseReport =
                        Gson().getListOf<PurchaseBillWiseReport>(result.data.get("rows").asJsonArray)
                    if (purchaseBillWiseReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                purchaseBillWiseReport = purchaseBillWiseReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(purchaseBillWiseReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadItemsReport(
        params: JsonObject,
        onSuccess: (List<PurchaseItemsReport>, TotalReport) -> Unit
    ) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            params.addProperty("sum", state.value.sum)

            when (val result = api.itemsReport(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val purchaseItemsReport =
                        Gson().getListOf<PurchaseItemsReport>(result.data.get("rows").asJsonArray)
                    if (purchaseItemsReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                purchaseItemsReport = purchaseItemsReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(purchaseItemsReport, totalReport)
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
            addProperty("fromDate", HP.getZonedDateWithFromTime(state.value.fromDate))
            addProperty("toDate", HP.getZonedDateWithToTime(state.value.toDate))
            addProperty("purchaseOn", state.value.purchaseOn.id.toInt())
            addProperty("purchaseType", state.value.purchaseType.id.toInt())
            addProperty("mop", state.value.mop.id.toInt())
        }
    }

    private fun getChartParams(): JsonObject {
        return JsonObject().apply {
            addProperty("date", HP.getZonedDate(LocalDate.now()))
            addProperty("purchaseOn", state.value.purchaseOn.id.toInt())
            addProperty("purchaseType", state.value.purchaseType.id.toInt())
            addProperty("mop", state.value.mop.id.toInt())
        }
    }

// endregion
}