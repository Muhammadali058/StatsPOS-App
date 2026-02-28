package com.example.statspos.presentation.viewmodels.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.accounts.Banks
import com.example.statspos.domain.models.accounts.Expenses
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.repository.accounts.BanksRepository
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.reports.SalesReportsRepository
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

        val list: List<Banks> = emptyList(),
        val totalBanks: Int = 0,

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

    // endregion

    // region Network calls
    fun loadData() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("text", "")
            }

            when (val result = api.billWiseReport(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val resultTotal =
                        result.data.get("total").asJsonObject.get("totalBanks").asInt
                    val resultList =
                        Gson().getListOf<Banks>(result.data.get("rows").asJsonArray)
                    state.update {
                        it.copy(
                            list = resultList,
                            totalBanks = resultTotal,
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
    // endregion
}