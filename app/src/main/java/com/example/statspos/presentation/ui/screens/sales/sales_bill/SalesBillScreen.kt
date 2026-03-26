package com.example.statspos.presentation.ui.screens.sales.sales_bill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppDropdownMenu
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.TabLayout
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.SalesItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
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
}

@Composable
fun SalesBillScreen(
    sharedViewModel: SharedViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    salesBill: SalesBills?,
    onBack: () -> Unit,
) {
    val salesViewModel = hiltViewModel<AddUpdateSalesViewModel>()
    val salesItemsViewModel = hiltViewModel<SalesItemsViewModel>()
    fun goBackWithResult() {
//        if(isPendingBill){
//            viewModel.tempClose {
//                sharedViewModel.notifyBillSaved()
//                onBack()
//            }
//        }else if(isPostedBill) {
//            viewModel.postBill {
//                sharedViewModel.notifyBillPosted()
//                onBack()
//            }
//        }

        onBack()
    }

    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }

//    BackHandler {
//        if (backStack.size == 1) {
//            goBackWithResult()
//        }
//    }

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
        }
    )
}

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
    onBack: () -> Unit,
    goBackWithResult: () -> Unit,
) {
    val tabs = listOf("Items", "Details")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val context = LocalContext.current
    val salesState by salesViewModel.state.collectAsStateWithLifecycle()
    val salesItemsState by salesItemsViewModel.state.collectAsStateWithLifecycle()
    val event by salesViewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

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
            salesViewModel.editData(invoiceId){
                salesViewModel.setHasLoadedOnce(true)
            }
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = salesState.error,
            onDismiss = {
                showErrorDialog = false
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
                        if (isPendingBill) {
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        } else if (isPostedBill) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    if (HP.passwords.useDeleteSalesBill == true) {
                                        showPasswordDialog = true
                                    } else {
                                        showDeleteDialog = true
                                    }
                                }) {
                                    AppIcon(
                                        icon = Icons.Default.Delete,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
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
                                DropdownMenuItem(
                                    text = { AppText("Margin") },
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
                        TabLayout(
                            pagerState = pagerState,
                            tabs = tabs,
                        )

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize(),
                        ) { page ->
                            when (page) {
                                0 ->
                                    SalesBillItemsBody(
                                        sharedViewModel = sharedViewModel,
                                        salesViewModel = salesViewModel,
                                        salesItemsViewModel = salesItemsViewModel,
                                        snackbarHostState = snackbarHostState,
                                        onAddButtonClick = { updateId, isUpdated, sales ->
                                            onAddUpdateSalesItem(updateId, isUpdated, sales)
                                        },
                                        onBack = onBack,
                                    )

                                1 ->
                                    AddUpdateSalesBillBody(
                                        sharedViewModel = sharedViewModel,
                                        salesViewModel = salesViewModel,
                                        salesItemsViewModel = salesItemsViewModel,
                                        snackbarHostState = snackbarHostState,
                                        isPendingBill = isPendingBill,
                                        isPostedBill = isPostedBill,
                                    )
                            }
                        }
                    }
                }

                // Save & Post Buttons
                if (salesState.hasLoadedOnce) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                    ) {
                        if (!isPostedBill) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if(salesItemsState.list.isNotEmpty()) 0.5f else 1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (salesState.isSaving) {
                                    AppCircularProgressIndicator()
                                } else {
                                    SaveButton(text = "Save") {
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
                                        salesViewModel.postBill {
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

            if (salesState.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}
