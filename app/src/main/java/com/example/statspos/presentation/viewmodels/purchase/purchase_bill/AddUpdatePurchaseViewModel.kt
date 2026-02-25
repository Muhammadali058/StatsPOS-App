package com.example.statspos.presentation.viewmodels.purchase.purchase_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.domain.repository.purchase.PurchaseRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddUpdatePurchaseViewModel @Inject constructor(
    private val api: PurchaseRepository,
    private val accountsRepo: AccountsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val invoiceId: Long = 0L,
        val invoiceNo: Int = 0,
        var updateVendor: Boolean = false,

        var total: Double = 0.0,
        var isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs == true,
        val disc: String = "",
        var totalDisc: Double = 0.0,

        var purchaseOn: DropdownItem = HP.purchaseOn[0],
        var purchaseType: DropdownItem = HP.purchaseType[0],
        var vendorId: Long = 0L,
        val vendorName: String = "",
        val balance: String = "Balance: 0 (P)",

        val expense: String = "",
        val refInvoiceNo: String = "",
        val remarks: String = "",
        val vendorDiscount: String = "",

        val mop: DropdownItem = HP.mop[0],
        var bank: DropdownItem = HP.getNoneDropdownItem(),
        var subBank: DropdownItem = HP.getNoneDropdownItem(),
        var supplier: DropdownItem = HP.getNoneDropdownItem(),
        var warehouse: DropdownItem = HP.getNoneDropdownItem(),

        var isMopCashBank: Boolean = true,
        var isRetail: Boolean = HP.settings.isDefaultRateRetail == true,
        val date: LocalDate = LocalDate.now(),

        // Extras
        var totalBill: Double = 0.0,

        val isPendingBill: Boolean = false,
        val isPostedBill: Boolean = false,
        val purchaseBill: PurchaseBills? = null,

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
    fun onUpdateVendorChange(value: Boolean) {
        state.update { it.copy(updateVendor = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
        getBalance(value)
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
        updateTotal(state.value.totalBill)
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
        updateTotal(state.value.totalBill)
    }

    fun onPurchaseOnChange(value: DropdownItem) {
        state.update { it.copy(purchaseOn = value) }
    }

    fun onPurchaseTypeChange(value: DropdownItem) {
        state.update { it.copy(purchaseType = value) }
    }

    fun onDateChange(value: LocalDate) {
        state.update { it.copy(date = value) }
    }

    fun onExpenseChange(value: String) {
        state.update { it.copy(expense = value) }
    }

    fun onRefInvoiceNoChange(value: String) {
        state.update { it.copy(refInvoiceNo = value) }
    }

    fun onRemarksChange(value: String) {
        state.update { it.copy(remarks = value) }
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

    fun onWarehouseSelected(value: DropdownItem) {
        state.update { it.copy(warehouse = value) }
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

            val purchase = getFormData()

            val result = if (state.value.isPostedBill) {
                purchase.purchaseId = state.value.invoiceId
                purchase.updateVendor = state.value.updateVendor
                api.updatePurchase(purchase)
            } else {
                purchase.purchaseId = state.value.invoiceId
                purchase.updateVendor = state.value.updateVendor
                api.insertPurchase(purchase)
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

            val purchase = getFormData()
            purchase.purchaseId = state.value.invoiceId
            purchase.isPendingBill = state.value.isPendingBill

            val result = api.tempClose(purchase)

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

            when (val result = api.deletePurchase(id, state.value.isPostedBill)) {
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

            when (val result = api.getPurchase(id, state.value.isPostedBill)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val purchase = Gson().get<Purchase>(result.data.asJsonObject)
                    setFormData(purchase)
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
                                vendorName = account.accountName.toString(),
                                remarks = account.remarks.toString(),
                            )
                        }

                        // Auto Credit Select
                        if (account.isCredit!!) {
                            state.update { it.copy(purchaseOn = HP.purchaseOn[1]) }
                        }

                        if(HP.getDoubleValue(account.disc.toString()) > 0.0){
                            if(account.isDiscRsPer!!){
                                state.update { it.copy(vendorDiscount = "Rs.${account.disc}") }
                            }else{
                                state.update { it.copy(vendorDiscount = "${account.disc}%") }
                            }
                        }
                    }
                }
            } else {
                state.update {
                    it.copy(
                        balance = "Balance: 0 (P)",
                        vendorName = "",
                        purchaseOn = HP.purchaseOn[0],
                    )
                }
            }
        }
    }

    // endregion

    // region Methods
    fun getFormData(): Purchase {
        val purchase = Purchase(
            total = state.value.total,
            isDiscRsPer = state.value.isDiscRsPer,
            disc = HP.getDoubleValue(state.value.disc),
            totalDisc = state.value.totalDisc,

            expenses = HP.getDoubleValue(state.value.expense),
            refInvoiceNo = state.value.refInvoiceNo,

            date = HP.getZonedDate(state.value.date),
            purchaseOn = state.value.purchaseOn.id.toInt(),
            purchaseType = state.value.purchaseType.id.toInt(),
            isMopCashBank = state.value.mop.id == 1L,

            vendorId = state.value.vendorId,
            bankId = state.value.bank.id,
            subBankId = state.value.subBank.id,
            supplierId = state.value.supplier.id,
            warehouseId = state.value.warehouse.id,
        )

        if (!state.value.isPostedBill) {
            purchase.currentShiftId = HP.user.currentShiftId
        }

        return purchase
    }

    private fun setFormData(purchase: Purchase) {
        state.update {
            it.copy(
                invoiceNo = purchase.invoiceNo!!,

                vendorId = purchase.vendorId!!,
                vendorName = HP.getDropdownNameById(purchase.vendorId!!, HP.vendors),

                expense = purchase.expenses.toString(),
                refInvoiceNo = purchase.refInvoiceNo.toString(),

                mop = if (purchase.isMopCashBank!!) HP.mop[0] else HP.mop[1],
                bank = HP.getDropdownById(purchase.bankId!!, HP.banks),
                subBank = HP.getDropdownById(purchase.subBankId!!, HP.subBanks),
                supplier = HP.getDropdownById(purchase.supplierId!!, HP.suppliers),
                warehouse = HP.getDropdownById(purchase.warehouseId!!, HP.warehouses),

                purchaseOn = HP.getDropdownById(purchase.purchaseOn!!.toLong(), HP.purchaseOn),
                purchaseType = HP.getDropdownById(
                    purchase.purchaseType!!.toLong(),
                    HP.purchaseType
                ),

                date = if (state.value.isPostedBill) HP.toLocalDate(purchase.date!!) else LocalDate.now(),
            )
        }

        if (purchase.vendorId!! != 0L)
            getBalance(purchase.vendorId!!)
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                vendorId = 0L,
                vendorName = "",

                expense = "",
                refInvoiceNo = "",
                remarks = "",

                purchaseOn = HP.purchaseOn[0],
                purchaseType = HP.purchaseType[0],
                mop = HP.mop[0],
                bank = HP.getNoneDropdownItem(),
                subBank = HP.getNoneDropdownItem(),
                supplier = HP.getNoneDropdownItem(),
                warehouse = HP.getNoneDropdownItem(),

                balance = "Balance: 0 (P)",
                isDiscRsPer = HP.settings.isDefaultDiscRs!!,
                disc = "",
                totalDisc = 0.0,
                date = LocalDate.now(),

                // Extras
                isPendingBill = false,
                isPostedBill = false,
                invoiceId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.invoiceId == 0L) {
            showMessage("Failed to load invoice number")
            return false
        }

        if (state.value.purchaseOn.id == 2L) {
            if (state.value.vendorId == 0L) {
                showMessage("Please select vendor")
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
        purchaseBill: PurchaseBills?
    ) {
        state.update {
            it.copy(
                invoiceId = invoiceId,
                isPendingBill = isPendingBill,
                isPostedBill = isPostedBill,
                purchaseBill = purchaseBill,
            )
        }

        purchaseBill?.run {
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

            state.update {
                it.copy(
                    totalBill = grossTotal!!,
                    total = total,

                    isDiscRsPer = isDiscRsPer!!,
                    disc = disc.toString(),
                    totalDisc = totalDisc,
                )
            }
        }
    }

    fun updateTotal(totalBill: Double) {
        val totalDisc = getTotalDisc(totalBill)
        val total = totalBill - totalDisc

        state.update {
            it.copy(
                totalBill = totalBill,
                total = total,

                totalDisc = totalDisc,
            )
        }

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