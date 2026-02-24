package com.example.statspos.presentation.ui.screens.utilities.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.PasswordTextbox
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.UploadImageView
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.utilities.users.AddUpdateUserViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import okhttp3.MultipartBody

@Composable
fun AddUpdateUserScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateUserViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialState(
                isUpdate = isUpdate,
                updateId = updateId
            )

            if (isUpdate) {
                viewModel.editData(updateId)
            }

            viewModel.setHasLoadedOnce(true)
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this user",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("User deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = if (isUpdate) "Update User" else "Add User",
                actions = {
                    Row {
                        if (isUpdate) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    showDeleteDialog = true
                                }) {
                                    AppIcon(
                                        icon = Icons.Default.Delete,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(8.dp))
                    Basic(
                        username = state.username,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        userType = state.userType,
                        shift = state.shift,

                        onUsernameChange = viewModel::onUsernameChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                        onUserTypeSelected = viewModel::onUserTypeSelected,
                        onShiftSelected = viewModel::onShiftSelected,
                    )

                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        }
                    )

                    UserRights(
                        items = state.items,
                        onItemsChange = viewModel::onItemsChange,
                        categories = state.categories,
                        onCategoriesChange = viewModel::onCategoriesChange,
                        sales = state.sales,
                        onSalesChange = viewModel::onSalesChange,
                        purchase = state.purchase,
                        onPurchaseChange = viewModel::onPurchaseChange,
                        warehouse = state.warehouse,
                        onWarehouseChange = viewModel::onWarehouseChange,
//                        Accounts
                        customers = state.customers,
                        onCustomersChange = viewModel::onCustomersChange,
                        vendors = state.vendors,
                        onVendorsChange = viewModel::onVendorsChange,
                        suppliers = state.suppliers,
                        onSuppliersChange = viewModel::onSuppliersChange,
                        banks = state.banks,
                        onBanksChange = viewModel::onBanksChange,
                        expenses = state.expenses,
                        onExpensesChange = viewModel::onExpensesChange,
//                        Users
                        users = state.users,
                        onUsersChange = viewModel::onUsersChange,
                        settings = state.settings,
                        onSettingsChange = viewModel::onSettingsChange,
                        barcodeLabels = state.barcodeLabels,
                        onBarcodeLabelsChange = viewModel::onBarcodeLabelsChange,
                        employees = state.employees,
                        onEmployeesChange = viewModel::onEmployeesChange,
//                        Reports
                        salesReports = state.salesReports,
                        onSalesReportsChange = viewModel::onSalesReportsChange,
                        purchaseReports = state.purchaseReports,
                        onPurchaseReportsChange = viewModel::onPurchaseReportsChange,
                        profitReports = state.profitReports,
                        onProfitReportsChange = viewModel::onProfitReportsChange,
                        stockReports = state.stockReports,
                        onStockReportsChange = viewModel::onStockReportsChange,
                        accountReports = state.accountReports,
                        onAccountReportsChange = viewModel::onAccountReportsChange,
                        itemsReports = state.itemsReports,
                        onItemsReportsChange = viewModel::onItemsReportsChange,
                        auditReports = state.auditReports,
                        onAuditReportsChange = viewModel::onAuditReportsChange,
//                        Others
                        dateWiseEntry = state.dateWiseEntry,
                        onDateWiseEntryChange = viewModel::onDateWiseEntryChange,
                        dateWisePurchase = state.dateWisePurchase,
                        onDateWisePurchaseChange = viewModel::onDateWisePurchaseChange,
                        printDuplicates = state.printDuplicates,
                        onPrintDuplicatesChange = viewModel::onPrintDuplicatesChange,
                        deleteAnything = state.deleteAnything,
                        onDeleteAnythingChange = viewModel::onDeleteAnythingChange,
                        entry = state.entry,
                        onEntryChange = viewModel::onEntryChange,
//                        POS
                        changeRates = state.changeRates,
                        onChangeRatesChange = viewModel::onChangeRatesChange,
                        seeMargin = state.seeMargin,
                        onSeeMarginChange = viewModel::onSeeMarginChange,
                        salesReturn = state.salesReturn,
                        onSalesReturnChange = viewModel::onSalesReturnChange,
                        creditBill = state.creditBill,
                        onCreditBillChange = viewModel::onCreditBillChange,
                        editSalesBill = state.editSalesBill,
                        onEditSalesBillChange = viewModel::onEditSalesBillChange,
                        editCreditBill = state.editCreditBill,
                        onEditCreditBillChange = viewModel::onEditCreditBillChange,
                        dateWiseSales = state.dateWiseSales,
                        onDateWiseSalesChange = viewModel::onDateWiseSalesChange,
                        payBill = state.payBill,
                        onPayBillChange = viewModel::onPayBillChange,
                        discount = state.discount,
                        onDiscountChange = viewModel::onDiscountChange,
                        seeCost = state.seeCost,
                        onSeeCostChange = viewModel::onSeeCostChange,
                        searchItems = state.searchItems,
                        onSearchItemsChange = viewModel::onSearchItemsChange,
                        fbrInvoice = state.fbrInvoice,
                        onFbrInvoiceChange = viewModel::onFbrInvoiceChange,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(vertical = 16.dp)
                ) {
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.insertOrUpdateData {
                                goBackWithResult()
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }

    }
}

