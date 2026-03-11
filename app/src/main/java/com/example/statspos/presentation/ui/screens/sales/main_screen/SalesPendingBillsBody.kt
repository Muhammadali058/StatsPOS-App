package com.example.statspos.presentation.ui.screens.sales.main_screen

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
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.main_screen.SalesPendingBillsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun SalesPendingBillsBody(
    sharedViewModel: SharedViewModel,
    onAddUpdateButtonClick: (Long, Boolean, SalesBills?) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SalesPendingBillsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showPrintPasswordDialog by remember { mutableStateOf(false) }
    var bill by remember { mutableStateOf<SalesBills?>(null) }
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

    fun showBill(
        bill: List<SalesBill>,
        ledger: List<AccountReport>?,
    ) {
        val file = salesBillVoucher(
            context = context,
            bill = bill,
            ledger = ledger,
        )

        openPdf(context, file)
    }

    fun printBill() {
        bill?.run {
            viewModel.getBill(id!!) { bill, ledger ->
                showBill(bill, ledger)
            }
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
                    Spacer(Modifier.height(4.dp))
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { salesBills ->
                            onAddUpdateButtonClick(salesBills.id!!, true, salesBills)
                        },
                        onPrintClick = { salesBill ->
                            if (HP.adminPasswords.usePrintDuplicates == true) {
                                bill = salesBill
                                showPrintPasswordDialog = true
                            } else {
                                bill = salesBill
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
    items: List<SalesBills>,
    onItemClick: (SalesBills) -> Unit,
    onPrintClick: (SalesBills) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
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
    item: SalesBills,
    onItemClick: (SalesBills) -> Unit,
    onPrintClick: (SalesBills) -> Unit,
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
                    HeadingMedium("Bill No. ")
                    LabelMedium(item.id.toString())
                    Spacer(Modifier.width(8.dp))
                    LabelLarge(item.customerName.toString())
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeadingMedium("Date: ")
                    LabelLarge(item.date.toString())
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
            HeadingLarge("Total", Modifier.weight(1f))
            HeadingMedium("On", Modifier.weight(.5f))
            HeadingMedium("Type", Modifier.weight(.5f))
            HeadingMedium("MOP", Modifier.weight(.5f))
            HeadingMedium("R/W", Modifier.weight(.5f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            LabelLarge(
                HP.formatDecimal((item.grossTotal!! - item.totalDisc!!)),
                Modifier.weight(1f)
            )
            LabelMedium(item.salesOn.toString(), Modifier.weight(.5f))
            LabelMedium(item.salesType.toString(), Modifier.weight(.5f))
            LabelMedium(item.mop.toString(), Modifier.weight(.5f))
            LabelMedium(item.type.toString(), Modifier.weight(.5f))
        }
    }
}

