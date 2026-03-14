package com.example.statspos.presentation.viewmodels.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.settings.Settings
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.UserTypes
import com.example.statspos.utils.get
import com.example.statspos.utils.getListOf
import com.example.statspos.utils.getUserType
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val api: SettingsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val defaultRate: DropdownItem = HP.defaultRate[0],
        val printLanguage: DropdownItem = HP.printLanguages[0],
        val defaultDiscount: DropdownItem = HP.defaultDiscount[0],

        // Settings
//        val shopName: String = "",
//        val contact: String = "",
//        val address: String = "",

        val saleUnderStock: Boolean = false,
        val costWarning: Boolean = false,
        val stockWarning: Boolean = false,
        val autoCreditSelect: Boolean = false,
        val showItemStock: Boolean = false,
        val loadAutoCompleteItems: Boolean = false,
        val paymentNotifications: Boolean = false,
        val editOldCreditBill: Boolean = false,
        val autoRetailChange: Boolean = false,
        val instantSearch: Boolean = false,
        val useUrdu: Boolean = false,
        val showLedgerInBill: Boolean = false,
        val showLedgerInVoucher: Boolean = false,
        val qtyChangeable: Boolean = false,
        val saleCartons: Boolean = false,
        val fourRateSystem: Boolean = false,
        val sameDateBillEdit: Boolean = false,
        val showCustomerLastRate: Boolean = false,
        val alwaysUseLastRate: Boolean = false,
        val allowManyDuplicateBillPrints: Boolean = false,
        val isPaymentNecessary: Boolean = false,
        val itemExistsInSalesWarning: Boolean = false,
        val shiftWiseSales: Boolean = false,
        val shiftWisePurchase: Boolean = false,
        val fullWindowReports: Boolean = false,

        // Passwords
        val deleteItem: String = "",
        val deleteAccount: String = "",
        val editSalesBill: String = "",
        val editPurchaseBill: String = "",
        val deleteSalesBill: String = "",
        val deletePurchaseBill: String = "",
        val deleteEntry: String = "",

        val useDeleteItem: Boolean = false,
        val useDeleteAccount: Boolean = false,
        val useEditSalesBill: Boolean = false,
        val useEditPurchaseBill: Boolean = false,
        val useDeleteSalesBill: Boolean = false,
        val useDeletePurchaseBill: Boolean = false,
        val useDeleteEntry: Boolean = false,

        // Extras
        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
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
//    fun onShopNameChange(value: String) {
//        state.update { it.copy(shopName = value) }
//    }
//
//    fun onContactChange(value: String) {
//        state.update { it.copy(contact = value) }
//    }
//
//    fun onAddressChange(value: String) {
//        state.update { it.copy(address = value) }
//    }

    fun onDefaultRateChange(value: DropdownItem) {
        state.update { it.copy(defaultRate = value) }
    }

    fun onPrintLanguageChange(value: DropdownItem) {
        state.update { it.copy(printLanguage = value) }
    }

    fun onDefaultDiscountChange(value: DropdownItem) {
        state.update { it.copy(defaultDiscount = value) }
    }

    //    Settings
    fun onSaleUnderStockChange(value: Boolean) {
        state.update { it.copy(saleUnderStock = value) }
    }

    fun onCostWarningChange(value: Boolean) {
        state.update { it.copy(costWarning = value) }
    }

    fun onStockWarningChange(value: Boolean) {
        state.update { it.copy(stockWarning = value) }
    }

    fun onAutoCreditSelectChange(value: Boolean) {
        state.update { it.copy(autoCreditSelect = value) }
    }

    fun onShowItemStockChange(value: Boolean) {
        state.update { it.copy(showItemStock = value) }
    }

    fun onLoadAutoCompleteItemsChange(value: Boolean) {
        state.update { it.copy(loadAutoCompleteItems = value) }
    }

    fun onPaymentNotificationsChange(value: Boolean) {
        state.update { it.copy(paymentNotifications = value) }
    }

    fun onEditOldCreditBillChange(value: Boolean) {
        state.update { it.copy(editOldCreditBill = value) }
    }

    fun onAutoRetailChangeChange(value: Boolean) {
        state.update { it.copy(autoRetailChange = value) }
    }

    fun onInstantSearchChange(value: Boolean) {
        state.update { it.copy(instantSearch = value) }
    }

    fun onUseUrduChange(value: Boolean) {
        state.update { it.copy(useUrdu = value) }
    }

    fun onShowLedgerInBillChange(value: Boolean) {
        state.update { it.copy(showLedgerInBill = value) }
    }

    fun onShowLedgerInVoucherChange(value: Boolean) {
        state.update { it.copy(showLedgerInVoucher = value) }
    }

    fun onQtyChangeableChange(value: Boolean) {
        state.update { it.copy(qtyChangeable = value) }
    }

    fun onSaleCartonsChange(value: Boolean) {
        state.update { it.copy(saleCartons = value) }

        if (value) {
            if (state.value.fourRateSystem)
                state.update { it.copy(fourRateSystem = false) }

            state.update { it.copy(defaultRate = HP.defaultRate[1]) }
        } else {
            state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        }
    }

    fun onFourRateSystemChange(value: Boolean) {
        state.update { it.copy(fourRateSystem = value) }

        if (value) {
            if (state.value.saleCartons)
                state.update { it.copy(saleCartons = false) }

            state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        } else {
            if (state.value.saleCartons)
                state.update { it.copy(defaultRate = HP.defaultRate[1]) }
            else
                state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        }
    }

    fun onSameDateBillEditChange(value: Boolean) {
        state.update { it.copy(sameDateBillEdit = value) }
    }

    fun onShowCustomerLastRateChange(value: Boolean) {
        state.update { it.copy(showCustomerLastRate = value) }
    }

    fun onAlwaysUseLastRateChange(value: Boolean) {
        state.update { it.copy(alwaysUseLastRate = value) }
    }

    fun onAllowManyDuplicateBillPrintsChange(value: Boolean) {
        state.update { it.copy(allowManyDuplicateBillPrints = value) }
    }

    fun onItemExistsInSalesWarningChange(value: Boolean) {
        state.update { it.copy(itemExistsInSalesWarning = value) }
    }

    fun onIsPaymentNecessaryChange(value: Boolean) {
        state.update { it.copy(isPaymentNecessary = value) }
    }

    fun onShiftWiseSalesChange(value: Boolean) {
        state.update { it.copy(shiftWiseSales = value) }
    }

    fun onShiftWisePurchaseChange(value: Boolean) {
        state.update { it.copy(shiftWisePurchase = value) }
    }

    fun onFullWindowReportsChange(value: Boolean) {
        state.update { it.copy(fullWindowReports = value) }
    }

    // Passwords
    fun onDeleteItemChange(value: String) {
        state.update { it.copy(deleteItem = value) }
    }

    fun onDeleteAccountChange(value: String) {
        state.update { it.copy(deleteAccount = value) }
    }

    fun onEditSalesBillChange(value: String) {
        state.update { it.copy(editSalesBill = value) }
    }

    fun onEditPurchaseBillChange(value: String) {
        state.update { it.copy(editPurchaseBill = value) }
    }

    fun onDeleteSalesBillChange(value: String) {
        state.update { it.copy(deleteSalesBill = value) }
    }

    fun onDeletePurchaseBillChange(value: String) {
        state.update { it.copy(deletePurchaseBill = value) }
    }

    fun onDeleteEntryChange(value: String) {
        state.update { it.copy(deleteEntry = value) }
    }

    fun onUseDeleteItemChange(value: Boolean) {
        state.update { it.copy(useDeleteItem = value) }
    }

    fun onUseDeleteAccountChange(value: Boolean) {
        state.update { it.copy(useDeleteAccount = value) }
    }

    fun onUseEditSalesBillChange(value: Boolean) {
        state.update { it.copy(useEditSalesBill = value) }
    }

    fun onUseEditPurchaseBillChange(value: Boolean) {
        state.update { it.copy(useEditPurchaseBill = value) }
    }

    fun onUseDeleteSalesBillChange(value: Boolean) {
        state.update { it.copy(useDeleteSalesBill = value) }
    }

    fun onUseDeletePurchaseBillChange(value: Boolean) {
        state.update { it.copy(useDeletePurchaseBill = value) }
    }

    fun onUseDeleteEntryChange(value: Boolean) {
        state.update { it.copy(useDeleteEntry = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val settings = getFormDataSettings()
            val passwords = getFormDataPasswords()

            when (val result = api.updateSettings(settings, passwords)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.settings =
                        Gson().get<Settings>(result.data.get("settings").asJsonObject)
                    HP.passwords =
                        Gson().get<Passwords>(result.data.get("passwords").asJsonObject)

                    onSuccess()
                }
            }
        }
    }

    fun editData() {
        viewModelScope.launch {
            setFormDataSettings(HP.settings)
            setFormDataPasswords(HP.passwords)
        }
    }
    // endregion

    // region Methods
    private fun getFormDataSettings(): Settings {
        return Settings(
//            shopName = state.value.shopName,
//            contact = state.value.contact,
//            address = state.value.address,

            isDefaultRateRetail = state.value.defaultRate.id == 0L,
            isDefaultDiscRs = state.value.defaultDiscount.id == 0L,
            printLanguage = state.value.printLanguage.id.toInt(),

            saleUnderStock = state.value.saleUnderStock,
            costWarning = state.value.costWarning,
            stockWarning = state.value.stockWarning,
            autoCreditSelect = state.value.autoCreditSelect,
            showItemStock = state.value.showItemStock,
            loadAutoCompleteItems = state.value.loadAutoCompleteItems,
            paymentNotifications = state.value.paymentNotifications,
            editOldCreditBill = state.value.editOldCreditBill,
            autoRetailChange = state.value.autoRetailChange,
            instantSearch = state.value.instantSearch,
            useUrdu = state.value.useUrdu,
            showLedgerInBill = state.value.showLedgerInBill,
            showLedgerInVoucher = state.value.showLedgerInVoucher,
            qtyChangeable = state.value.qtyChangeable,
            saleCartons = state.value.saleCartons,
            fourRateSystem = state.value.fourRateSystem,
            sameDateBillEdit = state.value.sameDateBillEdit,
            showCustomerLastRate = state.value.showCustomerLastRate,
            alwaysUseLastRate = state.value.alwaysUseLastRate,
            allowManyDuplicateBillPrints = state.value.allowManyDuplicateBillPrints,
            isPaymentNecessary = state.value.isPaymentNecessary,
            itemExistsInSalesWarning = state.value.itemExistsInSalesWarning,
            shiftWiseSales = state.value.shiftWiseSales,
            shiftWisePurchase = state.value.shiftWisePurchase,
            fullWindowReports = state.value.fullWindowReports,
        )
    }

    private fun getFormDataPasswords(): Passwords {
        return Passwords(
            deleteItem = state.value.deleteItem,
            deleteAccount = state.value.deleteAccount,
            editSalesBill = state.value.editSalesBill,
            editPurchaseBill = state.value.editPurchaseBill,
            deleteSalesBill = state.value.deleteSalesBill,
            deletePurchaseBill = state.value.deletePurchaseBill,
            deleteEntry = state.value.deleteEntry,

            useDeleteItem = state.value.useDeleteItem,
            useDeleteAccount = state.value.useDeleteAccount,
            useEditSalesBill = state.value.useEditSalesBill,
            useEditPurchaseBill = state.value.useEditPurchaseBill,
            useDeleteSalesBill = state.value.useDeleteSalesBill,
            useDeletePurchaseBill = state.value.useDeletePurchaseBill,
            useDeleteEntry = state.value.useDeleteEntry,
        )
    }

    private fun setFormDataSettings(setting: Settings) {
        state.update {
            it.copy(
//                shopName = setting.shopName!!,
//                contact = setting.contact!!,
//                address = setting.address!!,

                defaultRate = HP.defaultRate[if (setting.isDefaultRateRetail!!) 0 else 1],
                defaultDiscount = HP.defaultDiscount[if (setting.isDefaultDiscRs!!) 0 else 1],
                printLanguage = HP.printLanguages[setting.printLanguage!! - 1],

                saleUnderStock = setting.saleUnderStock!!,
                costWarning = setting.costWarning!!,
                stockWarning = setting.stockWarning!!,
                autoCreditSelect = setting.autoCreditSelect!!,
                showItemStock = setting.showItemStock!!,
                loadAutoCompleteItems = setting.loadAutoCompleteItems!!,
                paymentNotifications = setting.paymentNotifications!!,
                editOldCreditBill = setting.editOldCreditBill!!,
                autoRetailChange = setting.autoRetailChange!!,
                instantSearch = setting.instantSearch!!,
                useUrdu = setting.useUrdu!!,
                showLedgerInBill = setting.showLedgerInBill!!,
                showLedgerInVoucher = setting.showLedgerInVoucher!!,
                qtyChangeable = setting.qtyChangeable!!,
                saleCartons = setting.saleCartons!!,
                fourRateSystem = setting.fourRateSystem!!,
                sameDateBillEdit = setting.sameDateBillEdit!!,
                showCustomerLastRate = setting.showCustomerLastRate!!,
                alwaysUseLastRate = setting.alwaysUseLastRate!!,
                allowManyDuplicateBillPrints = setting.allowManyDuplicateBillPrints!!,
                isPaymentNecessary = setting.isPaymentNecessary!!,
                itemExistsInSalesWarning = setting.itemExistsInSalesWarning!!,
                shiftWiseSales = setting.shiftWiseSales!!,
                shiftWisePurchase = setting.shiftWisePurchase!!,
                fullWindowReports = setting.fullWindowReports!!,
            )
        }
    }

    private fun setFormDataPasswords(password: Passwords) {
        state.update {
            it.copy(
                deleteItem = password.deleteItem.toString(),
                deleteAccount = password.deleteAccount.toString(),
                editSalesBill = password.editSalesBill.toString(),
                editPurchaseBill = password.editPurchaseBill.toString(),
                deleteSalesBill = password.deleteSalesBill.toString(),
                deletePurchaseBill = password.deletePurchaseBill.toString(),
                deleteEntry = password.deleteEntry.toString(),

                useDeleteItem = password.useDeleteItem!!,
                useDeleteAccount = password.useDeleteAccount!!,
                useEditSalesBill = password.useEditSalesBill!!,
                useEditPurchaseBill = password.useEditPurchaseBill!!,
                useDeleteSalesBill = password.useDeleteSalesBill!!,
                useDeletePurchaseBill = password.useDeletePurchaseBill!!,
                useDeleteEntry = password.useDeleteEntry!!,
            )
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
        state.update { it.copy(isLoading = false) }
    }

    // endregion
}