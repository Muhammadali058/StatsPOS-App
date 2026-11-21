package com.graphees.statspos.presentation.ui.screens.purchase.purchase_bill

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.graphees.statspos.domain.models.purchase.Purchase
import com.graphees.statspos.domain.models.purchase.PurchaseBillItems
import com.graphees.statspos.domain.models.purchase.PurchaseBills
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIconButton
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
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.components.UpgradeToPremiumBottomSheet
import com.graphees.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.HelpScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.PaymentScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.purchase.purchase_bill.AddUpdatePurchaseViewModel
import com.graphees.statspos.presentation.viewmodels.purchase.purchase_bill.PurchaseItemsViewModel
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
    data class AddUpdatePurchaseItem(
        val updateId: Long,
        val isUpdate: Boolean,
        val purchase: Purchase
    ) :
        Routes()

    @Serializable
    data object SearchItem : Routes()

    @Serializable
    data object Payment : Routes()

    @Serializable
    data object Help : Routes()

}

@Composable
fun PurchaseItemsScreen(
    sharedViewModel: SharedViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    purchaseBill: PurchaseBills?,
    onBack: () -> Unit,
) {
    val purchaseViewModel = hiltViewModel<AddUpdatePurchaseViewModel>()
    val purchaseItemsViewModel = hiltViewModel<PurchaseItemsViewModel>()
    fun goBackWithResult() {
        onBack()
    }

    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            purchaseViewModel.updateInitialState(
                invoiceId = invoiceId,
                isPendingBill = isPendingBill,
                isPostedBill = isPostedBill,
                purchaseBill = purchaseBill,
            )

            purchaseItemsViewModel.updateInitialState(
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
                    purchaseViewModel = purchaseViewModel,
                    purchaseItemsViewModel = purchaseItemsViewModel,
                    invoiceId = invoiceId,
                    isPostedBill = isPostedBill,
                    onAddUpdatePurchaseItem = { updateId, isUpdate, purchase ->
                        navigate(Routes.AddUpdatePurchaseItem(updateId, isUpdate, purchase))
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
            entry<Routes.AddUpdatePurchaseItem> { key ->
                AddUpdatePurchaseItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    purchase = key.purchase,
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
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
    purchaseViewModel: AddUpdatePurchaseViewModel,
    purchaseItemsViewModel: PurchaseItemsViewModel,
    invoiceId: Long = 0L,
    isPostedBill: Boolean = false,
    onAddUpdatePurchaseItem: (Long, Boolean, Purchase) -> Unit,
    onUpgradeClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBack: () -> Unit,
    goBackWithResult: () -> Unit,
) {
    val context = LocalContext.current
    val purchaseState by purchaseViewModel.state.collectAsStateWithLifecycle()
    val purchaseItemsState by purchaseItemsViewModel.state.collectAsStateWithLifecycle()
    val event by purchaseViewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showUpgradeToPremiumSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = purchaseViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!purchaseState.hasLoadedOnce) {
            purchaseViewModel.editData(invoiceId) {
                purchaseViewModel.setHasLoadedOnce(true)
            }
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = purchaseState.error,
            onDismiss = {
                showErrorDialog = false

                if(purchaseState.error!!.contains("upgrade to premium", ignoreCase = true))
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
                purchaseViewModel.deleteData(invoiceId) {
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
            passwordFor = PasswordFor.DELETE_PURCHASE_BILL,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                purchaseViewModel.deleteData(invoiceId) {
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

    fun printBill(id: Long) {
        if (HP.appSettings.onlinePrints == true && purchaseState.print) {
            SocketManager.printPurchaseBill(
                invoiceId = id,
                isPendingBill = false,
                billType = 1
            )
        }
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
                title = "Total: ${HP.formatDecimal(purchaseState.total, mustDecimals = 1)}",
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
                PurchaseVendorBottomSheet (
                    purchaseViewModel = purchaseViewModel,
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
                if (purchaseState.hasLoadedOnce) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        PurchaseBillItemsBody(
                            sharedViewModel = sharedViewModel,
                            purchaseViewModel = purchaseViewModel,
                            purchaseItemsViewModel = purchaseItemsViewModel,
                            snackbarHostState = snackbarHostState,
                            onAddButtonClick = { updateId, isUpdated, sales ->
                                onAddUpdatePurchaseItem(updateId, isUpdated, sales)
                            },
                        )
                    }
                }

                // Save & Post Buttons
                if (purchaseState.hasLoadedOnce) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                    ) {
//                        if(HP.appSettings.onlinePrints == true) {
//                            AppSwitch(
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                checked = purchaseState.print,
//                                onCheckedChange = purchaseViewModel::onPrintChange,
//                                label = "Print"
//                            )
//                            Spacer(Modifier.height(8.dp))
//                        }
                        Row{
                            if (!isPostedBill) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (purchaseItemsState.list.isNotEmpty()) 0.5f else 1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (purchaseState.isSaving) {
                                        AppCircularProgressIndicator()
                                    } else {
                                        AppSaveButton(text = "Temp Close") {
                                            purchaseViewModel.tempClose {
                                                sharedViewModel.notifyBillSaved()
                                                onBack()
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            if (purchaseItemsState.list.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (purchaseState.isPosting) {
                                        AppCircularProgressIndicator()
                                    } else {
                                        AppSaveButton(text = "Post") {
                                            purchaseViewModel.postBill { id ->
//                                                printBill(id)
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

            if (purchaseState.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}


@Composable
private fun PurchaseBillItemsBody(
    sharedViewModel: SharedViewModel,
    purchaseViewModel: AddUpdatePurchaseViewModel,
    purchaseItemsViewModel: PurchaseItemsViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Purchase) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val salesState by purchaseViewModel.state.collectAsStateWithLifecycle()
    val state by purchaseItemsViewModel.state.collectAsStateWithLifecycle()
    val event by purchaseItemsViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = purchaseItemsViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    fun getPurchaseObject(): Purchase {
        val purchase = purchaseViewModel.getFormData()
        purchase.id = salesState.invoiceId
        purchase.isPostedBill = salesState.isPostedBill
        purchase.invoiceNo = salesState.invoiceNo
        purchase.totalItems = state.list.size
        return purchase
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal) {
                if(purchaseItemsViewModel.state.value.list.isEmpty()) {
                    onAddButtonClick(0L, false, getPurchaseObject())
                }
            }
            purchaseItemsViewModel.setHasLoadedOnce(true)
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
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
                purchaseItemsViewModel.deleteData(selectedId) {
                    purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
                    context.showToast("Item deleted successfully")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                onAddButtonClick(0L, false, getPurchaseObject())
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
//                            purchaseItemsViewModel.onSearchChange(
//                                it,
//                                purchaseViewModel::updateTotal
//                            )
//                        },
//                        onSearchClick = {
//                            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
//                            keyboardController?.hide()
//                        },
//                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
                        },
                        items = state.list,
                        onItemClick = { purchaseBillItem ->
                            onAddButtonClick(purchaseBillItem.id!!, true, getPurchaseObject())
                        },
                        onDeleteClick = { purchaseBillItem ->
                            selectedId = purchaseBillItem.id!!
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
    items: List<PurchaseBillItems>,
    onItemClick: (PurchaseBillItems) -> Unit,
    onDeleteClick: (PurchaseBillItems) -> Unit,
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
    item: PurchaseBillItems,
    onItemClick: (PurchaseBillItems) -> Unit,
    onDeleteClick: (PurchaseBillItems) -> Unit,
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
                            modifier = Modifier,
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
                    Row(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        ListHeading(
                            text = "Qty: ",
                            color = primaryColor,
                        )
                        ListLabel(
                            text = HP.formatDecimal(item.qty),
                            color = secondaryColor,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        ListHeading(
                            text = "Cost: ",
                            color = primaryColor,
                        )
                        ListLabel(
                            text = HP.formatDecimal(item.finalCost),
                            color = secondaryColor,
                        )
                    }
                }

                if (HP.settings.saleCartons == true) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            ListHeading(
                                text = "Crtn: ",
                                color = primaryColor,
                            )
                            ListLabel(
                                text = item.crtn.toString(),
                                color = secondaryColor,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            ListHeading(
                                text = "Cost Crtn: ",
                                color = primaryColor,
                            )
                            ListLabel(
                                text = if (item.crtn!! != 0) HP.formatDecimal((item.finalCost!! * item.crtnSize!!)) else "0",
                                color = secondaryColor,
                            )
                        }
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
                        text = "Gross Cost",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                    ListHeading(
                        text = "Disc",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                    ListHeading(
                        text = "Tax",
                        Modifier.weight(1f),
                        color = primaryColor,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListLabel(
                        text = HP.formatDecimal(item.cost),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.calculatedDisc),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.tax),
                        Modifier.weight(1f),
                        color = secondaryColor,
                    )
                }
            }
        }
    }
}
