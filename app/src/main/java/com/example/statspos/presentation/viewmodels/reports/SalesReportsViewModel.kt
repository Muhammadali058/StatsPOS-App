package com.example.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.sales.SalesBillWiseReport
import com.example.statspos.domain.models.reports.sales.SalesItemsReport
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.reports.SalesReportsRepository
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
class SalesReportsViewModel @Inject constructor(
    private val api: SalesReportsRepository,
    private val itemsRepo: ItemsRepository,
) : ViewModel() {

    // region ScreenState
    data class ScreenState(
        val itemId: Long = 0L,
        val itemname: String = "",
        val categoryName: String = "",
        val subCategoryName: String = "",
        val vendorName: String = "",
        val customerName: String = "",
        val accountCategoryName: String = "",
        val supplierName: String = "",
        val username: String = "",
        val categoryId: Long = 0L,
        val subCategoryId: Long = 0L,
        val vendorId: Long = 0L,
        val customerId: Long = 0L,
        val accountCategoryId: Long = 0L,
        val supplierId: Long = 0L,
        val userId: Long = 0L,

        val fromDate: LocalDate = LocalDate.now(),
        val toDate: LocalDate = LocalDate.now(),
        val salesType: DropdownItem = HP.getNoneDropdownItem("Both"),
        val salesOn: DropdownItem = HP.getNoneDropdownItem("Both"),
        val mop: DropdownItem = HP.getNoneDropdownItem("Both"),
        val salesRetailType: DropdownItem = HP.getNoneDropdownItem("Both"),
        val sum: Boolean = false,

        val salesBillWiseReport: List<SalesBillWiseReport>? = null,
        val salesItemsReport: List<SalesItemsReport>? = null,
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

    fun onCustomerNameChange(value: String) {
        state.update { it.copy(customerName = value) }
    }

    fun onAccountCategoryNameChange(value: String) {
        state.update { it.copy(accountCategoryName = value) }
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

    fun onCustomerIdChange(value: Long) {
        state.update { it.copy(customerId = value) }
    }

    fun onAccountCategoryIdChange(value: Long) {
        state.update { it.copy(accountCategoryId = value) }
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

    fun onSalesTypeChange(value: DropdownItem) {
        state.update { it.copy(salesType = value) }
    }

    fun onSalesOnChange(value: DropdownItem) {
        state.update { it.copy(salesOn = value) }
    }

    fun onMOPChange(value: DropdownItem) {
        state.update { it.copy(mop = value) }
    }

    fun onSalesRetailTypeChange(value: DropdownItem) {
        state.update { it.copy(salesRetailType = value) }
    }

    fun onSumChange(value: Boolean) {
        state.update { it.copy(sum = value) }
    }

    // endregion

    // region Button Clicks
    fun onTotalBillsClick(onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit) {
        loadBillWiseReport(getParams(), onSuccess)
    }

    fun onTotalItemsClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        loadItemsReport(getParams(), onSuccess)
    }

    fun onFilterClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        val params = getParams()
        params.addProperty("itemId", state.value.itemId)
        params.addProperty("categoryId", state.value.categoryId)
        params.addProperty("subCategoryId", state.value.subCategoryId)
        params.addProperty("vendorId", state.value.vendorId)
        params.addProperty("customerId", state.value.customerId)
        params.addProperty("customerCategoryId", state.value.accountCategoryId)
        params.addProperty("supplierId", state.value.supplierId)
        params.addProperty("userId", state.value.userId)
        loadItemsReport(params, onSuccess)
    }

    fun onItemClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        if (state.value.itemId == 0L) {
            showMessage("Select item")
        } else {
            val params = getParams()
            params.addProperty("itemId", state.value.itemId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onCategoryClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        if (state.value.categoryId == 0L) {
            showMessage("Select category")
        } else {
            val params = getParams()
            params.addProperty("categoryId", state.value.categoryId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onSubCategoryClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        if (state.value.subCategoryId == 0L) {
            showMessage("Select sub-category")
        } else {
            val params = getParams()
            params.addProperty("subCategoryId", state.value.subCategoryId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onVendorClick(onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit) {
        if (state.value.vendorId == 0L) {
            showMessage("Select vendor")
        } else {
            val params = getParams()
            params.addProperty("vendorId", state.value.vendorId)
            loadItemsReport(params, onSuccess)
        }

    }

    fun onCustomerClick(onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.customerId == 0L) {
            showMessage("Select customer")
        } else {
            val params = getParams()
            params.addProperty("customerId", state.value.customerId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onAccountCategoryClick(onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.accountCategoryId == 0L) {
            showMessage("Select customer category")
        } else {
            val params = getParams()
            params.addProperty("customerCategoryId", state.value.accountCategoryId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onSupplierClick(onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit) {
        if (state.value.supplierId == 0L) {
            showMessage("Select supplier")
        } else {
            val params = getParams()
            params.addProperty("supplierId", state.value.supplierId)
            loadBillWiseReport(params, onSuccess)
        }
    }

    fun onUserClick(onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit) {
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
    private fun loadBillWiseReport(
        params: JsonObject,
        onSuccess: (List<SalesBillWiseReport>, TotalReport) -> Unit
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

                    val salesBillWiseReport =
                        Gson().getListOf<SalesBillWiseReport>(result.data.get("rows").asJsonArray)
                    if (salesBillWiseReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                salesBillWiseReport = salesBillWiseReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(salesBillWiseReport, totalReport)
                    } else {
                        showMessage("Not record found")
                    }
                }
            }
        }
    }

    private fun loadItemsReport(
        params: JsonObject,
        onSuccess: (List<SalesItemsReport>, TotalReport) -> Unit
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

                    val salesItemsReport =
                        Gson().getListOf<SalesItemsReport>(result.data.get("rows").asJsonArray)
                    if (salesItemsReport.isNotEmpty()) {
                        val totalReport =
                            Gson().get<TotalReport>(result.data.get("total").asJsonObject)

                        state.update {
                            it.copy(
                                salesItemsReport = salesItemsReport,
                                totalReport = totalReport,
                            )
                        }

                        onSuccess(salesItemsReport, totalReport)
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
            addProperty("salesOn", state.value.salesOn.id.toInt())
            addProperty("salesType", state.value.salesType.id.toInt())
            addProperty("mop", state.value.mop.id.toInt())
            addProperty("type", state.value.salesRetailType.id.toInt())
            addProperty("fbrInvoice", 0)
        }
    }

    // endregion
}