package com.graphees.statspos.presentation.ui.screens.shopping_app.sales_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.sales.SalesOrderItems
import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.PrimaryButton
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.SecondaryButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openGoogleMaps
import com.graphees.statspos.presentation.ui.utils.shareLocationOnWhatsApp
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.shopping_app.sales_orders.SalesOrderItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@Composable
fun SalesOrderItemsScreen(
    sharedViewModel: SharedViewModel,
    salesOrder: SalesOrders,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SalesOrderItemsViewModel>()
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

    // Load data initially
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.loadOrderItems(salesOrder.id!!)
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
                title = "Order Details",
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                openGoogleMaps(context, salesOrder.latitude!!, salesOrder.longitude!!)
                            }
                        ) {
                            AppIcon(
                                icon = Icons.Default.LocationOn,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        IconButton(
                            onClick = {
                                shareLocationOnWhatsApp(context, salesOrder.latitude!!, salesOrder.longitude!!)
                            }
                        ) {
                            AppIcon(
                                icon = Icons.Default.ShareLocation,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
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
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(12.dp))
                Column(
                    Modifier
                        .weight(1f)
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ItemsList(
                        modifier = Modifier
                            .weight(1f),
                        items = state.orderItems,
                    )
                }

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    if (state.isGeneratingBill) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton(
                            text = "Generate Bill",
                        ) {
                            viewModel.generateBill(salesOrder.id!!) {
                                sharedViewModel.notifyBillPosted()
                                context.showToast("Bill generated goto sales")
                                onBack()
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
private fun ItemsList(
    modifier: Modifier = Modifier,
    items: List<SalesOrderItems>,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
            )
        }
    }
}

@Composable
fun ItemCard(
    item: SalesOrderItems,
) {
    ListCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        onClick = {

        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                // Image
                ListImageView(
                    imageUrl = item.imageUrl,
                    modifier = Modifier
                        .size(70.dp),
                )
                Spacer(Modifier.width(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                ) {
                    // region Itemname & Delete icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = item.itemname!!,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            ),
                        )
                    }
                    // endregion

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        // region Price
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = "Rs. ",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                )
                                Text(
                                    text = HP.formatDecimal(item.rate!! * item.qty!!),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Rs. ${HP.formatDecimal(item.marketPrice!! * item.qty!!)}",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        textDecoration = TextDecoration.LineThrough,
                                    ),
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primaryFixed)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryFixed,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Saved Rs. ${HP.formatDecimal((item.marketPrice!! * item.qty!!) - (item.rate!! * item.qty!!))}",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryFixed,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                )
                            }
                        }
                        // endregion

                        // region Qty Stepper
                        Spacer(Modifier.width(8.dp))
                        Row(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.onSecondaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = item.qty.toString(),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp),
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                        // endregion
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
private fun OrderCard(
    salesOrder: SalesOrders,
) {
    ListCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
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
                                text = salesOrder.accountName.toString()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row {
                                HeadingMedium(
                                    text = "Order Id: "
                                )
                                LabelMedium(
                                    text = "#${salesOrder.id.toString()}"
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row {
                                HeadingMedium(
                                    text = "Value: "
                                )
                                LabelMedium(
                                    text = "${salesOrder.totalBill!! + salesOrder.deliveryCharges!!}"
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
                                text = HP.getFormatedDate(HP.toLocalDate(salesOrder.date.toString())),
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
                            text = salesOrder.address!!
                        )
                    }
                }
            }
        }
    }
}