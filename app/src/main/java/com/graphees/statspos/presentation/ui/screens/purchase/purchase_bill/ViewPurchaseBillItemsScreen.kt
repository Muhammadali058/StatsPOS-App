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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
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
import com.graphees.statspos.domain.models.purchase.PurchaseBill
import com.graphees.statspos.domain.models.purchase.PurchaseBillItems
import com.graphees.statspos.domain.models.purchase.PurchaseBills
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainHeading
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.purchase.main_screen.purchaseBillVoucher
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.ui.utils.sharePdf
import com.graphees.statspos.presentation.viewmodels.purchase.purchase_bill.PurchaseItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.SocketManager
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun ViewPurchaseBillItemsScreen(
    purchaseBill: PurchaseBills,
    isPostedBill: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<PurchaseItemsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var shareBill by remember { mutableStateOf(false) }
    var showPrintPasswordDialog by remember { mutableStateOf(false) }
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

    fun loadData() {
        viewModel.loadData({ a -> })
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            viewModel.updateInitialState(purchaseBill.id!!, isPostedBill)
            loadData()
            hasLoadedOnce = true
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

    fun showBill(
        bill: List<PurchaseBill>,
    ) {
        val file = purchaseBillVoucher(
            context = context,
            bill = bill,
        )

        if (shareBill)
            sharePdf(context, file)
        else
            openPdf(context, file)
    }

    fun printBill() {
        purchaseBill.run {
            if (shareBill) {
                viewModel.getBill(
                    invoiceId = id!!,
                    billType = if (isPostedBill) 1 else 2,
                    isPendingBill = !isPostedBill,
                ) { bill ->
                    showBill(bill)
                }
            } else {
                if (HP.appSettings.onlinePrints == true) {
                    SocketManager.printPurchaseBill(
                        invoiceId = id!!,
                        isPendingBill = !isPostedBill,
                        billType = if (isPostedBill) 1 else 2
                    )
                } else {
                    viewModel.getBill(
                        invoiceId = id!!,
                        billType = if (isPostedBill) 1 else 2,
                        isPendingBill = !isPostedBill,
                    ) { bill ->
                        showBill(bill)
                    }
                }
            }
        }
    }

    if (showPrintPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.PRINT_DUPLICATES,
            onDismiss = {
                showPrintPasswordDialog = false
            },
            onConfirm = {
                showPrintPasswordDialog = false
                printBill()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Total: ${HP.formatDecimal(state.totalBill, mustDecimals = 1)}",
                actions = {
                    Row {
                        if (HP.userRights.printDuplicates == true) {
                            IconButton(onClick = {
                                if (HP.adminPasswords.usePrintDuplicates == true) {
                                    shareBill = false
                                    showPrintPasswordDialog = true
                                }else{
                                    shareBill = false
                                    printBill()
                                }
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Print,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            IconButton(onClick = {
                                if (HP.adminPasswords.usePrintDuplicates == true) {
                                    shareBill = true
                                    showPrintPasswordDialog = true
                                }else{
                                    shareBill = true
                                    printBill()
                                }
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Share,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                        }
                    }
                },
            )
        },
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
                    SearchBox(
                        value = state.search,
                        onValueChange = {
                            viewModel.onSearchChange(it, {})
                        },
                        onSearchClick = {
                            loadData()
                            keyboardController?.hide()
                        },
                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            loadData()
                        },
                        items = state.list,
                        onItemClick = { salesBillItem ->

                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
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
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        item{
            Spacer(Modifier.height(4.dp))
        }
        items(items) { item ->
            ListCard(item = item) {
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun ListCard1(
    modifier: Modifier = Modifier,
    item: PurchaseBillItems,
    onItemClick: (PurchaseBillItems) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        shape = RoundedCornerShape(6.dp),
        onClick = {
            onItemClick(item)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                Text(
                    modifier = modifier,
                    text = item.itemname.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ListHeading(text = "Total: ")
                    ListLabel(text = HP.formatDecimal(item.total))
                }

                Spacer(Modifier.height(4.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        ListHeading(text = "Qty: ", color = primaryColor)
                        ListLabel(text = HP.formatDecimal(item.qty), color = secondaryColor)
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        ListHeading(text = "Cost: ", color = primaryColor)
                        ListLabel(text = HP.formatDecimal(item.finalCost), color = secondaryColor)
                    }
                }
                if (HP.settings.saleCartons == true) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(.7f)
                        ) {
                            ListHeading(text = "Crtn: ", color = primaryColor)
                            ListLabel(text = item.crtn.toString(), color = secondaryColor)
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            ListHeading(text = "Cost Crtn: ", color = primaryColor)
                            ListLabel(text = if(item.crtn!! != 0) HP.formatDecimal((item.finalCost!! * item.crtnSize!!)) else "0", color = secondaryColor)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ListHeading(text = "Gross Cost: ", color = primaryColor)
                    ListLabel(text = HP.formatDecimal(item.cost), color = secondaryColor)
                    Spacer(Modifier.width(8.dp))
                    ListHeading(text = "Disc: ", color = primaryColor)
                    ListLabel(text = HP.formatDecimal(item.calculatedDisc), color = secondaryColor)
                    Spacer(Modifier.width(8.dp))
                    ListHeading(text = "Tax: ", color = primaryColor)
                    ListLabel(text = HP.formatDecimal(item.tax), color = secondaryColor)
                }
            }
        }
    }
}



@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: PurchaseBillItems,
    onItemClick: (PurchaseBillItems) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        shape = RoundedCornerShape(6.dp),
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

                if(HP.settings.saleCartons == true){
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


//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                ) {
//                    ListHeading(
//                        text = "Qty",
//                        Modifier.weight(1f),
//                        color = primaryColor,
//                    )
//                    if (HP.settings.saleCartons == true) {
//                        ListHeading(
//                            text = "Crtn",
//                            Modifier.weight(1f),
//                            color = primaryColor,
//                        )
//                    }
//                    ListHeading(
//                        text = "Cost",
//                        Modifier.weight(1f),
//                        color = primaryColor,
//                    )
//                    if (HP.settings.saleCartons == true) {
//                        ListHeading(
//                            text = "Cost Crtn",
//                            Modifier.weight(1f),
//                            color = primaryColor,
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                ) {
//                    ListLabel(
//                        text = HP.formatDecimal(item.qty),
//                        Modifier.weight(1f),
//                        color = secondaryColor,
//                    )
//                    if (HP.settings.saleCartons == true) {
//                        ListLabel(
//                            text = item.crtn.toString(),
//                            Modifier.weight(1f),
//                            color = secondaryColor,
//                        )
//                    }
//                    ListLabel(
//                        text = HP.formatDecimal(item.finalCost),
//                        Modifier.weight(1f),
//                        color = secondaryColor,
//                    )
//                    if (HP.settings.saleCartons == true) {
//                        ListLabel(
//                            text = if (item.crtn!! != 0) HP.formatDecimal((item.finalCost!! * item.crtnSize!!)) else "0",
//                            Modifier.weight(1f),
//                            color = secondaryColor,
//                        )
//                    }
//                }

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


