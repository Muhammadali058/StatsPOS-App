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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.purchase.PurchaseBills
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingLarge
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.purchase.main_screen.PurchasePostedBillsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasePostedBillsBody(
    sharedViewModel: SharedViewModel,
    onViewClick: (PurchaseBills) -> Unit,
    onAddUpdateButtonClick: (Long, Boolean, PurchaseBills?) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<PurchasePostedBillsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var purchaseBill by remember { mutableStateOf<PurchaseBills?>(null) }
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

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.EDIT_PURCHASE_BILL,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                purchaseBill?.run {
                    onAddUpdateButtonClick(id!!, true, this)
                }
            }
        )
    }

    fun editBill(purchaseBills: PurchaseBills) {
        onAddUpdateButtonClick(purchaseBills.id!!, true, purchaseBills)
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
                    items = HP.purchasePostedBillsSearchBy,
                    selectedItem = state.searchBy,
                    onItemSelected = viewModel::onSearchByChange,
                    label = {
                        Text(text = "Search By")
                    },
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
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        items = HP.purchaseType,
                        selectedItem = state.purchaseType,
                        onItemSelected = { item ->
                            viewModel.onPurchaseTypeChange(item)
                        },
                        label = {
                            Text(text = "Purchase Type")
                        },
                        addNone = true,
                        noneText = "Both",
                    )
                    Spacer(Modifier.width(8.dp))
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(),
                        items = HP.purchaseOn,
                        selectedItem = state.purchaseOn,
                        onItemSelected = { item ->
                            viewModel.onPurchaseOnChange(item)
                        },
                        label = {
                            Text(text = "Purchase On")
                        },
                        addNone = true,
                        noneText = "Both",
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ComboBox(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    Spacer(Modifier.height(8.dp))
                    SearchBox(
                        modifier = Modifier
                            .padding(bottom = 4.dp),
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
                    BodyList(
                        modifier = Modifier
                            .weight(1f),
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
                        onItemClick = { purchaseBills ->
                            // If bill type is sale not return
                            if (purchaseBills.purchaseType == "Purchase") {
                                if (purchaseBills.warehouseId == 0L) {
                                    // if user has access to edit bill
                                    if (HP.userRights.editPurchaseBill == true) {
                                        val today = LocalDate.now()
                                        val billDate = HP.toLocalDate(purchaseBills.localDate!!)
                                        val isToday = billDate.isEqual(today)

                                        // if bill on credit
                                        if (purchaseBills.purchaseOn == "Credit") {
                                            if (HP.userRights.editCreditBill == true) {

                                                if (isToday && HP.settings.sameDateBillEdit == true) {
                                                    editBill(purchaseBills)
                                                } else if (isToday) {
                                                    if (HP.passwords.useEditPurchaseBill == true) {
                                                        purchaseBill = purchaseBills
                                                        showPasswordDialog = true
                                                    } else {
                                                        editBill(purchaseBills)
                                                    }
                                                } else {
                                                    if (HP.settings.editOldCreditBill == true) {
                                                        if (HP.passwords.useEditPurchaseBill == true) {
                                                            purchaseBill = purchaseBills
                                                            showPasswordDialog = true
                                                        } else {
                                                            editBill(purchaseBills)
                                                        }
                                                    }
                                                }

                                            }
                                        } else {
                                            // if bill on cash
                                            if (isToday && HP.settings.sameDateBillEdit == true) {
                                                editBill(purchaseBills)
                                            } else {
                                                if (HP.passwords.useEditPurchaseBill == true) {
                                                    purchaseBill = purchaseBills
                                                    showPasswordDialog = true
                                                } else {
                                                    editBill(purchaseBills)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onViewClick = onViewClick
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    HeadingMedium(
                        text = "Total Bills: ",
                    )
                    LabelMedium(
                        text = state.totalBills.toString(),
                    )
                }
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
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {
                onFilterClick()
            },
            icon = Icons.Default.FilterList,
            buttonSize = 32.dp,
            size = 26.dp
        )
    }
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
    items: List<PurchaseBills>,
    onItemClick: (PurchaseBills) -> Unit,
    onViewClick: (PurchaseBills) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        isLoadingNextPage = isLoadingNextPage,
    ) {
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
                onViewClick = onViewClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: PurchaseBills,
    onItemClick: (PurchaseBills) -> Unit,
    onViewClick: (PurchaseBills) -> Unit,
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
                    LabelLarge(item.vendorName.toString())
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeadingMedium("Sr. ")
                    LabelMedium(item.id.toString())
                    Spacer(Modifier.width(16.dp))
                    HeadingMedium("Inv No. ")
                    LabelMedium(item.invoiceNo.toString())
                    if(item.refInvoiceNo.toString().isNotEmpty()) {
                        Spacer(Modifier.width(16.dp))
                        HeadingMedium("Ref Inv No. ")
                        LabelMedium(item.refInvoiceNo.toString())
                    }
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
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeadingMedium("User: ")
                    LabelLarge(item.username.toString())
                }
                if(item.warehouseName.toString().isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HeadingMedium("Warehouse: ")
                        LabelLarge(item.warehouseName.toString())
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            AppIconButton(
                onClick = {
                    onViewClick(item)
                },
                icon = Icons.Default.List
            )
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            LabelLarge(HP.formatDecimal((item.grossTotal!! - item.totalDisc!!)), Modifier.weight(1f))
            LabelMedium(item.purchaseOn.toString(), Modifier.weight(.5f))
            LabelMedium(item.purchaseType.toString(), Modifier.weight(.5f))
            LabelMedium(item.mop.toString(), Modifier.weight(.5f))
        }
    }
}

