package com.graphees.statspos.presentation.ui.screens.shopping_app.sales_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppDropdownMenu
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.ChipsRow
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.DropdownItem
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.PrimaryButton
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SecondaryButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.shopping_app.sales_orders.SalesOrdersViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun SalesOrdersScreen(
    onViewOrderItemsClick: (SalesOrders) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = hiltViewModel<SalesOrdersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }

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
                        Row(
                            Modifier
                                .fillMaxWidth(),
                        ) {
                            DateTextbox(
                                modifier = Modifier
                                    .weight(1f),
                                date = state.fromDate,
                                onDateChange = viewModel::onFromDateChange,
                                label = "From Date"
                            )
                            Spacer(Modifier.width(8.dp))
                            DateTextbox(
                                modifier = Modifier
                                    .weight(1f),
                                date = state.toDate,
                                onDateChange = viewModel::onToDateChange,
                                label = "To Date"
                            )
                        }
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
                                onViewOrderItemsClick(salesOrder)
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
private fun OrderCard(
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

                    Spacer(Modifier.height(8.dp))
                    Column{
                        HeadingMedium(
                            text = "Address: "
                        )
                        Spacer(Modifier.height(2.dp))
                        LabelMedium(
                            text = item.address!!
                        )
                    }
                    Spacer(Modifier.height(8.dp))

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