package com.example.statspos.presentation.ui.screens.purchase.purchase_bill

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
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
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
import com.example.statspos.presentation.viewmodels.purchase.purchase_bill.AddUpdatePurchaseViewModel
import com.example.statspos.presentation.viewmodels.purchase.purchase_bill.PurchaseItemsViewModel
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
    data class AddUpdatePurchaseItem(
        val updateId: Long,
        val isUpdate: Boolean,
        val purchase: Purchase
    ) :
        Routes()

    @Serializable
    data object SearchItem : Routes()
}

@Composable
fun PurchaseBillScreen(
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
                    isPendingBill = isPendingBill,
                    isPostedBill = isPostedBill,
                    onAddUpdatePurchaseItem = { updateId, isUpdate, purchase ->
                        navigate(Routes.AddUpdatePurchaseItem(updateId, isUpdate, purchase))
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
        }
    )
}

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    purchaseViewModel: AddUpdatePurchaseViewModel,
    purchaseItemsViewModel: PurchaseItemsViewModel,
    invoiceId: Long = 0L,
    isPendingBill: Boolean = false,
    isPostedBill: Boolean = false,
    onAddUpdatePurchaseItem: (Long, Boolean, Purchase) -> Unit,
    onBack: () -> Unit,
    goBackWithResult: () -> Unit,
) {
    val tabs = listOf("Items", "Details")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val context = LocalContext.current
    val purchaseState by purchaseViewModel.state.collectAsStateWithLifecycle()
    val purchaseItemsState by purchaseItemsViewModel.state.collectAsStateWithLifecycle()
    val event by purchaseViewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

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
                                    if (HP.passwords.useDeletePurchaseBill == true) {
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
                if (purchaseState.hasLoadedOnce) {
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
                                    PurchaseBillItemsBody(
                                        sharedViewModel = sharedViewModel,
                                        purchaseViewModel = purchaseViewModel,
                                        purchaseItemsViewModel = purchaseItemsViewModel,
                                        snackbarHostState = snackbarHostState,
                                        onAddButtonClick = { updateId, isUpdated, sales ->
                                            onAddUpdatePurchaseItem(updateId, isUpdated, sales)
                                        },
                                    )

                                1 ->
                                    AddUpdatePurchaseBillBody(
                                        sharedViewModel = sharedViewModel,
                                        purchaseViewModel = purchaseViewModel,
                                        snackbarHostState = snackbarHostState,
                                    )
                            }
                        }
                    }
                }

                // Save & Post Buttons
                if (purchaseState.hasLoadedOnce) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                    ) {
                        if (!isPostedBill) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (purchaseState.isSaving) {
                                    AppCircularProgressIndicator()
                                } else {
                                    SaveButton(text = "Save") {
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
                                    SaveButton(text = "Post") {
                                        purchaseViewModel.postBill {
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

            if (purchaseState.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}
