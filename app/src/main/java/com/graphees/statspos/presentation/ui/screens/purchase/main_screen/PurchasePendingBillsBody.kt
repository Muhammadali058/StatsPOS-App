package com.graphees.statspos.presentation.ui.screens.purchase.main_screen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.purchase.PurchaseBill
import com.graphees.statspos.domain.models.purchase.PurchaseBills
import com.graphees.statspos.domain.models.sales.SalesBills
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainHeading
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PrintIcon
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.UpgradeToPremiumBottomSheet
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.purchase.main_screen.PurchasePendingBillsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.SocketManager
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@Composable
fun PurchasePendingBillsBody(
    sharedViewModel: SharedViewModel,
    onAddUpdateButtonClick: (Long, Boolean, PurchaseBills?) -> Unit,
    onUpgradeClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<PurchasePendingBillsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showPrintPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var bill by remember { mutableStateOf<PurchaseBills?>(null) }
    var showUpgradeToPremiumSheet by remember { mutableStateOf(false) }

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

                if(state.error!!.contains("upgrade to premium", ignoreCase = true))
                    showUpgradeToPremiumSheet = true
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

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this bill",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                bill?.run {
                    viewModel.deleteBill(id!!) {
                        context.showToast("Bill deleted successfully")
                        viewModel.loadData()
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
                        },
                        onDeleteClick = { purchaseBill ->
                            bill = purchaseBill
                            showDeleteDialog = true
                        },
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
    onDeleteClick: (PurchaseBills) -> Unit,
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
                onDeleteClick = onDeleteClick,
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
    onDeleteClick: (PurchaseBills) -> Unit,
    onPrintClick: (PurchaseBills) -> Unit,
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
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            modifier = modifier,
                            text = item.vendorName.toString().ifEmpty { "Vendor not selected" },
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ListHeading("Total: ")
                            ListLabel(HP.formatDecimal((item.grossTotal!! - item.totalDisc!!)))
                        }
                    }

                    if (HP.userRights.printDuplicates == true) {
                        Spacer(Modifier.width(8.dp))
                        PrintIcon {
                            onPrintClick(item)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = "Bill No",
                        Modifier.weight(0.5f),
                        color = primaryColor
                    )
                    ListHeading(
                        text = "User",
                        Modifier.weight(1f),
                        color = primaryColor
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListLabel(
                        text = item.id.toString(),
                        Modifier.weight(0.5f),
                        color = secondaryColor
                    )
                    ListLabel(
                        text = item.username.toString(),
                        Modifier.weight(1f),
                        color = secondaryColor
                    )
                }

            }
        }

        Spacer(Modifier.height(8.dp))
        ListHorizontalDivider()
        Spacer(Modifier.height(8.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ListHeading("Date: ")
                ListLabel(item.date.toString())
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListHeading("On", Modifier.weight(1f), color = primaryColor)
                        ListHeading("Type", Modifier.weight(1f), color = primaryColor)
                        ListHeading("MOP", Modifier.weight(1f), color = primaryColor)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListLabel(
                            item.purchaseOn.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                        ListLabel(
                            item.purchaseType.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                        ListLabel(
                            item.mop.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))
                DeleteIcon {
                    onDeleteClick(item)
                }
            }
        }
    }
}