package com.example.statspos.presentation.ui.screens.sales.sales_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Outbox
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import com.example.statspos.R
import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppDropdownMenu
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.ChipsRow
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.DropdownItem
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PrimaryButton
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SecondaryButton
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_orders.SalesOrdersViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class ViewOrderItems(val salesOrderId: Long) : Routes()

    @Serializable
    data object Login : Routes()
}

@Composable
fun SalesOrdersScreen(
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
                    onViewOrderItemsClick = { salesOrderId ->
                        navigate(Routes.ViewOrderItems(salesOrderId))
                    },
                    onLogin = {
                        navigate(Routes.Login)
                    },
                )
            }
            entry<Routes.ViewOrderItems> { key ->
                SalesOrderItemsScreen(
                    salesOrderId = key.salesOrderId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.Login> { key ->
                SalesOrdersLoginScreen(
                    onLogin = {
                        backStack.removeLastOrNull()
                        onBack()
                    },
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
    onViewOrderItemsClick: (Long) -> Unit,
    onLogin: () -> Unit,
) {
    val viewModel = hiltViewModel<SalesOrdersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

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

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            if (!viewModel.isUserLoggedIn()) {
                onLogin()
            }
            viewModel.setHasLoadedOnce(true)
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
                    onBack()
                },
                title = "Sales Orders",
                actions = {
                    Row {
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
                                text = "Sign Out",
//                            icon = {
//                                AppIcon(R.drawable.linked, size = 20.dp)
//                            },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.signOut {
                                        onBack()
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    Spacer(Modifier.height(12.dp))
                    ChipsRow(
                        modifier = Modifier,
                        items = state.statusList,
                        selectedItem = state.selectedStatus,
                        onItemSelected = { item ->
                            viewModel.onSelectedStatusChange(item)
                        },
                        addNone = false,
                    )

                    if (state.selectedStatus.name.equals("delivered", ignoreCase = true)) {
                        Spacer(Modifier.height(16.dp))
                        DateTextbox(
                            modifier = Modifier
                                .fillMaxWidth(),
                            date = state.date,
                            onDateChange = viewModel::onDateChange,
                            label = "Date"
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                Column(
                    Modifier
                        .weight(1f)
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (state.orders.isNotEmpty()) {
                        OrdersList(
                            modifier = Modifier
                                .weight(1f),
                            items = state.orders,
                            updatingOrderId = state.updatingOrderId,
                            onClick = { salesOrder ->
                                onViewOrderItemsClick(salesOrder.id!!)
                            },
                            onAccept = { salesOrder ->
                                viewModel.onAccept(salesOrder.id!!)
                            },
                            onDelivered = { salesOrder ->
                                viewModel.onDelivered(salesOrder.id!!)
                            },
                            onCancel = { salesOrder ->
                                viewModel.onCancel(salesOrder.id!!)
                            },
                        )
                    } else {
                        Text("No order found")
                    }
                }

                BottomHeading(
                    text = "Orders: ",
                    value = state.orders.size.toString()
                )

                if (state.isLoading) {
                    ProgressBarLayout()
                }
            }
        }
    }
}

@Composable
private fun OrdersList(
    modifier: Modifier = Modifier,
    items: List<SalesOrders>,
    updatingOrderId: Long? = null,
    onClick: (SalesOrders) -> Unit,
    onAccept: (SalesOrders) -> Unit,
    onDelivered: (SalesOrders) -> Unit,
    onCancel: (SalesOrders) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(items) { item ->
            OrderCard(
                item = item,
                updatingStatus = updatingOrderId == item.id,
                onClick = onClick,
                onAccept = onAccept,
                onDelivered = onDelivered,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
fun OrderCard(
    item: SalesOrders,
    updatingStatus: Boolean,
    onClick: (SalesOrders) -> Unit,
    onAccept: (SalesOrders) -> Unit,
    onDelivered: (SalesOrders) -> Unit,
    onCancel: (SalesOrders) -> Unit,
) {
    ListCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        onClick = {
            onClick(item)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            )
            {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(
                        icon = Icons.Default.Outbox,
                        size = 28.dp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            HeadingMedium(
                                text = item.accountName.toString()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row {
                                HeadingMedium(
                                    text = "Order Id: "
                                )
//                                Spacer(Modifier.height(4.dp))
                                LabelMedium(
                                    text = "#${item.id.toString()}"
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row {
                                HeadingMedium(
                                    text = "Value: "
                                )
                                LabelMedium(
                                    text = "${item.totalBill!! + item.deliveryCharges!!}"
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Create on",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                            )
                            Text(
                                text = HP.getFormatedDate(HP.toLocalDate(item.date.toString())),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            if (updatingStatus) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    AppCircularProgressIndicator(
                                        modifier = Modifier
                                            .scale(.8f)
                                    )
                                }
                            } else {
                                if (item.status.equals("new", ignoreCase = true)) {
                                    PrimaryButton(
                                        text = "Accept",
                                        onClick = {
                                            onAccept(item)
                                        },
                                    )
                                }

                                if (item.status.equals("processing", ignoreCase = true)) {
                                    PrimaryButton(
                                        text = "Delivered",
                                        onClick = {
                                            onDelivered(item)
                                        },
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                if (!item.status.equals(
                                        "cancelled",
                                        ignoreCase = true
                                    ) && !item.status.equals("delivered", ignoreCase = true)
                                ) {
                                    SecondaryButton(
                                        text = "Cancel",
                                        onClick = {
                                            onCancel(item)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}