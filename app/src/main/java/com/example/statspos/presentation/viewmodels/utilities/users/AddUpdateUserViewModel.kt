package com.example.statspos.presentation.viewmodels.utilities.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.main.MainRepository
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
class AddUpdateUserViewModel @Inject constructor(
    private val api: UsersRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val userType: DropdownItem = HP.userTypes[0],
        val shift: DropdownItem = HP.shifts[0],

        val imageUrl: String = "",

        // User Rights
        val items: Boolean = true,
        val sales: Boolean = true,
        val purchase: Boolean = true,
        val categories: Boolean = true,
        val warehouse: Boolean = true,
        // Accounts
        val accounts: Boolean = true,
        val customers: Boolean = true,
        val vendors: Boolean = true,
        val suppliers: Boolean = true,
        val expenses: Boolean = true,
        val banks: Boolean = true,
        // Utilities
        val utilities: Boolean = true,
        val users: Boolean = true,
        val settings: Boolean = true,
        val barcodeLabels: Boolean = true,
        val employees: Boolean = true,
        // Reports
        val reports: Boolean = true,
        val salesReports: Boolean = true,
        val purchaseReports: Boolean = true,
        val profitReports: Boolean = true,
        val stockReports: Boolean = true,
        val accountReports: Boolean = true,
        val itemsReports: Boolean = true,
        val auditReports: Boolean = true,
        // Others
        val dateWiseEntry: Boolean = true,
        val dateWisePurchase: Boolean = true,
        val printDuplicates: Boolean = true,
        val deleteAnything: Boolean = true,
        val entry: Boolean = true,
        // POS
        val changeRates: Boolean = true,
        val seeMargin: Boolean = true,
        val salesReturn: Boolean = true,
        val creditBill: Boolean = true,
        val editSalesBill: Boolean = true,
        val editCreditBill: Boolean = true,
        val dateWiseSales: Boolean = true,
        val payBill: Boolean = true,
        val discount: Boolean = true,
        val seeCost: Boolean = true,
        val searchItems: Boolean = true,
        val fbrInvoice: Boolean = true,

        // Extras
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isUploadingImage: Boolean = false,
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
    fun onUsernameChange(value: String) {
        state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        state.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        state.update { it.copy(confirmPassword = value) }
    }

    fun onUserTypeSelected(value: DropdownItem) {
        state.update { it.copy(userType = value) }
        changeUserRights()
    }

    fun onShiftSelected(value: DropdownItem) {
        state.update { it.copy(shift = value) }
    }

    //    User Rights
    fun onItemsChange(value: Boolean) {
        state.update { it.copy(items = value) }
    }

    fun onCategoriesChange(value: Boolean) {
        state.update { it.copy(categories = value) }
    }

    fun onPurchaseChange(value: Boolean) {
        state.update { it.copy(purchase = value) }
    }

    fun onSalesChange(value: Boolean) {
        state.update { it.copy(sales = value) }
    }

    fun onWarehouseChange(value: Boolean) {
        state.update { it.copy(warehouse = value) }
    }

    //    Accounts
    fun onAccountsChange(value: Boolean) {
        state.update { it.copy(accounts = value) }
    }
    fun onCustomersChange(value: Boolean) {
        state.update { it.copy(customers = value) }
        onAccountsChange(checkAccounts())
    }

    fun onVendorsChange(value: Boolean) {
        state.update { it.copy(vendors = value) }
        onAccountsChange(checkAccounts())
    }

    fun onSuppliersChange(value: Boolean) {
        state.update { it.copy(suppliers = value) }
        onAccountsChange(checkAccounts())
    }

    fun onBanksChange(value: Boolean) {
        state.update { it.copy(banks = value) }
        onAccountsChange(checkAccounts())
    }

    fun onExpensesChange(value: Boolean) {
        state.update { it.copy(expenses = value) }
        onAccountsChange(checkAccounts())
    }

    //    Utilities
    fun onUtilitiesChange(value: Boolean) {
        state.update { it.copy(utilities = value) }
    }
    fun onUsersChange(value: Boolean) {
        state.update { it.copy(users = value) }
        onUtilitiesChange(checkUtilities())
    }

    fun onSettingsChange(value: Boolean) {
        state.update { it.copy(settings = value) }
        onUtilitiesChange(checkUtilities())
    }

    fun onBarcodeLabelsChange(value: Boolean) {
        state.update { it.copy(barcodeLabels = value) }
        onUtilitiesChange(checkUtilities())
    }

    fun onEmployeesChange(value: Boolean) {
        state.update { it.copy(employees = value) }
        onUtilitiesChange(checkUtilities())
    }

    //    Others
    fun onDateWiseEntryChange(value: Boolean) {
        state.update { it.copy(dateWiseEntry = value) }
    }

    fun onDateWisePurchaseChange(value: Boolean) {
        state.update { it.copy(dateWisePurchase = value) }
    }

    fun onPrintDuplicatesChange(value: Boolean) {
        state.update { it.copy(printDuplicates = value) }
    }

    fun onDeleteAnythingChange(value: Boolean) {
        state.update { it.copy(deleteAnything = value) }
    }

    fun onEntryChange(value: Boolean) {
        state.update { it.copy(entry = value) }
    }

    //    Reports
    fun onReportsChange(value: Boolean) {
        state.update { it.copy(reports = value) }
    }
    fun onSalesReportsChange(value: Boolean) {
        state.update { it.copy(salesReports = value) }
        onReportsChange(checkReports())
    }

    fun onPurchaseReportsChange(value: Boolean) {
        state.update { it.copy(purchaseReports = value) }
        onReportsChange(checkReports())
    }

    fun onProfitReportsChange(value: Boolean) {
        state.update { it.copy(profitReports = value) }
        onReportsChange(checkReports())
    }

    fun onStockReportsChange(value: Boolean) {
        state.update { it.copy(stockReports = value) }
        onReportsChange(checkReports())
    }

    fun onAccountReportsChange(value: Boolean) {
        state.update { it.copy(accountReports = value) }
        onReportsChange(checkReports())
    }

    fun onItemsReportsChange(value: Boolean) {
        state.update { it.copy(itemsReports = value) }
        onReportsChange(checkReports())
    }

    fun onAuditReportsChange(value: Boolean) {
        state.update { it.copy(auditReports = value) }
        onReportsChange(checkReports())
    }

    //    POS
    fun onChangeRatesChange(value: Boolean) {
        state.update { it.copy(changeRates = value) }
    }

    fun onSeeMarginChange(value: Boolean) {
        state.update { it.copy(seeMargin = value) }
    }

    fun onSalesReturnChange(value: Boolean) {
        state.update { it.copy(salesReturn = value) }
    }

    fun onCreditBillChange(value: Boolean) {
        state.update { it.copy(creditBill = value) }
    }

    fun onEditSalesBillChange(value: Boolean) {
        state.update { it.copy(editSalesBill = value) }
    }

    fun onEditCreditBillChange(value: Boolean) {
        state.update { it.copy(editCreditBill = value) }
    }

    fun onDateWiseSalesChange(value: Boolean) {
        state.update { it.copy(dateWiseSales = value) }
    }

    fun onPayBillChange(value: Boolean) {
        state.update { it.copy(payBill = value) }
    }

    fun onDiscountChange(value: Boolean) {
        state.update { it.copy(discount = value) }
    }

    fun onSeeCostChange(value: Boolean) {
        state.update { it.copy(seeCost = value) }
    }

    fun onSearchItemsChange(value: Boolean) {
        state.update { it.copy(searchItems = value) }
    }

    fun onFbrInvoiceChange(value: Boolean) {
        state.update { it.copy(fbrInvoice = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun insertOrUpdateData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val user = getFormDataUsers()
            val userRights = getFormDataUserRights()

            val result = if (state.value.isUpdate) {
                api.updateUser(state.value.updateId, user, userRights)
            } else {
                api.insertUser(user, userRights)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    HP.users =
                        Gson().getListOf<DropdownItem>(result.data.get("users").asJsonArray)
//                    clearTextboxes()
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

            if (state.value.isUploadingImage)
                return@launch

            beforeRequest()

            when (val result = api.deleteUser(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.users =
                        Gson().getListOf<DropdownItem>(result.data.get("users").asJsonArray)
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

            when (val result = api.getUser(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val user = Gson().get<Users>(result.data.get("user").asJsonObject)
                    val userRights =
                        Gson().get<UserRights>(result.data.get("userRights").asJsonObject)

                    setFormDataUser(user)
                    setFormDataUserRights(userRights)
                }
            }
        }
    }

    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            state.update { it.copy(isUploadingImage = true) }

            when (val result = mainRepo.uploadImage(multipart)) {
                is Resource.Error -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showError(result.error)
                }

                is Resource.Information -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showMessage(result.message)
                }

                is Resource.Success -> {
                    val fileName = result.data.asJsonObject.get("fileName").asString
                    state.update {
                        it.copy(
                            isUploadingImage = false,
                            imageUrl = fileName,
                        )
                    }
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormDataUsers(): Users {
        return Users(
            username = state.value.username,
            password = state.value.password,
            userType = state.value.userType.id.toInt(),
            shift = state.value.shift.id.toInt(),

            imageUrl = state.value.imageUrl,
        )
    }

    private fun getFormDataUserRights(): UserRights {
        return UserRights(
            items = state.value.items,
            categories = state.value.categories,
            purchase = state.value.purchase,
            sales = state.value.sales,
            warehouse = state.value.warehouse,
//            Accounts
            accounts = state.value.accounts,
            customers = state.value.customers,
            vendors = state.value.vendors,
            suppliers = state.value.suppliers,
            banks = state.value.banks,
            expenses = state.value.expenses,
//            Utilities
            utilities = state.value.utilities,
            users = state.value.users,
            settings = state.value.settings,
            barcodeLabels = state.value.barcodeLabels,
            employees = state.value.employees,
//            Reports
            reports = state.value.reports,
            salesReport = state.value.salesReports,
            purchaseReport = state.value.purchaseReports,
            profitReport = state.value.profitReports,
            stockReport = state.value.stockReports,
            accountsReport = state.value.accountReports,
            itemsReport = state.value.itemsReports,
            auditReport = state.value.auditReports,
//            Others
            dateWiseEntry = state.value.dateWiseEntry,
            dateWisePurchase = state.value.dateWisePurchase,
            printDuplicates = state.value.printDuplicates,
            deleteAnything = state.value.deleteAnything,
            entry = state.value.entry,
//            POS
            changeRates = state.value.changeRates,
            seeMargin = state.value.seeMargin,
            salesReturn = state.value.salesReturn,
            creditBill = state.value.creditBill,
            editSaleBill = state.value.editSalesBill,
            editCreditBill = state.value.editCreditBill,
            dateWiseSales = state.value.dateWiseSales,
            payBill = state.value.payBill,
            discount = state.value.discount,
            seeCost = state.value.seeCost,
            searchItems = state.value.searchItems,
            fbrInvoice = state.value.fbrInvoice,
        )
    }

    private fun setFormDataUser(user: Users) {
        state.update {
            it.copy(
                username = user.username.toString(),
                password = user.password.toString(),
                confirmPassword = user.password.toString(),
                userType = HP.getDropdownById(user.userType?.toLong() ?: 0, HP.userTypes)!!,
                shift = HP.getDropdownById(user.shift?.toLong() ?: 0, HP.shifts)!!,

                imageUrl = user.imageUrl!!,
            )
        }
    }

    private fun setFormDataUserRights(userRights: UserRights) {
        state.update {
            it.copy(
                items = userRights.items!!,
                categories = userRights.categories!!,
                purchase = userRights.purchase!!,
                sales = userRights.sales!!,
                warehouse = userRights.warehouse!!,
//                Accounts
                accounts = userRights.accounts!!,
                customers = userRights.customers!!,
                vendors = userRights.vendors!!,
                suppliers = userRights.suppliers!!,
                expenses = userRights.expenses!!,
                banks = userRights.banks!!,
//                Utilities
                utilities = userRights.utilities!!,
                users = userRights.users!!,
                settings = userRights.settings!!,
                barcodeLabels = userRights.barcodeLabels!!,
                employees = userRights.employees!!,
//                Reports
                reports = userRights.reports!!,
                salesReports = userRights.salesReport!!,
                purchaseReports = userRights.purchaseReport!!,
                profitReports = userRights.profitReport!!,
                stockReports = userRights.stockReport!!,
                accountReports = userRights.accountsReport!!,
                itemsReports = userRights.itemsReport!!,
                auditReports = userRights.auditReport!!,
//                Others
                dateWiseEntry = userRights.dateWiseEntry!!,
                dateWisePurchase = userRights.dateWisePurchase!!,
                printDuplicates = userRights.printDuplicates!!,
                deleteAnything = userRights.deleteAnything!!,
                entry = userRights.entry!!,
//                POS
                changeRates = userRights.changeRates!!,
                seeMargin = userRights.seeMargin!!,
                salesReturn = userRights.salesReturn!!,
                creditBill = userRights.creditBill!!,
                editSalesBill = userRights.editSaleBill!!,
                editCreditBill = userRights.editCreditBill!!,
                dateWiseSales = userRights.dateWiseSales!!,
                payBill = userRights.payBill!!,
                discount = userRights.discount!!,
                seeCost = userRights.seeCost!!,
                searchItems = userRights.searchItems!!,
                fbrInvoice = userRights.fbrInvoice!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                username = "",
                password = "",
                confirmPassword = "",
                userType = HP.userTypes[0],
                shift = HP.shifts[0],

                imageUrl = "",

                // Extras
                isUpdate = false,
                updateId = 0L,
            )
        }

        checkOrUnCheckAllRights(true)
    }

    private fun isValid(): Boolean {
        if (state.value.username.isEmpty()) {
            showMessage("Please enter username")
            return false
        }

        if (state.value.password.isEmpty()) {
            showMessage("Please enter password")
            return false
        }

        if (state.value.confirmPassword.isEmpty()) {
            showMessage("Re-enter password")
            return false
        }

        if (state.value.password != state.value.confirmPassword) {
            showMessage("Password didn't match")
            return false
        }

        return true
    }

    private fun changeUserRights() {
        when (state.value.userType.id) {
            getUserType(UserTypes.ADMINISTRATOR) -> {
                checkOrUnCheckAllRights(true)
            }

            getUserType(UserTypes.POS_USER) -> {
                checkOrUnCheckAllRights(false)

                state.update {
                    it.copy(
                        sales = true,
                        printDuplicates = true,
                    )
                }
            }

            getUserType(UserTypes.INVENTORY_MANAGER) -> {
                checkOrUnCheckAllRights(false)

                state.update {
                    it.copy(
                        items = true,
                        purchase = true,
                        categories = true,
                        warehouse = true,
                        barcodeLabels = true,
                        accounts = true,
                        vendors = true,
                    )
                }
            }
        }
    }

    private fun checkOrUnCheckAllRights(value: Boolean) {
        state.update {
            it.copy(
                items = value,
                categories = value,
                purchase = value,
                sales = value,
                warehouse = value,

//                Accounts
                accounts = value,
                customers = value,
                vendors = value,
                suppliers = value,
                banks = value,
                expenses = value,

//                Utilities
                utilities = value,
                users = value,
                settings = value,
                barcodeLabels = value,
                employees = value,

//                Reports
                reports = value,
                salesReports = value,
                purchaseReports = value,
                profitReports = value,
                stockReports = value,
                accountReports = value,
                itemsReports = value,
                auditReports = value,

//                Others
                dateWiseEntry = value,
                dateWisePurchase = value,
                printDuplicates = value,
                deleteAnything = value,
                entry = value,

//                POS
                changeRates = value,
                seeMargin = value,
                salesReturn = value,
                creditBill = value,
                editSalesBill = value,
                editCreditBill = value,
                dateWiseSales = value,
                payBill = value,
                discount = value,
                seeCost = value,
                searchItems = value,
                fbrInvoice = value,
            )
        }
    }

    private fun checkAccounts(): Boolean{
        if(state.value.customers)
            return true
        if(state.value.vendors)
            return true
        if(state.value.suppliers)
            return true
        if(state.value.banks)
            return true
        if(state.value.expenses)
            return true

        return false
    }

    private fun checkUtilities(): Boolean{
        if(state.value.users)
            return true
        if(state.value.settings)
            return true
        if(state.value.barcodeLabels)
            return true
        if(state.value.employees)
            return true

        return false
    }

    private fun checkReports(): Boolean{
        if(state.value.salesReports)
            return true
        if(state.value.purchaseReports)
            return true
        if(state.value.profitReports)
            return true
        if(state.value.stockReports)
            return true
        if(state.value.accountReports)
            return true
        if(state.value.itemsReports)
            return true
        if(state.value.auditReports)
            return true

        return false
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long) {
        state.update { it.copy(isUpdate = isUpdate, updateId = updateId) }
    }

    // endregion
}