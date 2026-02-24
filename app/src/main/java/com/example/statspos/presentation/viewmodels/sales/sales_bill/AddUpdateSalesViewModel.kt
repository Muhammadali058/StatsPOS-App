package com.example.statspos.presentation.viewmodels.sales.sales_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.domain.repository.sales.SalesRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
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
class AddUpdateSalesViewModel @Inject constructor(
    private val api: SalesRepository,
    private val accountsRepo: AccountsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val invoiceId: Long = 0L,
        val invoiceNo: Int = 0,

        var total: Double = 0.0,
        var cost: Double = 0.0,
        var profit: Double = 0.0,
        var isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs == true,
        val disc: String = "",
        var totalDisc: Double = 0.0,
        var payment: String = "",
        var change: String = "",
        var salesOn: DropdownItem = HP.salesOn[0],
        var salesType: DropdownItem = HP.salesType[0],
        var customerId: Long = 0L,
        val selectedCustomerName: String = "",
        val customerName: String = "",
        val balance: String = "Balance: 0 (R)",

        val mop: DropdownItem = HP.mop[0],
        var bank: DropdownItem = HP.getNoneDropdownItem(),
        var subBank: DropdownItem = HP.getNoneDropdownItem(),
        var supplier: DropdownItem = HP.getNoneDropdownItem(),

        var isMopCashBank: Boolean = true,
        var isRetail: Boolean = HP.settings.isDefaultRateRetail == true,
        val date: LocalDate = LocalDate.now(),
        val dueDate: LocalDate = LocalDate.now().plusDays(7),
        var isEstimatedBill: Boolean = false,
        val remarks: String = "",

        // Extras
        var totalBill: Double = 0.0,
        var totalCost: Double = 0.0,
        var localTime: String = "",
        val paymentEnabled: Boolean = false,

        val isPendingBill: Boolean = false,
        val isPostedBill: Boolean = false,
        val salesBill: SalesBills? = null,

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isPosting: Boolean = false,
        val message: String? = null,
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
    fun onCustomerIdChange(value: Long) {
        state.update { it.copy(customerId = value) }
        getBalance(value)
    }

    fun onSelectedCustomerNameChange(value: String) {
        state.update { it.copy(selectedCustomerName = value) }
    }

    fun onCustomerNameChange(value: String) {
        state.update { it.copy(customerName = value) }
    }

    fun onIsRetailChange(value: Boolean) {
        state.update { it.copy(isRetail = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
        updateTotal(state.value.totalBill, state.value.totalCost)
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
        updateTotal(state.value.totalBill, state.value.totalCost)
    }

    fun onSalesOnChange(value: DropdownItem) {
        state.update { it.copy(salesOn = value) }
    }

    fun onSalesTypeChange(value: DropdownItem) {
        state.update { it.copy(salesType = value) }
    }

    fun onDateChange(value: LocalDate) {
        state.update { it.copy(date = value) }
    }

    fun onDueDateChange(value: LocalDate) {
        state.update { it.copy(dueDate = value) }
    }

    fun onBankSelected(value: DropdownItem) {
        state.update { it.copy(bank = value) }
    }

    fun onSubBankSelected(value: DropdownItem) {
        state.update { it.copy(subBank = value) }
    }

    fun onSupplierSelected(value: DropdownItem) {
        state.update { it.copy(supplier = value) }
    }

    fun onMOPChange(value: DropdownItem) {
        state.update {
            it.copy(
                mop = value,
                isMopCashBank = value.id == 1L,
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
            )
        }
    }

    fun onPaymentChange(value: String) {
        state.update { it.copy(payment = value) }
        updatePayment()
    }

    fun onChangeChange(value: String) {
        state.update { it.copy(change = value) }
    }

    fun onRemarksChange(value: String) {
        state.update { it.copy(remarks = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun postBill(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isPosting)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isPosting = true) }

            val sale = getFormData()

            val result = if (state.value.isPostedBill) {
                sale.salesId = state.value.invoiceId
                api.updateSales(sale)
            } else {
                sale.salesId = state.value.invoiceId
                api.insertSales(sale)
            }

            state.update { it.copy(isPosting = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
//                    clearTextboxes()
                    onSuccess()
                }
            }
        }
    }

    fun tempClose(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isPosting)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val sale = getFormData()

            val result = if (state.value.isPendingBill) {
                sale.salesId = state.value.invoiceId
                sale.isPendingBill = state.value.isPendingBill
                api.tempClose(sale)
            } else {
                sale.salesId = state.value.invoiceId
                api.tempClose(sale)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isPosting)
                return@launch

            beforeRequest()

            when (val result = api.deleteSales(id, state.value.isPostedBill)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    onSuccess()
                }
            }
        }
    }

    fun editData(id: Long) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.getSales(id, state.value.isPostedBill)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val sales = Gson().get<Sales>(result.data.asJsonObject)
                    setFormData(sales)
                }
            }
        }
    }

    private fun getBalance(accountId: Long) {
        viewModelScope.launch {
            if (accountId != 0L) {
                when (val result = accountsRepo.getBalance(accountId)) {
                    is Resource.Error -> showError(result.error)
                    is Resource.Information -> showMessage(result.message)
                    is Resource.Success -> {
                        val balance = HP.getBalanceWithLabel(result.data.get("balance").asDouble)
                        val account = Gson().get<Accounts>(result.data.get("data").asJsonObject)

                        state.update {
                            it.copy(
                                balance = balance,
                                customerName = account.accountName.toString(),
                            )
                        }

                        // Auto Credit Select
                        if (HP.settings.autoCreditSelect!! && HP.userRights.creditBill!!) {
                            state.update { it.copy(salesOn = HP.salesOn[1]) }
                        } else {
                            if (account.isCredit!! && HP.userRights.creditBill!!) {
                                state.update { it.copy(salesOn = HP.salesOn[1]) }
                            }
                        }

                        // Due Days
                        if (account.dueDays!! > 0) {
                            state.update {
                                it.copy(
                                    dueDate = LocalDate.now().plusDays(account.dueDays.toLong())
                                )
                            }
                        }

                        // Select Supplier
                        if (account.supplierId!! != 0L) {
                            state.update {
                                it.copy(
                                    supplier = HP.getDropdownById(
                                        account.supplierId!!,
                                        HP.suppliers
                                    )
                                )
                            }
                        }

                        // Select Retail
                        if (HP.settings.fourRateSystem == false) {
                            if (HP.settings.autoRetailChange!!) {
                                state.update { it.copy(isRetail = account.isRetail!!) }
                            }
                        }
                    }
                }
            } else {
                state.update {
                    it.copy(
                        balance = "Balance: 0 (R)",
                        customerName = "",
                        salesOn = HP.salesOn[0],
                        dueDate = LocalDate.now().plusDays(7)
                    )
                }
            }
        }
    }

    fun changeBillType(isRetail: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isPosting)
                return@launch

            beforeRequest()

            val body = JsonObject().apply {
                addProperty("isRetail", isRetail)
                addProperty("isPostedBill", state.value.isPostedBill)
                addProperty("salesId", state.value.invoiceId)
            }
            when (val result = api.changeBillType(body)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    onSuccess()
                }
            }
        }
    }
    // endregion

    // region Methods
    fun getFormData(): Sales {
        val sale = Sales(
            total = state.value.total,
            cost = state.value.cost,
            profit = state.value.profit,
            isDiscRsPer = state.value.isDiscRsPer,
            disc = HP.getDoubleValue(state.value.disc),
            totalDisc = state.value.totalDisc,

            payment = HP.getIntValue(state.value.payment),
            change = HP.getIntValue(state.value.change),
            salesOn = state.value.salesOn.id.toInt(),
            salesType = state.value.salesType.id.toInt(),
            isMopCashBank = state.value.mop.id == 1L,

            customerId = state.value.customerId,
            customerName = state.value.customerName,
            bankId = state.value.bank.id,
            subBankId = state.value.subBank.id,
            supplierId = state.value.supplier.id,

            isRetail = state.value.isRetail,
            date = HP.getZonedDate(state.value.date),
            dueDate = HP.getZonedDate(state.value.dueDate),
            isEstimatedBill = state.value.isEstimatedBill,
            remarks = state.value.remarks,
        )

        if (state.value.isPostedBill) {
            val localTime = HP.toLocalTime(state.value.localTime)
            sale.date = HP.getZonedDateWithTime(state.value.date, localTime)
        } else {
            sale.currentShiftId = HP.user.currentShiftId
        }

        return sale
    }

    private fun setFormData(sale: Sales) {
        state.update {
            it.copy(
                invoiceNo = sale.invoiceNo!!,

                customerId = sale.customerId!!,
                selectedCustomerName = HP.getDropdownNameById(sale.customerId!!, HP.customers),
                customerName = sale.customerName.toString(),

                payment = sale.payment.toString(),
                change = sale.change.toString(),
                mop = if (sale.isMopCashBank!!) HP.mop[0] else HP.mop[1],
                bank = HP.getDropdownById(sale.bankId!!, HP.banks),
                subBank = HP.getDropdownById(sale.subBankId!!, HP.subBanks),
                supplier = HP.getDropdownById(sale.supplierId!!, HP.suppliers),
                remarks = sale.remarks.toString(),

                salesOn = HP.getDropdownById(sale.salesOn!!.toLong(), HP.salesOn),
                salesType = HP.getDropdownById(sale.salesType!!.toLong(), HP.salesType),
                isRetail = sale.isRetail!!,
                isEstimatedBill = sale.isEstimatedBill!!,

                date = if (state.value.isPostedBill) HP.toLocalDate(sale.date!!) else LocalDate.now(),
                dueDate = HP.toLocalDate(sale.dueDate!!),

                paymentEnabled = !(state.value.isPostedBill && sale.salesOn!! == 2),

                // Extras
                localTime = sale.date.toString()
            )
        }

        if (sale.customerId!! != 0L)
            getBalance(sale.customerId!!)
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                customerId = 0L,
                selectedCustomerName = "",
                customerName = "",
                payment = "",
                change = "",
                remarks = "",

                salesOn = HP.salesOn[0],
                salesType = HP.salesType[0],
                mop = HP.mop[0],
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
                supplier = HP.getNoneDropdownItem(),

                balance = "Balance: 0 (R)",
                isDiscRsPer = HP.settings.isDefaultDiscRs!!,
                disc = "",
                totalDisc = 0.0,
                date = LocalDate.now(),
                dueDate = LocalDate.now(),
                isRetail = HP.settings.isDefaultRateRetail!!,
                isEstimatedBill = false,

                // Extras
                isPendingBill = false,
                isPostedBill = false,
                invoiceId = 0L,

                localTime = "",
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.invoiceId == 0L) {
            showMessage("Failed to load invoice number")
            return false
        }

        if (state.value.salesOn.id == 2L) {
            if (state.value.customerId == 0L) {
                showMessage("Please select customer")
                return false
            }
        }

        if (state.value.mop.id == 2L) {
            if (state.value.bank.id == 0L) {
                showMessage("Please select bank")
                return false
            } else {
                if (state.value.subBank.id == 0L) {
                    showMessage("Please select bank account")
                    return false
                }
            }
        }

        if (HP.settings.isPaymentNecessary == true && state.value.salesOn.id == 1L) {
            val payment = HP.getIntValue(state.value.payment)
            val total = state.value.total

            if (payment == 0) {
                showMessage("Please enter payment")
                return false
            }

            if (payment > 0 && payment < total) {
                showMessage("Less payment entered")
                return false
            }
        }

        return true
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

    fun updateInitialState(
        invoiceId: Long,
        isPendingBill: Boolean,
        isPostedBill: Boolean,
        salesBill: SalesBills?
    ) {
        state.update {
            it.copy(
                invoiceId = invoiceId,
                isPendingBill = isPendingBill,
                isPostedBill = isPostedBill,
                salesBill = salesBill,
            )
        }

        salesBill?.run {
            val totalDisc = if (grossTotal!! > 0.0) {
                if (isDiscRsPer!!)
                    disc!!
                else {
                    val totalDisc = (disc!! / 100) * grossTotal!!
                    totalDisc
                }
            } else
                0.0

            val total = grossTotal!! - totalDisc
            val profit = total - totalCost!!
            state.update {
                it.copy(
                    totalBill = grossTotal!!,
                    totalCost = totalCost!!,

                    total = total,
                    cost = totalCost!!,
                    profit = profit,

                    isDiscRsPer = isDiscRsPer!!,
                    disc = disc.toString(),
                    totalDisc = totalDisc,

//                    localTime = localDate.toString()
                )
            }
        }
    }

    fun updateTotal(totalBill: Double, totalCost: Double) {
        val totalDisc = getTotalDisc(totalBill)
        val total = totalBill - totalDisc
        val profit = total - totalCost

        state.update {
            it.copy(
                totalBill = totalBill,
                totalCost = totalCost,

                total = total,
                cost = totalCost,
                profit = profit,

                totalDisc = totalDisc,
            )
        }

        updatePayment()
    }

    fun updatePayment() {
        val payment = HP.getIntValue(state.value.payment)
        val change = if (payment != 0) {
            (payment - state.value.total.toInt())
        } else
            0

        state.update { it.copy(change = change.toString()) }
    }

    private fun getTotalDisc(totalBill: Double): Double {
        if (!state.value.isPendingBill && !state.value.isPostedBill) {
            return 0.0
        } else {
            val totalDisc = if (totalBill > 0.0) {
                if (state.value.isDiscRsPer)
                    HP.getDoubleValue(state.value.disc)
                else {
                    val disc = HP.getDoubleValue(state.value.disc)
                    val totalDisc = (disc / 100) * totalBill
                    totalDisc
                }
            } else
                0.0

            return totalDisc
        }
    }
    // endregion
}