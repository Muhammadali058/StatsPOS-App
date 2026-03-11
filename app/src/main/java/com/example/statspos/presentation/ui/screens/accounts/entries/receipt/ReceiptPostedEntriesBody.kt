package com.example.statspos.presentation.ui.screens.accounts.entries.receipt

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.accounts.Entries
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.models.reports.accounts.EntryVoucher
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.screens.accounts.entries.vouchers.entryVoucher
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.accounts.entries.receipt.ReceiptEntriesViewModel
import com.example.statspos.utils.EntryType
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun ReceiptPostedEntriesBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<ReceiptEntriesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
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
            viewModel.loadEntries()
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
            text = "Are you sure to delete this entry",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteEntry() {
                    context.showToast("Entry deleted successfully")
                    viewModel.loadEntries()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ENTRY,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteEntry() {
                    context.showToast("Entry deleted successfully")
                    viewModel.loadEntries()
                }
            }
        )
    }

    fun showVoucher(
        entry: EntryVoucher,
        ledger: List<AccountReport>?,
    ) {
        val file = entryVoucher(
            context = context,
            entryType = EntryType.RECEIPT,
            entry = entry,
            ledger = ledger,
        )

        openPdf(context, file)
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
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        selectedMOP = state.selectedMOP,
                        onValueChange = viewModel::onSearchChange,
                        onFromDateChange = { date ->
                            viewModel.onFromDateChange(date)
                            viewModel.loadEntries()
                        },
                        onToDateChange = { date ->
                            viewModel.onToDateChange(date)
                            viewModel.loadEntries()
                        },
                        onSelectedMOPChange = { mop ->
                            viewModel.onSelectedMOPChange(mop)
                            viewModel.loadEntries()
                        },
                        onSearchClick = {
                            viewModel.loadEntries()
                            keyboardController?.hide()
                        },
                    )
                }
                Spacer(Modifier.height(4.dp))
                BodyList(
                    modifier = Modifier
                        .weight(1f)
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
                    isRefreshing = state.isLoading,
                    onRefresh = {
                        viewModel.loadEntries()
                    },
                    items = state.list,
                    onDeleteClick = { entry ->
                        if (HP.userRights.deleteAnything == true) {
                            viewModel.setDeleteIdChange(entry.id!!)
                            if (HP.passwords.useDeleteAccount == true) {
                                showPasswordDialog = true
                            } else {
                                showDeleteDialog = true
                            }
                        }
                    },
                    onPrintClick = { entry ->
                        viewModel.getEntry(
                            entryId = entry.id!!,
                            onSuccess = { entry, ledger ->
                                showVoucher(entry, ledger)
                            }
                        )
                    }
                )
            }

            BottomHeading(
                modifier = Modifier
                    .navigationBarsPadding()
//                    .windowInsetsPadding(
//                        WindowInsets.navigationBars
//                            .union(WindowInsets.ime)
//                    )
                ,
                text = "Total Entries: ",
                value = state.totalEntries.toString()
            )
        }

        // Delete progress bar
        if (state.isDeleting) {
            ProgressBarLayout()
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    fromDate: LocalDate,
    toDate: LocalDate,
    selectedMOP: DropdownItem?,
    onValueChange: (String) -> Unit,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onSelectedMOPChange: (DropdownItem) -> Unit,
    onSearchClick: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        SearchTextbox(
            value = value,
            onValueChange = onValueChange,
            onEndIconClick = {
                onValueChange("")
            },
            onSearchClick = onSearchClick,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = listOf(
                DropdownItem(1, "Cash"),
                DropdownItem(2, "Bank"),
            ),
            selectedItem = selectedMOP,
            onItemSelected = onSelectedMOPChange,
            label = {
                Text(text = "M.O.P")
            },
            noneText = "Both",
            addNone = true,
        )
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
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<Entries>,
    onDeleteClick: (Entries) -> Unit,
    onPrintClick: (Entries) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        items(items) { item ->
            ListCard(
                item = item,
                onDeleteClick = onDeleteClick,
                onPrintClick = onPrintClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Entries,
    onDeleteClick: (Entries) -> Unit,
    onPrintClick: (Entries) -> Unit,
) {
    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
        shape = RoundedCornerShape(6.dp),
        onClick = {

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
                LabelLarge(item.accountName.toString())
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("Amount", Modifier.width(120.dp))
                    HeadingMedium("MOP", Modifier.width(50.dp))
                    HeadingMedium("Date", Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    LabelMedium(HP.formatDecimal(item.amount), Modifier.width(120.dp))
                    LabelMedium(item.mop.toString(), Modifier.width(50.dp))
                    LabelMedium(item.date.toString(), Modifier.weight(1f))
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("User: ")
                    LabelMedium(item.username.toString())
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    HeadingMedium("Naration: ")
                    LabelMedium(item.naration.toString())
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier,
            ) {
                AppIconButton(
                    icon = Icons.Default.Print,
                    onClick = {
                        onPrintClick(item)
                    }
                )
                Spacer(Modifier.height(8.dp))
                if (HP.userRights.deleteAnything == true) {
                    AppIconButton(
                        icon = Icons.Default.Delete,
                        onClick = {
                            onDeleteClick(item)
                        }
                    )
                }
            }
        }
    }
}

