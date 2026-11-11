package com.graphees.statspos.presentation.ui.screens.purchase.purchase_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.graphees.statspos.domain.models.purchase.PurchaseOrderVoucher
import com.graphees.statspos.domain.models.purchase.PurchaseOrders
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.DeleteIconButton
import com.graphees.statspos.presentation.ui.components.EditIconButton
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.FilterIcon
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.SearchTextbox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.purchase.purchase_orders.PurchaseOrdersViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.serialization.Serializable
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdatePurchaseOrder(val updateId: Long, val isUpdate: Boolean) : Routes()

    data class OrderItems(val purchaseOrderId:Long) : Routes()
}

@Composable
fun PurchaseOrdersScreen(
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
                    onAddButtonClick = { updateId, isUpdated ->
                        navigate(Routes.AddUpdatePurchaseOrder(updateId, isUpdated))
                    },
                    onOrderItemsClick = { orderId ->
                        navigate(Routes.OrderItems(orderId))
                    },
                    onBack = onBack,
                )
            }
            entry<Routes.AddUpdatePurchaseOrder> { key ->
                AddUpdatePurchaseOrderScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.OrderItems> { key ->
                PurchaseOrderItemsScreen (
                    sharedViewModel = sharedViewModel,
                    purchaseOrderId = key.purchaseOrderId,
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
    onAddButtonClick: (Long, Boolean) -> Unit,
    onOrderItemsClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<PurchaseOrdersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }
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

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            viewModel.loadData()
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
            text = "Are you sure to delete this Order",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Order deleted successfully")
                }
            }
        )
    }

    fun showVoucher(
        purchaseOrder: List<PurchaseOrderVoucher>,
    ) {
        val file = purchaseOrderVoucher(
            context = context,
            purchaseOrder = purchaseOrder,
        )

        openPdf(context, file)
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                onAddButtonClick(0L, false)
            }
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Purchase Orders",
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
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    SearchBox {
                        SearchBox(
                            value = state.search,
                            onValueChange = viewModel::onSearchChange,
                            onSearchClick = {
                                viewModel.loadData()
                                keyboardController?.hide()
                            },
                            fromDate = state.fromDate,
                            toDate = state.toDate,
                            onFromDateChange = { date ->
                                viewModel.onFromDateChange(date)
                            },
                            onToDateChange = { date ->
                                viewModel.onToDateChange(date)
                            },
                        )
                    }
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { purchaseOrder ->
                            onOrderItemsClick(purchaseOrder.id!!)
                        },
                        onPrintClick = { purchaseOrder ->
                            viewModel.getOrder(
                                orderId = purchaseOrder.id!!,
                                onSuccess = { order ->
                                    showVoucher(order)
                                }
                            )
                        },
                        onEditClick = { purchaseOrder ->
                            onAddButtonClick(purchaseOrder.id!!, true)
                        },
                        onDeleteClick = { purchaseOrder ->
                            selectedId = purchaseOrder.id!!
                            showDeleteDialog = true
                        },
                    )
                }

                BottomHeading(
                    text = "Total Orders: ",
                    value = state.totalPurchaseOrders.toString()
                )
            }
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchTextbox(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            onEndIconClick = {
                onValueChange("")
            },
            onSearchClick = onSearchClick,
        )
    }
    Spacer(Modifier.height(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        DateTextbox(
            modifier = Modifier
                .weight(1f),
            date = fromDate,
            onDateChange = onFromDateChange,
            label = "From Date"
        )
        Spacer(Modifier.width(8.dp))
        DateTextbox(
            modifier = Modifier
                .weight(1f),
            date = toDate,
            onDateChange = onToDateChange,
            label = "To Date"
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<PurchaseOrders>,
    onItemClick: (PurchaseOrders) -> Unit,
    onPrintClick: (PurchaseOrders) -> Unit,
    onEditClick: (PurchaseOrders) -> Unit,
    onDeleteClick: (PurchaseOrders) -> Unit,
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
                onPrintClick = onPrintClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: PurchaseOrders,
    onItemClick: (PurchaseOrders) -> Unit,
    onPrintClick: (PurchaseOrders) -> Unit,
    onEditClick: (PurchaseOrders) -> Unit,
    onDeleteClick: (PurchaseOrders) -> Unit,
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // region Name
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier
                            .weight(1f),
                        text = item.purchaseOrderName.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.width(8.dp))
                    EditIconButton {
                        onEditClick(item)
                    }
                }
                // endregion

                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.2f)
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListHeading("Total: ", color = primaryColor)
                            ListLabel(HP.formatDecimal(item.total), color = secondaryColor)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListHeading("Remarks: ", color = primaryColor)
                            ListLabel(item.remarks.toString(), color = secondaryColor)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    AppIconButton(
                        icon = Icons.Default.Print,
                        onClick = {
                            onPrintClick(item)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    if (HP.userRights.deleteAnything == true) {
                        Spacer(Modifier.width(8.dp))
                        DeleteIconButton {
                            onDeleteClick(item)
                        }
                    }
                }
            }
        }
    }
}

