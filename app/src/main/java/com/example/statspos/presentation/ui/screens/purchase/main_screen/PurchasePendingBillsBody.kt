package com.example.statspos.presentation.ui.screens.purchase.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.purchase.PurchaseBill
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.models.sales.SalesBill
import com.example.statspos.domain.models.sales.SalesBills
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingLarge
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.ListHeading
import com.example.statspos.presentation.ui.components.ListLabel
import com.example.statspos.presentation.ui.components.ListMainLabel
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.screens.sales.main_screen.salesBillVoucher
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.purchase.main_screen.PurchasePendingBillsViewModel
import com.example.statspos.presentation.viewmodels.sales.main_screen.SalesPendingBillsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.SocketManager
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun PurchasePendingBillsBody(
    sharedViewModel: SharedViewModel,
    onAddUpdateButtonClick: (Long, Boolean, PurchaseBills?) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<PurchasePendingBillsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showPrintPasswordDialog by remember { mutableStateOf(false) }
    var bill by remember { mutableStateOf<PurchaseBills?>(null) }
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
    LaunchedEffect(sharedViewModelState.billSaved, sharedViewModelState.billPosted) {
        if (sharedViewModelState.billSaved || sharedViewModelState.billPosted) {
            viewModel.loadData()
            sharedViewModel.consumeBillSaved()
        }
    }

    // When branch changed
    LaunchedEffect(sharedViewModelState.refreshPurchaseScreen) {
        if (sharedViewModelState.refreshPurchaseScreen) {
            viewModel.loadData()
            sharedViewModel.consumeRefreshPurchaseScreen()
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

        openPdf(context, file)
    }

    fun printBill() {
        bill?.run {
            if (HP.appSettings.onlinePrints == true) {
                SocketManager.printPurchaseBill(
                    invoiceId = id!!,
                    isPendingBill = true,
                    billType = 2
                )
            } else {
                viewModel.getBill(id!!) { bill->
                    showBill(bill)
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
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        floatingActionButton = {
            AppFloatingActionButton {
                viewModel.makeNewBill { invoiceId ->
                    sharedViewModel.notifyBillSaved()
                    onAddUpdateButtonClick(invoiceId, true, null)
                }
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
                    SearchBox(
                        value = state.search,
                        onValueChange = viewModel::onSearchChange,
                        onSearchClick = {
                            viewModel.loadData()
                            keyboardController?.hide()
                        },
                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { purchaseBill ->
                            onAddUpdateButtonClick(purchaseBill.id!!, true, purchaseBill)
                        },
                        onPrintClick = { purchaseBill ->
                            if (HP.adminPasswords.usePrintDuplicates == true) {
                                bill = purchaseBill
                                showPrintPasswordDialog = true
                            } else {
                                bill = purchaseBill
                                printBill()
                            }
                        }
                    )
                }

                BottomHeading(
                    text = "Total Bills: ",
                    value = state.totalBills.toString()
                )
            }
        }
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<PurchaseBills>,
    onItemClick: (PurchaseBills) -> Unit,
    onPrintClick: (PurchaseBills) -> Unit,
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
            ListCard(
                item = item,
                onItemClick = onItemClick,
                onPrintClick = onPrintClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: PurchaseBills,
    onItemClick: (PurchaseBills) -> Unit,
    onPrintClick: (PurchaseBills) -> Unit,
) {
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
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ListHeading("Bill No. ")
                    ListLabel(item.id.toString())
                    Spacer(Modifier.width(8.dp))
                    ListMainLabel(item.vendorName.toString().ifEmpty { "Vendor not selected" })
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ListHeading("Date: ")
                    ListLabel(item.date.toString())
                }
                if(item.warehouseName.toString().isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ListHeading("Warehouse: ")
                        ListLabel(item.warehouseName.toString())
                    }
                }
            }
            if(HP.userRights.printDuplicates == true) {
                Spacer(Modifier.width(8.dp))
                Column {
                    AppIconButton(
                        icon = Icons.Default.Print,
                        onClick = {
                            onPrintClick(item)
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ListHeading("Total", Modifier.weight(1f))
            ListHeading("On", Modifier.weight(.5f))
            ListHeading("Type", Modifier.weight(.5f))
            ListHeading("MOP", Modifier.weight(.5f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ListLabel(HP.formatDecimal((item.grossTotal!! - item.totalDisc!!)), Modifier.weight(1f))
            ListLabel(item.purchaseOn.toString(), Modifier.weight(.5f))
            ListLabel(item.purchaseType.toString(), Modifier.weight(.5f))
            ListLabel(item.mop.toString(), Modifier.weight(.5f))
        }
    }
}

