package com.graphees.statspos.presentation.ui.screens.sales.sales_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.sales.Sales
import com.graphees.statspos.domain.models.sales.SalesBillItems
import com.graphees.statspos.domain.models.sales.SalesBills
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppDropdownMenu
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIconButton
import com.graphees.statspos.presentation.ui.components.DropdownItem
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.components.UpgradeToPremiumBottomSheet
import com.graphees.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.HelpScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.PaymentScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesViewModel
import com.graphees.statspos.presentation.viewmodels.sales.sales_bill.SalesItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.SocketManager
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdateSalesItem(val updateId: Long, val isUpdate: Boolean, val sales: Sales) :
        Routes()

    @Serializable
    data class ViewBillMargin(
        val invoiceId: Long,
        val isPostedBill: Boolean,
        val totalDisc: Double
    ) : Routes()

    @Serializable
    data object SearchItem : Routes()

    @Serializable
    data object Payment : Routes()

    @Serializable
    data object Help : Routes()

}

@Composable
fun SalesItemsScreen(
    sharedViewModel: SharedViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    salesBill: SalesBills?,
    onBack: () -> Unit,
) {
    fun goBackWithResult() {
        onBack()
    }

    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }

    val salesViewModel = hiltViewModel<AddUpdateSalesViewModel>()
    val salesItemsViewModel = hiltViewModel<SalesItemsViewModel>()

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            salesViewModel.updateInitialState(
                invoiceId = invoiceId,
                isPendingBill = isPendingBill,
                isPostedBill = isPostedBill,
                salesBill = salesBill,
            )

            salesItemsViewModel.updateInitialState(
                invoiceId = invoiceId,
                isPostedBill = isPostedBill,
            )

            hasLoadedOnce = true
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
                    salesViewModel = salesViewModel,
                    salesItemsViewModel = salesItemsViewModel,
                    invoiceId = invoiceId,
                    isPendingBill = isPendingBill,
                    isPostedBill = isPostedBill,
                    onAddUpdateSalesItem = { updateId, isUpdate, sales ->
                        navigate(Routes.AddUpdateSalesItem(updateId, isUpdate, sales))
                    },
                    onMarginClick = { totalDisc ->
                        navigate(Routes.ViewBillMargin(invoiceId, isPostedBill, totalDisc))
                    },
                    onUpgradeClick = {
                        navigate(Routes.Payment)
                    },
                    onHelpClick = {
                        navigate(Routes.Help)
                    },
                    onBack = onBack,
                    goBackWithResult = {
                        goBackWithResult()
                    },
                )
            }
            entry<Routes.AddUpdateSalesItem> { key ->
                AddUpdateSalesItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    sales = key.sales,
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.ViewBillMargin> { key ->
                ViewSalesBillMarginScreen(
                    invoiceId = key.invoiceId,
                    isPostedBill = key.isPostedBill,
                    totalDisc = key.totalDisc,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.SearchItem> { key ->
                SearchItemsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.Payment> {
                PaymentScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.Help> {
                HelpScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    salesViewModel: AddUpdateSalesViewModel,
    salesItemsViewModel: SalesItemsViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    onAddUpdateSalesItem: (Long, Boolean, Sales) -> Unit,
    onMarginClick: (Double) -> Unit,
    onUpgradeClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBack: () -> Unit,
    goBackWithResult: () -> Unit,
) {
    val context = LocalContext.current
    val salesState by salesViewModel.state.collectAsStateWithLifecycle()
    val salesItemsState by salesItemsViewModel.state.collectAsStateWithLifecycle()
    val event by salesViewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showUpgradeToPremiumSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = salesViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!salesState.hasLoadedOnce) {
            salesViewModel.editData(invoiceId) {
                salesViewModel.setHasLoadedOnce(true)
            }
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = salesState.error,
            onDismiss = {
                showErrorDialog = false

                if(salesState.error!!.contains("upgrade to premium", ignoreCase = true))
                    showUpgradeToPremiumSheet = true
            },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this bill",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                salesViewModel.deleteData(invoiceId) {
                    context.showToast("Bill deleted successfully")
                    if (isPostedBill) {
                        sharedViewModel.notifyBillPosted()
                        onBack()
                    } else {
                        sharedViewModel.notifyBillSaved()
                        onBack()
                    }
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_SALES_BILL,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                salesViewModel.deleteData(invoiceId) {
                    context.showToast("Bill deleted successfully")
                    if (isPostedBill) {
                        sharedViewModel.notifyBillPosted()
                        onBack()
                    } else {
                        sharedViewModel.notifyBillSaved()
                        onBack()
                    }
                }
            }
        )
    }

    if (showUpgradeToPremiumSheet) {
        UpgradeToPremiumBottomSheet(
            onDismiss = {
                showUpgradeToPremiumSheet = false
            },
            onUpgradeClick = {
                onUpgradeClick()
            },
            onContactClick = {
                onHelpClick()
            }
        )
    }

    fun printBill(id: Long) {
        if (HP.appSettings.onlinePrints == true && salesState.print) {
            SocketManager.printSalesBill(
                invoiceId = id,
                isPendingBill = false,
                billType = 1
            )
        }
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
                    goBackWithResult()
                },
                title = "Total: ${HP.formatDecimal(salesState.total, mustDecimals = 1)}",
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            AppIcon(
                                icon = R.drawable.ic_user,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (HP.userRights.seeMargin == true) {
                            IconButton(
                                onClick = {
                                    menuExpanded = true
                                }
                            ) {
                                AppIcon(
                                    icon = Icons.Default.MoreVert,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            AppDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier
                                    .width(200.dp),
                            ) {
                                DropdownItem(
                                    text = "Margin",
                                    icon = {
                                        AppIcon(R.drawable.margin, size = 14.dp)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onMarginClick(salesState.totalDisc)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                modifier = Modifier
                    .fillMaxWidth(),
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                },
            ) {
                SalesCustomerBottomSheet(
                    salesViewModel = salesViewModel,
                    onRetailChange = { value ->
                        salesViewModel.onIsRetailChange(value)
                        if (isPendingBill || isPostedBill) {
                            salesViewModel.changeBillType(value) {
                                salesItemsViewModel.loadData(salesViewModel::updateTotal)

                                if (isPendingBill) {
                                    salesViewModel.tempClose {
                                        sharedViewModel.notifyBillSaved()
                                    }
                                }

                                if (isPostedBill) {
                                    salesViewModel.postBill {
                                        sharedViewModel.notifyBillPosted()
                                    }
                                }
                            }
                        }
                    },
                    onClick = {
                        showBottomSheet = false
                    },
                )
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (salesState.hasLoadedOnce) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        SalesBillItemsBody(
                            sharedViewModel = sharedViewModel,
                            salesViewModel = salesViewModel,
                            salesItemsViewModel = salesItemsViewModel,
                            snackbarHostState = snackbarHostState,
                            onAddButtonClick = { updateId, isUpdated, sales ->
                                onAddUpdateSalesItem(updateId, isUpdated, sales)
                            },
                        )
                    }
                }

                // Save & Post Buttons
                if (salesState.hasLoadedOnce) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                    ) {
                        if(HP.appSettings.onlinePrints == true) {
                            AppSwitch(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                checked = salesState.print,
                                onCheckedChange = salesViewModel::onPrintChange,
                                label = "Print"
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        Row {
                            if (!isPostedBill) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (salesItemsState.list.isNotEmpty()) 0.5f else 1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (salesState.isSaving) {
                                        AppCircularProgressIndicator()
                                    } else {
                                        SaveButton(text = "Temp Close") {
                                            salesViewModel.tempClose {
                                                sharedViewModel.notifyBillSaved()
                                                onBack()
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            if (salesItemsState.list.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (salesState.isPosting) {
                                        AppCircularProgressIndicator()
                                    } else {
                                        SaveButton(text = "Post") {
                                            salesViewModel.postBill { id ->
                                                printBill(id)
                                                sharedViewModel.notifyBillPosted()
                                                onBack()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (salesState.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
private fun SalesBillItemsBody(
    sharedViewModel: SharedViewModel,
    salesViewModel: AddUpdateSalesViewModel,
    salesItemsViewModel: SalesItemsViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Sales) -> Unit,
) {
    val context = LocalContext.current
    val salesState by salesViewModel.state.collectAsStateWithLifecycle()
    val state by salesItemsViewModel.state.collectAsStateWithLifecycle()
    val event by salesItemsViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = salesItemsViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    fun getSalesObject(): Sales {
        val sales = salesViewModel.getFormData()
        sales.id = salesState.invoiceId
        sales.isPostedBill = salesState.isPostedBill
        sales.invoiceNo = salesState.invoiceNo
        sales.totalItems = state.list.size
        return sales
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            salesItemsViewModel.loadData(salesViewModel::updateTotal) {
                if (salesItemsViewModel.state.value.list.isEmpty()) {
                    onAddButtonClick(0L, false, getSalesObject())
                }
            }
            salesItemsViewModel.setHasLoadedOnce(true)
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            salesItemsViewModel.loadData(salesViewModel::updateTotal)
            sharedViewModel.consumeDataChanged()
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
            text = "Are you sure to delete this item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                salesItemsViewModel.deleteData(selectedId) {
                    salesItemsViewModel.loadData(salesViewModel::updateTotal)
                    context.showToast("Item deleted successfully")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                onAddButtonClick(0L, false, getSalesObject())
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
//                    SearchBox(
//                        value = state.search,
//                        onValueChange = {
//                            salesItemsViewModel.onSearchChange(it, salesViewModel::updateTotal)
//                        },
//                        onSearchClick = {
//                            salesItemsViewModel.loadData(salesViewModel::updateTotal)
//                            keyboardController?.hide()
//                        },
//                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            salesItemsViewModel.loadData(salesViewModel::updateTotal)
                        },
                        items = state.list,
                        onItemClick = { salesBillItem ->
                            onAddButtonClick(salesBillItem.id!!, true, getSalesObject())
                        },
                        onDeleteClick = { salesBillItem ->
                            selectedId = salesBillItem.id!!
                            showDeleteDialog = true
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(vertical = 8.dp)
                ) {
                    HeadingMedium(text = "Items: ")
                    LabelMedium(text = state.totalItems.toString())
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Qty: ")
                    LabelMedium(text = HP.formatDecimal(state.totalQty))
                    if (HP.settings.saleCartons == true) {
                        Spacer(Modifier.width(8.dp))
                        HeadingMedium(text = "Crtn: ")
                        LabelMedium(text = state.totalCrtn.toString())
                    }
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Disc: ")
                    LabelMedium(text = HP.formatDecimal(state.totalItemDisc))
                }
            }
        }
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<SalesBillItems>,
    onItemClick: (SalesBillItems) -> Unit,
    onDeleteClick: (SalesBillItems) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        item {
            Spacer(Modifier.height(4.dp))
        }
        items(items) { item ->
            ListCard(
                item = item,
                onItemClick = onItemClick,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: SalesBillItems,
    onItemClick: (SalesBillItems) -> Unit,
    onDeleteClick: (SalesBillItems) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        onClick = {
            onItemClick(item)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            // Image
            ListImageView(
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .size(60.dp),
                showIfNull = true,
            ) {
                Spacer(Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            modifier = modifier,
                            text = item.itemname.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ListHeading(text = "Total: ")
                            ListLabel(text = HP.formatDecimal(item.total))
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    DeleteIconButton {
                        onDeleteClick(item)
                    }
                }

                Spacer(Modifier.height(4.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = "Qty",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                    ListHeading(
                        text = "Rate",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                    ListHeading(
                        text = "Disc",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListLabel(
                        text = HP.formatDecimal(item.qty),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.rate),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.disc),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                }
                if (HP.settings.saleCartons == true) {
                    Spacer(Modifier.height(4.dp))
                    ListHorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListHeading(
                            text = "Crtn",
                            Modifier.weight(0.5f),
                            color = primaryColor,
                        )
                        ListHeading(
                            text = "Crtn Rate",
                            Modifier.weight(1f),
                            color = primaryColor,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListLabel(
                            text = item.crtn.toString(),
                            Modifier.weight(0.5f),
                            color = secondaryColor,
                        )
                        ListLabel(
                            text = HP.formatDecimal(item.crtnRate),
                            Modifier.weight(1f),
                            color = secondaryColor,
                        )
                    }
                }

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .weight(.7f)
//                    ) {
//                        ListHeading(text = "Qty: ", color = primaryColor)
//                        ListLabel(text = HP.formatDecimal(item.qty), color = secondaryColor)
//                    }
//                    Row(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        ListHeading(text = "Rate: ", color = primaryColor)
//                        ListLabel(text = HP.formatDecimal(item.rate), color = secondaryColor)
//                    }
//                }
//                if (HP.settings.saleCartons == true) {
//                    Spacer(Modifier.height(4.dp))
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .weight(.7f)
//                        ) {
//                            ListHeading(text = "Crtn: ", color = primaryColor)
//                            ListLabel(text = item.crtn.toString(), color = secondaryColor)
//                        }
//                        Row(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            ListHeading(text = "Crtn Rate: ", color = primaryColor)
//                            ListLabel(text = HP.formatDecimal(item.crtnRate), color = secondaryColor)
//                        }
//                    }
//                }
//                Spacer(Modifier.height(4.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .weight(.7f)
//                    ) {
//                        ListHeading(text = "Disc: ", color = primaryColor)
//                        ListLabel(text = HP.formatDecimal(item.disc), color = secondaryColor)
//                    }
//                    Row(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        ListMainHeading(text = "Total: ")
//                        ListMainLabel(text = HP.formatDecimal(item.total))
//                    }
//                }
            }
        }
    }
}

