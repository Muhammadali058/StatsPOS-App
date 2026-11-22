package com.graphees.statspos.presentation.ui.screens.utilities.settings.home

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
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.utilities.settings.PrintSettingsScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.utilities.settings.SalesSettingsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object PrintSettings : Routes()
}

@Composable
fun SalesSettingsScreen(
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
                    onPrintSettingsClick = {
                        navigate(Routes.PrintSettings)
                    },
                    onBack = {
                        onBack()
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
    onPrintSettingsClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SalesSettingsViewModel>()
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
                title = "Sales Settings",
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(bottom = 8.dp)
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
                        .imePadding()
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Basic(
                        defaultRate = state.defaultRate,
                        printLanguage = state.printLanguage,
                        onDefaultRateSelected = viewModel::onDefaultRateChange,
                        onPrintLanguageSelected = viewModel::onPrintLanguageChange,
                    )
                    Spacer(Modifier.height(8.dp))
                    Settings(
                        saleUnderStock = state.saleUnderStock,
                        onSaleUnderStockChange = viewModel::onSaleUnderStockChange,
                        costWarning = state.costWarning,
                        onCostWarningChange = viewModel::onCostWarningChange,
                        autoCreditSelect = state.autoCreditSelect,
                        onAutoCreditSelectChange = viewModel::onAutoCreditSelectChange,
                        showItemStock = state.showItemStock,
                        onShowItemStockChange = viewModel::onShowItemStockChange,
                        editOldCreditBill = state.editOldCreditBill,
                        onEditOldCreditBillChange = viewModel::onEditOldCreditBillChange,
                        autoRetailChange = state.autoRetailChange,
                        onAutoRetailChangeChange = viewModel::onAutoRetailChangeChange,
                        showLedgerInBill = state.showLedgerInBill,
                        onShowLedgerInBillChange = viewModel::onShowLedgerInBillChange,
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
                        itemExistsInSalesWarning = state.itemExistsInSalesWarning,
                        onItemExistsInSalesWarningChange = viewModel::onItemExistsInSalesWarningChange,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ReportButton("Print Settings") {
                            onPrintSettingsClick()
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
                        AppSaveButton {
                            viewModel.updateSettings {
                                context.showToast("Settings saved successfully")
                                onBack()
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
    printLanguage: DropdownItem?,
    onDefaultRateSelected: (DropdownItem) -> Unit,
    onPrintLanguageSelected: (DropdownItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Spacer(Modifier.height(8.dp))
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
            outlined = true,
        )
//        ComboBox(
//            modifier = Modifier
//                .fillMaxWidth(),
//            items = HP.printLanguages,
//            selectedItem = printLanguage,
//            onItemSelected = onPrintLanguageSelected,
//            label = {
//                Text("Print Language")
//            },
//            showEndIcon = false,
//            outlined = true,
//        )
    }
}

@Composable
private fun Settings(
    saleUnderStock: Boolean,
    onSaleUnderStockChange: (Boolean) -> Unit,
    costWarning: Boolean,
    onCostWarningChange: (Boolean) -> Unit,
    autoCreditSelect: Boolean,
    onAutoCreditSelectChange: (Boolean) -> Unit,
    showItemStock: Boolean,
    onShowItemStockChange: (Boolean) -> Unit,
    editOldCreditBill: Boolean,
    onEditOldCreditBillChange: (Boolean) -> Unit,
    autoRetailChange: Boolean,
    onAutoRetailChangeChange: (Boolean) -> Unit,
    showLedgerInBill: Boolean,
    onShowLedgerInBillChange: (Boolean) -> Unit,
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
    itemExistsInSalesWarning: Boolean,
    onItemExistsInSalesWarningChange: (Boolean) -> Unit,
) {
    Spacer(Modifier.height(12.dp))
    val paddingBottom = 24.dp

    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        // region Column 1
        Column(
            modifier =
                Modifier.weight(1f),
        ){
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = saleCartons,
                onCheckedChange = onSaleCartonsChange,
                label = "Sale Cartons"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = fourRateSystem,
                onCheckedChange = onFourRateSystemChange,
                label = "Four Rates in Sales"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = autoCreditSelect,
                onCheckedChange = onAutoCreditSelectChange,
                label = "Auto Credit When Customer Selected"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = autoRetailChange,
                onCheckedChange = onAutoRetailChangeChange,
                label = "Customer Wise Retail Change"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = showCustomerLastRate,
                onCheckedChange = onShowCustomerLastRateChange,
                label = "Show Customer Last Rate"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = alwaysUseLastRate,
                onCheckedChange = onAlwaysUseLastRateChange,
                label = "Always use Last Rate"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = showLedgerInBill,
                onCheckedChange = onShowLedgerInBillChange,
                label = "Show Ledger in Bill"
            )
        }
        // endregion

        // region Column 2
        Column(
            modifier =
                Modifier.weight(1f),
        ){
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = costWarning,
                onCheckedChange = onCostWarningChange,
                label = "Cost Warning"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = saleUnderStock,
                onCheckedChange = onSaleUnderStockChange,
                label = "Sale Under Stock"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = editOldCreditBill,
                onCheckedChange = onEditOldCreditBillChange,
                label = "Edit Old Credit Bill"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = sameDateBillEdit,
                onCheckedChange = onSameDateBillEditChange,
                label = "Same Date Credit Bill Edit"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = showItemStock,
                onCheckedChange = onShowItemStockChange,
                label = "Show Item Stock in Sales"
            )
            AppSwitch(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                checked = itemExistsInSalesWarning,
                onCheckedChange = onItemExistsInSalesWarningChange,
                label = "Item Exists in Sales Warning"
            )
        }
        // endregion
    }
}