@Composable
private fun Basic(
    username: String,
    password: String,
    confirmPassword: String,
    userType: DropdownItem,
    shift: DropdownItem,

    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUserTypeSelected: (DropdownItem) -> Unit,
    onShiftSelected: (DropdownItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        Textbox(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Username")
            }
        )
        PasswordTextbox(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Password")
            }
        )
        PasswordTextbox(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Confirm Password")
            }
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.userTypes,
            selectedItem = userType,
            onItemSelected = onUserTypeSelected,
            label = {
                Text("User Type")
            },
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.shifts,
            selectedItem = shift,
            onItemSelected = onShiftSelected,
            label = {
                Text("Shift")
            },
        )
    }
}

@Composable
private fun UserRights(
    items: Boolean,
    onItemsChange: (Boolean) -> Unit,
    categories: Boolean,
    onCategoriesChange: (Boolean) -> Unit,
    sales: Boolean,
    onSalesChange: (Boolean) -> Unit,
    purchase: Boolean,
    onPurchaseChange: (Boolean) -> Unit,
    warehouse: Boolean,
    onWarehouseChange: (Boolean) -> Unit,
//    Accounts
    customers: Boolean,
    onCustomersChange: (Boolean) -> Unit,
    vendors: Boolean,
    onVendorsChange: (Boolean) -> Unit,
    suppliers: Boolean,
    onSuppliersChange: (Boolean) -> Unit,
    banks: Boolean,
    onBanksChange: (Boolean) -> Unit,
    expenses: Boolean,
    onExpensesChange: (Boolean) -> Unit,
//    Utilities
    users: Boolean,
    onUsersChange: (Boolean) -> Unit,
    settings: Boolean,
    onSettingsChange: (Boolean) -> Unit,
    barcodeLabels: Boolean,
    onBarcodeLabelsChange: (Boolean) -> Unit,
    employees: Boolean,
    onEmployeesChange: (Boolean) -> Unit,
//    Reports
    salesReports: Boolean,
    onSalesReportsChange: (Boolean) -> Unit,
    purchaseReports: Boolean,
    onPurchaseReportsChange: (Boolean) -> Unit,
    profitReports: Boolean,
    onProfitReportsChange: (Boolean) -> Unit,
    stockReports: Boolean,
    onStockReportsChange: (Boolean) -> Unit,
    accountReports: Boolean,
    onAccountReportsChange: (Boolean) -> Unit,
    itemsReports: Boolean,
    onItemsReportsChange: (Boolean) -> Unit,
    auditReports: Boolean,
    onAuditReportsChange: (Boolean) -> Unit,
//    Others
    dateWiseEntry: Boolean,
    onDateWiseEntryChange: (Boolean) -> Unit,
    dateWisePurchase: Boolean,
    onDateWisePurchaseChange: (Boolean) -> Unit,
    printDuplicates: Boolean,
    onPrintDuplicatesChange: (Boolean) -> Unit,
    deleteAnything: Boolean,
    onDeleteAnythingChange: (Boolean) -> Unit,
    entry: Boolean,
    onEntryChange: (Boolean) -> Unit,
//    POS
    changeRates: Boolean,
    onChangeRatesChange: (Boolean) -> Unit,
    seeMargin: Boolean,
    onSeeMarginChange: (Boolean) -> Unit,
    salesReturn: Boolean,
    onSalesReturnChange: (Boolean) -> Unit,
    creditBill: Boolean,
    onCreditBillChange: (Boolean) -> Unit,
    editSalesBill: Boolean,
    onEditSalesBillChange: (Boolean) -> Unit,
    editCreditBill: Boolean,
    onEditCreditBillChange: (Boolean) -> Unit,
    dateWiseSales: Boolean,
    onDateWiseSalesChange: (Boolean) -> Unit,
    payBill: Boolean,
    onPayBillChange: (Boolean) -> Unit,
    discount: Boolean,
    onDiscountChange: (Boolean) -> Unit,
    seeCost: Boolean,
    onSeeCostChange: (Boolean) -> Unit,
    searchItems: Boolean,
    onSearchItemsChange: (Boolean) -> Unit,
    fbrInvoice: Boolean,
    onFbrInvoiceChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "User Rights",
        initiallyExpanded = false,
    ) {
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = items,
                onCheckedChange = onItemsChange,
                label = "Items"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = categories,
                onCheckedChange = onCategoriesChange,
                label = "Categories"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = sales,
                onCheckedChange = onSalesChange,
                label = "Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = purchase,
                onCheckedChange = onPurchaseChange,
                label = "Purchase"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = warehouse,
                onCheckedChange = onWarehouseChange,
                label = "Warehouse"
            )
        }
        Spacer(Modifier.height(8.dp))
        Title("Accounts")
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = customers,
                onCheckedChange = onCustomersChange,
                label = "Customers"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = vendors,
                onCheckedChange = onVendorsChange,
                label = "Vendors"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = suppliers,
                onCheckedChange = onSuppliersChange,
                label = "Suppliers"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = banks,
                onCheckedChange = onBanksChange,
                label = "Banks"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = expenses,
                onCheckedChange = onExpensesChange,
                label = "Expenses"
            )
        }
        Spacer(Modifier.height(8.dp))
        Title("Utilities")
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = users,
                onCheckedChange = onUsersChange,
                label = "Users"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = settings,
                onCheckedChange = onSettingsChange,
                label = "Settings"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = barcodeLabels,
                onCheckedChange = onBarcodeLabelsChange,
                label = "Barcode Labels"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = employees,
                onCheckedChange = onEmployeesChange,
                label = "Employees"
            )
        }
        Spacer(Modifier.height(8.dp))
        Title("Reports")
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = salesReports,
                onCheckedChange = onSalesReportsChange,
                label = "Sales Reports"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = purchaseReports,
                onCheckedChange = onPurchaseReportsChange,
                label = "Purchase Reports"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = profitReports,
                onCheckedChange = onProfitReportsChange,
                label = "Profit Reports"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = stockReports,
                onCheckedChange = onStockReportsChange,
                label = "Stock Reports"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = accountReports,
                onCheckedChange = onAccountReportsChange,
                label = "Accounts Reports"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = itemsReports,
                onCheckedChange = onItemsReportsChange,
                label = "Items Reports"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = auditReports,
                onCheckedChange = onAuditReportsChange,
                label = "Audit Reports"
            )
        }
        Spacer(Modifier.height(8.dp))
        Title("Others")
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = dateWiseEntry,
                onCheckedChange = onDateWiseEntryChange,
                label = "Date Wise Entry"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = dateWisePurchase,
                onCheckedChange = onDateWisePurchaseChange,
                label = "Date Wise Purchase"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = printDuplicates,
                onCheckedChange = onPrintDuplicatesChange,
                label = "Print Duplicates"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = deleteAnything,
                onCheckedChange = onDeleteAnythingChange,
                label = "Delete Anything"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = entry,
                onCheckedChange = onEntryChange,
                label = "Entry"
            )
        }
        Spacer(Modifier.height(8.dp))
        Title("POS")
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = changeRates,
                onCheckedChange = onChangeRatesChange,
                label = "Change Rates"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = seeMargin,
                onCheckedChange = onSeeMarginChange,
                label = "See Margin"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = salesReturn,
                onCheckedChange = onSalesReturnChange,
                label = "Sales Return"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = creditBill,
                onCheckedChange = onCreditBillChange,
                label = "Credit Bill"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = editSalesBill,
                onCheckedChange = onEditSalesBillChange,
                label = "Edit Sales Bill"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = editCreditBill,
                onCheckedChange = onEditCreditBillChange,
                label = "Edit Credit Bill"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = dateWiseSales,
                onCheckedChange = onDateWiseSalesChange,
                label = "Date Wise Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = discount,
                onCheckedChange = onDiscountChange,
                label = "Discount"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = payBill,
                onCheckedChange = onPayBillChange,
                label = "Pay Bill"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = seeCost,
                onCheckedChange = onSeeCostChange,
                label = "See Cost"
            )
        }
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = searchItems,
                onCheckedChange = onSearchItemsChange,
                label = "Search Items"
            )
            if (HP.branch.fbrIntegrated == true) {
                AppSwitch(
                    modifier = Modifier.weight(1f),
                    checked = fbrInvoice,
                    onCheckedChange = onFbrInvoiceChange,
                    label = "FBR Invoice"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ImageExpandable(
    isUploadingImage: Boolean,
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
) {
    ExpandableSection(
        title = "Image",
        initiallyExpanded = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isUploadingImage) {
                AppCircularProgressIndicator()
            } else {
                UploadImageView(
                    imageUrl = imageUrl,
                    onImageUrlChange = onImageUrlChange
                )
            }
        }
    }
}

@Composable
private fun Title(
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        AppText(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyPrev() {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Basic(
                "",
                "",
                "",
                DropdownItem(0L, "None"),
                DropdownItem(0L, "None"),
                {},
                {},
                {},
                {},
                {},
            )

            ImageExpandable(
                isUploadingImage = false,
                imageUrl = "",
                onImageUrlChange = { },
            )

            UserRights(
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
                false,
                { },
            )

        }

        Box(
            modifier = Modifier
                .padding(16.dp),
        ) {
            SaveButton {}
        }

    }
}