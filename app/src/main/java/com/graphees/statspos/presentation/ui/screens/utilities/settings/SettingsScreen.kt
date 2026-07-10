package com.graphees.statspos.presentation.ui.screens.utilities.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ExpandableSection
import com.graphees.statspos.presentation.ui.components.PasswordTextbox
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.utilities.settings.SettingsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object AppSettings : Routes()

    @Serializable
    data object AdminSettings : Routes()

    @Serializable
    data object PrintSettings : Routes()
}

@Composable
fun SettingsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.Home> {
                Home(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        onBack()
                    },
                    onAppSettingsClick = {
                        navigate(Routes.AppSettings)
                    },
                    onAdminSettingsClick = {
                        navigate(Routes.AdminSettings)
                    },
                    onPrintSettingsClick = {
                        navigate(Routes.PrintSettings)
                    },
                )
            }
            entry<Routes.AppSettings> { key ->
                AppSettingsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.AdminSettings> { key ->
                AdminSettingsScreen (
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.PrintSettings> { key ->
                PrintSettingsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        }
    )
}

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
    onAppSettingsClick:() -> Unit,
    onAdminSettingsClick:() -> Unit,
    onPrintSettingsClick:() -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<SettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
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
            viewModel.editData()
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
                title = "Settings",
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp)
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
                    Basic(
                        defaultRate = state.defaultRate,
                        defaultDiscount = state.defaultDiscount,
                        printLanguage = state.printLanguage,
                        onDefaultRateSelected = viewModel::onDefaultRateChange,
                        onDefaultDiscountSelected = viewModel::onDefaultDiscountChange,
                        onPrintLanguageSelected = viewModel::onPrintLanguageChange,
                    )

                    Settings(
                        saleUnderStock = state.saleUnderStock,
                        onSaleUnderStockChange = viewModel::onSaleUnderStockChange,
                        costWarning = state.costWarning,
                        onCostWarningChange = viewModel::onCostWarningChange,
                        stockWarning = state.stockWarning,
                        onStockWarningChange = viewModel::onStockWarningChange,
                        autoCreditSelect = state.autoCreditSelect,
                        onAutoCreditSelectChange = viewModel::onAutoCreditSelectChange,
                        showItemStock = state.showItemStock,
                        onShowItemStockChange = viewModel::onShowItemStockChange,
                        loadAutoCompleteItems = state.loadAutoCompleteItems,
                        onLoadAutoCompleteItemsChange = viewModel::onLoadAutoCompleteItemsChange,
                        paymentNotifications = state.paymentNotifications,
                        onPaymentNotificationsChange = viewModel::onPaymentNotificationsChange,
                        editOldCreditBill = state.editOldCreditBill,
                        onEditOldCreditBillChange = viewModel::onEditOldCreditBillChange,
                        autoRetailChange = state.autoRetailChange,
                        onAutoRetailChangeChange = viewModel::onAutoRetailChangeChange,
                        instantSearch = state.instantSearch,
                        onInstantSearchChange = viewModel::onInstantSearchChange,
                        useUrdu = state.useUrdu,
                        onUseUrduChange = viewModel::onUseUrduChange,
                        showLedgerInBill = state.showLedgerInBill,
                        onShowLedgerInBillChange = viewModel::onShowLedgerInBillChange,
                        showLedgerInVoucher = state.showLedgerInVoucher,
                        onShowLedgerInVoucherChange = viewModel::onShowLedgerInVoucherChange,
                        qtyChangeable = state.qtyChangeable,
                        onQtyChangeableChange = viewModel::onQtyChangeableChange,
                        saleCartons = state.saleCartons,
                        onSaleCartonsChange = viewModel::onSaleCartonsChange,
                        fourRateSystem = state.fourRateSystem,
                        onFourRateSystemChange = viewModel::onFourRateSystemChange,
                        sameDateBillEdit = state.sameDateBillEdit,
                        onSameDateBillEditChange = viewModel::onSameDateBillEditChange,
                        showCustomerLastRate = state.showCustomerLastRate,
                        onShowCustomerLastRateChange = viewModel::onShowCustomerLastRateChange,
                        alwaysUseLastRate = state.alwaysUseLastRate,
                        onAlwaysUseLastRateChange = viewModel::onAlwaysUseLastRateChange,
                        allowManyDuplicateBillPrints = state.allowManyDuplicateBillPrints,
                        onAllowManyDuplicateBillPrintsChange = viewModel::onAllowManyDuplicateBillPrintsChange,
                        isPaymentNecessary = state.isPaymentNecessary,
                        onIsPaymentNecessaryChange = viewModel::onIsPaymentNecessaryChange,
                        itemExistsInSalesWarning = state.itemExistsInSalesWarning,
                        onItemExistsInSalesWarningChange = viewModel::onItemExistsInSalesWarningChange,
                        shiftWiseSales = state.shiftWiseSales,
                        onShiftWiseSalesChange = viewModel::onShiftWiseSalesChange,
                        shiftWisePurchase = state.shiftWisePurchase,
                        onShiftWisePurchaseChange = viewModel::onShiftWisePurchaseChange,
                        fullWindowReports = state.fullWindowReports,
                        onFullWindowReportsChange = viewModel::onFullWindowReportsChange,
                    )

                    Passwords(
                        deleteItem = state.deleteItem,
                        deleteAccount = state.deleteAccount,
                        editSalesBill = state.editSalesBill,
                        editPurchaseBill = state.editPurchaseBill,
                        deleteSalesBill = state.deleteSalesBill,
                        deletePurchaseBill = state.deletePurchaseBill,
                        deleteEntry = state.deleteEntry,
                        onDeleteItemChange = viewModel::onDeleteItemChange,
                        onDeleteAccountChange = viewModel::onDeleteAccountChange,
                        onEditSalesBillChange = viewModel::onEditSalesBillChange,
                        onEditPurchaseBillChange = viewModel::onEditPurchaseBillChange,
                        onDeleteSalesBillChange = viewModel::onDeleteSalesBillChange,
                        onDeletePurchaseBillChange = viewModel::onDeletePurchaseBillChange,
                        onDeleteEntryChange = viewModel::onDeleteEntryChange,
                        useDeleteItem = state.useDeleteItem,
                        useDeleteAccount = state.useDeleteAccount,
                        useEditSalesBill = state.useEditSalesBill,
                        useEditPurchaseBill = state.useEditPurchaseBill,
                        useDeleteSalesBill = state.useDeleteSalesBill,
                        useDeletePurchaseBill = state.useDeletePurchaseBill,
                        useDeleteEntry = state.useDeleteEntry,
                        onUseDeleteItemChange = viewModel::onUseDeleteItemChange,
                        onUseDeleteAccountChange = viewModel::onUseDeleteAccountChange,
                        onUseEditSalesBillChange = viewModel::onUseEditSalesBillChange,
                        onUseEditPurchaseBillChange = viewModel::onUseEditPurchaseBillChange,
                        onUseDeleteSalesBillChange = viewModel::onUseDeleteSalesBillChange,
                        onUseDeletePurchaseBillChange = viewModel::onUseDeletePurchaseBillChange,
                        onUseDeleteEntryChange = viewModel::onUseDeleteEntryChange,
                    )

                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                    ) {
                        Row {
                            ReportButton("App Settings") {
                                onAppSettingsClick()
                            }
                            Spacer(Modifier.width(12.dp))
                            ReportButton("Print Settings") {
                                onPrintSettingsClick()
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row {
                            ReportButton("Other Settings") {
                                onAdminSettingsClick()
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Box(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    if (state.isLoading) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.updateSettings {
                                context.showToast("Settings saved successfully")
                                goBackWithResult()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Basic(
    defaultRate: DropdownItem?,
    defaultDiscount: DropdownItem?,
    printLanguage: DropdownItem?,
    onDefaultRateSelected: (DropdownItem) -> Unit,
    onDefaultDiscountSelected: (DropdownItem) -> Unit,
    onPrintLanguageSelected: (DropdownItem) -> Unit,

    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.defaultRate,
            selectedItem = defaultRate,
            onItemSelected = onDefaultRateSelected,
            label = {
                Text("Default Rate")
            },
            showEndIcon = false,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.defaultDiscount,
            selectedItem = defaultDiscount,
            onItemSelected = onDefaultDiscountSelected,
            label = {
                Text("Default Discount")
            },
            showEndIcon = false,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.printLanguages,
            selectedItem = printLanguage,
            onItemSelected = onPrintLanguageSelected,
            label = {
                Text("Print Language")
            },
            showEndIcon = false,
        )
    }
}

@Composable
private fun Settings(
    saleUnderStock: Boolean,
    onSaleUnderStockChange: (Boolean) -> Unit,
    costWarning: Boolean,
    onCostWarningChange: (Boolean) -> Unit,
    stockWarning: Boolean,
    onStockWarningChange: (Boolean) -> Unit,
    autoCreditSelect: Boolean,
    onAutoCreditSelectChange: (Boolean) -> Unit,
    showItemStock: Boolean,
    onShowItemStockChange: (Boolean) -> Unit,
    loadAutoCompleteItems: Boolean,
    onLoadAutoCompleteItemsChange: (Boolean) -> Unit,
    paymentNotifications: Boolean,
    onPaymentNotificationsChange: (Boolean) -> Unit,
    editOldCreditBill: Boolean,
    onEditOldCreditBillChange: (Boolean) -> Unit,
    autoRetailChange: Boolean,
    onAutoRetailChangeChange: (Boolean) -> Unit,
    instantSearch: Boolean,
    onInstantSearchChange: (Boolean) -> Unit,
    useUrdu: Boolean,
    onUseUrduChange: (Boolean) -> Unit,
    showLedgerInBill: Boolean,
    onShowLedgerInBillChange: (Boolean) -> Unit,
    showLedgerInVoucher: Boolean,
    onShowLedgerInVoucherChange: (Boolean) -> Unit,
    qtyChangeable: Boolean,
    onQtyChangeableChange: (Boolean) -> Unit,
    saleCartons: Boolean,
    onSaleCartonsChange: (Boolean) -> Unit,
    fourRateSystem: Boolean,
    onFourRateSystemChange: (Boolean) -> Unit,
    sameDateBillEdit: Boolean,
    onSameDateBillEditChange: (Boolean) -> Unit,
    showCustomerLastRate: Boolean,
    onShowCustomerLastRateChange: (Boolean) -> Unit,
    alwaysUseLastRate: Boolean,
    onAlwaysUseLastRateChange: (Boolean) -> Unit,
    allowManyDuplicateBillPrints: Boolean,
    onAllowManyDuplicateBillPrintsChange: (Boolean) -> Unit,
    isPaymentNecessary: Boolean,
    onIsPaymentNecessaryChange: (Boolean) -> Unit,
    itemExistsInSalesWarning: Boolean,
    onItemExistsInSalesWarningChange: (Boolean) -> Unit,
    shiftWiseSales: Boolean,
    onShiftWiseSalesChange: (Boolean) -> Unit,
    shiftWisePurchase: Boolean,
    onShiftWisePurchaseChange: (Boolean) -> Unit,
    fullWindowReports: Boolean,
    onFullWindowReportsChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Settings",
        initiallyExpanded = true,
    ) {
        Spacer(Modifier.height(12.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = saleUnderStock,
                onCheckedChange = onSaleUnderStockChange,
                label = "Sale Under Stock"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = costWarning,
                onCheckedChange = onCostWarningChange,
                label = "Cost Warning"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = qtyChangeable,
                onCheckedChange = onQtyChangeableChange,
                label = "Qty Changeable"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = saleCartons,
                onCheckedChange = onSaleCartonsChange,
                label = "Sale Cartons"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = fourRateSystem,
                onCheckedChange = onFourRateSystemChange,
                label = "Four Rate System"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = stockWarning,
                onCheckedChange = onStockWarningChange,
                label = "Generate Purchase Orders"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = shiftWiseSales,
                onCheckedChange = onShiftWiseSalesChange,
                label = "Shift Wise Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = shiftWisePurchase,
                onCheckedChange = onShiftWisePurchaseChange,
                label = "Shift Wise Purchase"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showItemStock,
                onCheckedChange = onShowItemStockChange,
                label = "Show Item Stock in Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = instantSearch,
                onCheckedChange = onInstantSearchChange,
                label = "Instant Search"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showLedgerInBill,
                onCheckedChange = onShowLedgerInBillChange,
                label = "Show Ledger in Bill"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showLedgerInVoucher,
                onCheckedChange = onShowLedgerInVoucherChange,
                label = "Show Ledger in Voucher"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = useUrdu,
                onCheckedChange = onUseUrduChange,
                label = "Use Urdu"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = paymentNotifications,
                onCheckedChange = onPaymentNotificationsChange,
                label = "Payment Notifications"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = autoCreditSelect,
                onCheckedChange = onAutoCreditSelectChange,
                label = "Auto Credit Select in Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = autoRetailChange,
                onCheckedChange = onAutoRetailChangeChange,
                label = "Customer Wise Retail Change"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showCustomerLastRate,
                onCheckedChange = onShowCustomerLastRateChange,
                label = "Show Customer Last Rate"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = alwaysUseLastRate,
                onCheckedChange = onAlwaysUseLastRateChange,
                label = "Always use Last Rate"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = sameDateBillEdit,
                onCheckedChange = onSameDateBillEditChange,
                label = "Same Date Credit Bill Edit"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = editOldCreditBill,
                onCheckedChange = onEditOldCreditBillChange,
                label = "Edit Old Credit Bill"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = itemExistsInSalesWarning,
                onCheckedChange = onItemExistsInSalesWarningChange,
                label = "Item Exists in Sales Warning"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = allowManyDuplicateBillPrints,
                onCheckedChange = onAllowManyDuplicateBillPrintsChange,
                label = "Allow Many Duplicates"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = loadAutoCompleteItems,
                onCheckedChange = onLoadAutoCompleteItemsChange,
                label = "Load Auto Complete Items"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = isPaymentNecessary,
                onCheckedChange = onIsPaymentNecessaryChange,
                label = "Payment Necessary in POS"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = fullWindowReports,
                onCheckedChange = onFullWindowReportsChange,
                label = "Full Window Reports"
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Passwords(
    deleteItem: String,
    deleteAccount: String,
    editSalesBill: String,
    editPurchaseBill: String,
    deleteSalesBill: String,
    deletePurchaseBill: String,
    deleteEntry: String,
    onDeleteItemChange: (String) -> Unit,
    onDeleteAccountChange: (String) -> Unit,
    onEditSalesBillChange: (String) -> Unit,
    onEditPurchaseBillChange: (String) -> Unit,
    onDeleteSalesBillChange: (String) -> Unit,
    onDeletePurchaseBillChange: (String) -> Unit,
    onDeleteEntryChange: (String) -> Unit,
    useDeleteItem: Boolean,
    useDeleteAccount: Boolean,
    useEditSalesBill: Boolean,
    useEditPurchaseBill: Boolean,
    useDeleteSalesBill: Boolean,
    useDeletePurchaseBill: Boolean,
    useDeleteEntry: Boolean,
    onUseDeleteItemChange: (Boolean) -> Unit,
    onUseDeleteAccountChange: (Boolean) -> Unit,
    onUseEditSalesBillChange: (Boolean) -> Unit,
    onUseEditPurchaseBillChange: (Boolean) -> Unit,
    onUseDeleteSalesBillChange: (Boolean) -> Unit,
    onUseDeletePurchaseBillChange: (Boolean) -> Unit,
    onUseDeleteEntryChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Passwords",
        initiallyExpanded = true,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = deleteItem,
                onValueChange = onDeleteItemChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Delete Item")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useDeleteItem,
                onCheckedChange = onUseDeleteItemChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = deleteAccount,
                onValueChange = onDeleteAccountChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Delete Accounts")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useDeleteAccount,
                onCheckedChange = onUseDeleteAccountChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = editSalesBill,
                onValueChange = onEditSalesBillChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Edit Sales Bill")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useEditSalesBill,
                onCheckedChange = onUseEditSalesBillChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = editPurchaseBill,
                onValueChange = onEditPurchaseBillChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Edit Purchase Bill")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useEditPurchaseBill,
                onCheckedChange = onUseEditPurchaseBillChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = deleteSalesBill,
                onValueChange = onDeleteSalesBillChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Delete Sales Bill")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useDeleteSalesBill,
                onCheckedChange = onUseDeleteSalesBillChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = deletePurchaseBill,
                onValueChange = onDeletePurchaseBillChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Delete Purchase Bill")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useDeletePurchaseBill,
                onCheckedChange = onUseDeletePurchaseBillChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = deleteEntry,
                onValueChange = onDeleteEntryChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Delete Entry")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useDeleteEntry,
                onCheckedChange = onUseDeleteEntryChange,
                label = "Use"
            )
        }
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
                DropdownItem(0L, "None"),
                DropdownItem(0L, "None"),
                DropdownItem(0L, "None"),
                {},
                {},
                {},
            )

            Settings(
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

            Passwords(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                {},
                {},
                {},
                {},
                {},
                {},
                {},
            )
        }

        Box(
            modifier = Modifier
                .padding(24.dp),
        ) {
            SaveButton {}
        }

    }
}