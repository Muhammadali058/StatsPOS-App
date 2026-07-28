package com.graphees.statspos.presentation.ui.screens.sales.main_screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.domain.models.sales.SalesBill
import com.graphees.statspos.domain.models.sales.SalesBills
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.FilterIcon
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainHeading
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.PrintIcon
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.SearchTextbox
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.ui.utils.sharePdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.sales.main_screen.SalesPostedBillsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.SocketManager
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesPostedBillsBody(
    sharedViewModel: SharedViewModel,
    onViewClick: (SalesBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, SalesBills?) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<SalesPostedBillsViewModel>()
//    val salesViewModel = hiltViewModel<AddUpdateSalesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showEditPasswordDialog by remember { mutableStateOf(false) }
    var showDeletePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrintPasswordDialog by remember { mutableStateOf(false) }
    var bill by remember { mutableStateOf<SalesBills?>(null) }
    var shareBill by remember { mutableStateOf(false) }
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
    LaunchedEffect(sharedViewModelState.billPosted) {
        if (sharedViewModelState.billPosted) {
            viewModel.loadData()
            sharedViewModel.consumeBillPosted()
        }
    }

    // When branch changed
    LaunchedEffect(sharedViewModelState.refreshSalesScreen) {
        if (sharedViewModelState.refreshSalesScreen) {
            viewModel.loadData()
            sharedViewModel.consumeRefreshSalesScreen()
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

    if (showEditPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.EDIT_SALES_BILL,
            onDismiss = {
                showEditPasswordDialog = false
            },
            onConfirm = {
                showEditPasswordDialog = false
                bill?.run {
                    onAddUpdateButtonClick(id!!, true, this)
                }
            }
        )
    }

    if (showDeletePasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_SALES_BILL,
            onDismiss = {
                showDeletePasswordDialog = false
            },
            onConfirm = {
                showDeletePasswordDialog = false
                bill?.run {
                    viewModel.deleteBill(id!!) {
                        context.showToast("Bill deleted successfully")
                        viewModel.loadData()
                    }
                }
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

    fun editBill(salesBills: SalesBills) {
        onAddUpdateButtonClick(salesBills.id!!, true, salesBills)
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

        if (shareBill)
            sharePdf(context, file)
        else
            openPdf(context, file)
    }

    fun printBill() {
        bill?.run {
            if (shareBill) {
                viewModel.getBill(id!!) { bill, ledger ->
                    showBill(bill, ledger)
                }
            } else {
                if (HP.appSettings.onlinePrints == true) {
                    SocketManager.printSalesBill(
                        invoiceId = id!!,
                        isPendingBill = false,
                        billType = 2
                    )
                } else {
                    viewModel.getBill(id!!) { bill, ledger ->
                        showBill(bill, ledger)
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
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                modifier = Modifier
                    .fillMaxWidth(),
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                },
            ) {
                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.salesPostedBillsSearchBy,
                    selectedItem = state.searchBy,
                    onItemSelected = viewModel::onSearchByChange,
                    label = {
                        Text(text = "Search By")
                    },
                    outlined = true,
                )
                if (HP.user.userType == 1) {
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(),
                        items = HP.users,
                        selectedItem = state.user,
                        onItemSelected = viewModel::onUserChange,
                        label = {
                            Text(text = "User")
                        },
                        addNone = true,
                        outlined = true,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        items = HP.salesType,
                        selectedItem = state.salesType,
                        onItemSelected = { item ->
                            viewModel.onSalesTypeChange(item)
                        },
                        label = {
                            Text(text = "Sales Type")
                        },
                        addNone = true,
                        noneText = "Both",
                        showEndIcon = false,
                        outlined = true,
                    )
                    Spacer(Modifier.width(8.dp))
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(),
                        items = HP.salesOn,
                        selectedItem = state.salesOn,
                        onItemSelected = { item ->
                            viewModel.onSalesOnChange(item)
                        },
                        label = {
                            Text(text = "Sales On")
                        },
                        addNone = true,
                        noneText = "Both",
                        showEndIcon = false,
                        outlined = true,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        items = HP.mop,
                        selectedItem = state.mop,
                        onItemSelected = { item ->
                            viewModel.onMOPChange(item)
                        },
                        label = {
                            Text(text = "M.O.P")
                        },
                        addNone = true,
                        noneText = "Both",
                        showEndIcon = false,
                        outlined = true,
                    )
                    Spacer(Modifier.width(8.dp))
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(),
                        items = HP.salesRetailType,
                        selectedItem = state.salesRetailType,
                        onItemSelected = { item ->
                            viewModel.onSalesRetailTypeChange(item)
                        },
                        label = {
                            Text(text = "Type")
                        },
                        addNone = true,
                        noneText = "Both",
                        showEndIcon = false,
                        outlined = true,
                    )
                }
                Button(onClick = {
                    viewModel.loadData()
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Apply")
                }
            }
        }

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
                    SearchBox {
                        SearchBox(
                            value = state.search,
                            onValueChange = viewModel::onSearchChange,
                            onSearchClick = {
                                viewModel.loadData()
                                keyboardController?.hide()
                            },
                            onFilterClick = {
                                showBottomSheet = true
                            },
                            fromDate = state.fromDate,
                            toDate = state.toDate,
                            onFromDateChange = { date ->
                                viewModel.onFromDateChange(date)
                                viewModel.loadData()
                            },
                            onToDateChange = { date ->
                                viewModel.onToDateChange(date)
                                viewModel.loadData()
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
                        isLoadingNextPage = state.isLoadingNextPage,
                        endReached = state.endReached,
                        loadNextItems = {
                            viewModel.loadNextItems()
                        },
                        items = state.list,
                        onEditClick = { salesBills ->
                            // If bill type is sale not return
                            if (salesBills.salesType == "Sales") {
                                // if user has access to edit bill
                                if (HP.userRights.editSaleBill == true) {
                                    val today = LocalDate.now()
                                    val billDate = HP.toLocalDateTime(salesBills.localDate!!)
                                    val isToday = billDate.isEqual(today)

                                    // if bill on credit
                                    if (salesBills.salesOn == "Credit") {
                                        if (HP.userRights.editCreditBill == true) {

                                            if (isToday && HP.settings.sameDateBillEdit == true) {
                                                editBill(salesBills)
                                            } else if (isToday) {
                                                if (HP.passwords.useEditSalesBill == true) {
                                                    bill = salesBills
                                                    showEditPasswordDialog = true
                                                } else {
                                                    editBill(salesBills)
                                                }
                                            } else {
                                                if (HP.settings.editOldCreditBill == true) {
                                                    if (HP.passwords.useEditSalesBill == true) {
                                                        bill = salesBills
                                                        showEditPasswordDialog = true
                                                    } else {
                                                        editBill(salesBills)
                                                    }
                                                }
                                            }

                                        }
                                    } else {
                                        // if bill on cash
                                        if (isToday && HP.settings.sameDateBillEdit == true) {
                                            editBill(salesBills)
                                        } else {
                                            if (HP.passwords.useEditSalesBill == true) {
                                                bill = salesBills
                                                showEditPasswordDialog = true
                                            } else {
                                                editBill(salesBills)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onItemClick = onViewClick,
                        onDeleteClick = { salesBill ->
                            bill = salesBill
                            if (HP.passwords.useDeleteSalesBill == true) {
                                showDeletePasswordDialog = true
                            } else {
                                showDeleteDialog = true
                            }
//                            if (HP.adminPasswords.usePrintDuplicates == true) {
//                                bill = salesBill
//                                shareBill = share
//                                showPrintPasswordDialog = true
//                            } else {
//                                bill = salesBill
//                                shareBill = share
//                                printBill()
//                            }
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
private fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onFilterClick: () -> Unit,
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
        Spacer(Modifier.width(8.dp))
        FilterIcon {
            onFilterClick()
        }
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
    isLoadingNextPage: Boolean,
    endReached: Boolean,
    loadNextItems: () -> Unit,
    items: List<SalesBills>,
    onItemClick: (SalesBills) -> Unit,
    onEditClick: (SalesBills) -> Unit,
    onDeleteClick: (SalesBills) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        isLoadingNextPage = isLoadingNextPage,
    ) {
        item {
            Spacer(Modifier.height(4.dp))
        }
        items(items.size) { i ->
            val item = items[i]

            if (
                i == items.lastIndex &&
                !endReached &&
                !isLoadingNextPage
            ) {
                loadNextItems()
            }

            ListCard(
                item = item,
                onItemClick = onItemClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard1(
    modifier: Modifier = Modifier,
    item: SalesBills,
    onItemClick: (SalesBills) -> Unit,
    onEditClick: (SalesBills) -> Unit,
    onDeleteClick: (SalesBills) -> Unit,
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
                        Text(
                            modifier = modifier,
                            text = item.customerName.toString().ifEmpty { "Walk-in Customer" },
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ListHeading("Sr. ", color = primaryColor)
                            ListLabel(item.id.toString(), color = secondaryColor)
                            Spacer(Modifier.width(8.dp))
                            ListHeading("Inv No. ", color = primaryColor)
                            ListLabel(item.invoiceNo.toString(), color = secondaryColor)
                            Spacer(Modifier.width(8.dp))
                            ListHeading("Date: ", color = primaryColor)
                            ListLabel(item.date.toString(), color = secondaryColor)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ListHeading("User: ", color = primaryColor)
                            ListLabel(item.username.toString(), color = secondaryColor)
                        }
                    }

                    AppIconButton(
                        icon = Icons.Default.Edit,
                        onClick = {
                            onEditClick(item)
                        },
                        buttonSize = 26.dp,
                        size = 20.dp,
                    )
                }

                Spacer(Modifier.height(4.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListMainHeading("Total", Modifier.weight(1f))
                            ListHeading("On", Modifier.weight(.5f), color = primaryColor)
                            ListHeading("Type", Modifier.weight(.5f), color = primaryColor)
                            ListHeading("MOP", Modifier.weight(.5f), color = primaryColor)
                            ListHeading("R/W", Modifier.weight(.5f), color = primaryColor)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListMainLabel(
                                HP.formatDecimal((item.grossTotal!! - item.totalDisc!!)),
                                Modifier.weight(1f)
                            )
                            ListLabel(item.salesOn.toString(), Modifier.weight(.5f), color = secondaryColor)
                            ListLabel(item.salesType.toString(), Modifier.weight(.5f), color = secondaryColor)
                            ListLabel(item.mop.toString(), Modifier.weight(.5f), color = secondaryColor)
                            ListLabel(item.type.toString(), Modifier.weight(.5f), color = secondaryColor)
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    DeleteIcon{
                        onDeleteClick(item)
                    }
                }
            }
        }
    }
}


@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: SalesBills,
    onItemClick: (SalesBills) -> Unit,
    onEditClick: (SalesBills) -> Unit,
    onDeleteClick: (SalesBills) -> Unit,
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
                            text = item.customerName.toString().ifEmpty { "Walk-in Customer" },
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

                    Spacer(Modifier.width(8.dp))
                    AppIconButton(
                        icon = Icons.Default.Edit,
                        onClick = {
                            onEditClick(item)
                        },
                        buttonSize = 26.dp,
                        size = 20.dp,
                    )
                }

                Spacer(Modifier.height(8.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = "Sr.",
                        Modifier.weight(0.5f),
                        color = primaryColor
                    )
                    ListHeading(
                        text = "Inv No.",
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
                        text = item.invoiceNo.toString(),
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
                        ListHeading("R/W", Modifier.weight(1f), color = primaryColor)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListLabel(
                            item.salesOn.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                        ListLabel(
                            item.salesType.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                        ListLabel(
                            item.mop.toString(),
                            Modifier.weight(1f),
                            color = secondaryColor
                        )
                        ListLabel(
                            item.type.toString(),
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