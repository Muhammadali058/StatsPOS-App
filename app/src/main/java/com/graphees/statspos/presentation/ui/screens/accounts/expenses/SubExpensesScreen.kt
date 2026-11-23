package com.graphees.statspos.presentation.ui.screens.accounts.expenses

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.models.accounts.Expenses
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.ArrowIconButton
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIconButton
import com.graphees.statspos.presentation.ui.components.EditIconButton
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.ListReportIcon
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.accounts.banks.BankStatementScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.expenses.SubExpensesViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.serialization.Serializable
import kotlin.math.exp

private sealed class SubRoutes : NavKey {
    @Serializable
    data object Home : SubRoutes()

    @Serializable
    data class AddUpdateSubExpense(val updateId: Long, val isUpdate: Boolean, val expenseId: Long) :
        SubRoutes()

    @Serializable
    data class Ledger(val account: Accounts) : SubRoutes()
}

@Composable
fun SubExpensesScreen(
    sharedViewModel: SharedViewModel,
    expenseId: Long,
    onReportClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(SubRoutes.Home)
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
            entry<SubRoutes.Home> {
                Home(
                    sharedViewModel = sharedViewModel,
                    expenseId = expenseId,
                    onAddButtonClick = { updateId, isUpdated ->
                        navigate(SubRoutes.AddUpdateSubExpense(updateId, isUpdated, expenseId))
                    },
                    onLedgerClick = { account ->
                        navigate(SubRoutes.Ledger(account))
                    },
                    onReportClick = { subExpenseId ->
                        onReportClick(subExpenseId)
                    },
                    onBack = onBack,
                )
            }
            entry<SubRoutes.AddUpdateSubExpense> { key ->
                AddUpdateSubExpenseScreen (
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    expenseId = key.expenseId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<SubRoutes.Ledger> { key ->
                ExpenseEntryScreen (
                    sharedViewModel = sharedViewModel,
                    subExpenseId = key.account.id!!,
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
    expenseId: Long,
    onAddButtonClick: (Long, Boolean) -> Unit,
    onLedgerClick: (Accounts) -> Unit,
    onReportClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SubExpensesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }
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

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialState(
                expenseId = expenseId
            )
            viewModel.loadData()
            viewModel.setHasLoadedOnce(true)
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            viewModel.loadData()
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
            text = "Are you sure to delete this sub-expense",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Sub-Expense deleted successfully")
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ACCOUNT,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Sub-Expense deleted successfully")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                onAddButtonClick(0L, false)
            }
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Sub-Expenses",
            )
        }
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
                        onItemClick = { subExpense ->
                            onLedgerClick(subExpense)
                        },
                        onEditClick = { subExpense ->
                            onAddButtonClick(subExpense.id!!, true)
                        },
                        onDeleteClick = { subExpense ->
                            selectedId = subExpense.id!!

                            if (HP.passwords.useDeleteAccount == true) {
                                showPasswordDialog = true
                            } else {
                                showDeleteDialog = true
                            }
                        },
                        onReportClick = { subExpense ->
                            onReportClick(subExpense.id!!)
                        },
                    )
                }

                BottomHeading(
                    text = "Total Sub-Expenses: ",
                    value = state.totalSubExpenses.toString()
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
    items: List<Accounts>,
    onItemClick: (Accounts) -> Unit,
    onEditClick: (Accounts) -> Unit,
    onDeleteClick: (Accounts) -> Unit,
    onReportClick: (Accounts) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        item {
            Spacer(Modifier.height(4.dp))
        }
        items(items) { item ->
            ListCard(
                item = item,
                onItemClick = onItemClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onReportClick = onReportClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Accounts,
    onItemClick: (Accounts) -> Unit,
    onEditClick: (Accounts) -> Unit,
    onDeleteClick: (Accounts) -> Unit,
    onReportClick: (Accounts) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ConstantPaddings.LIST_PADDING_VERTICAL),
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
                // region Name
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = item.accountName.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.width(8.dp))
                    ArrowIconButton{
                        onItemClick(item)
                    }
                }
                // endregion

                Spacer(Modifier.height(4.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                    ) {
                        if(HP.userRights.accountsReport == true) {
                            ListReportIcon {
                                onReportClick(item)
                            }
                        }
                    }
                    if (HP.userRights.deleteAnything == true) {
                        Spacer(Modifier.width(8.dp))
                        DeleteIconButton {
                            onDeleteClick(item)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    EditIconButton {
                        onEditClick(item)
                    }
                }
            }
        }
    }
}


