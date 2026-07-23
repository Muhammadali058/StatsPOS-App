package com.graphees.statspos.presentation.ui.screens.accounts.customers

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.main.main.premium.HelpScreen
import com.graphees.statspos.presentation.ui.screens.main.main.premium.PaymentScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.customers.CustomersViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdateCustomer(val updateId: Long, val isUpdate: Boolean) : Routes()

    @Serializable
    data object Payment : Routes()

    @Serializable
    data object Help : Routes()

}

@Composable
fun CustomersScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(Routes.Home)
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
            entry<Routes.Home> {
                Home(
                    sharedViewModel = sharedViewModel,
                    onAddButtonClick = { updateId, isUpdate ->
                        navigate(Routes.AddUpdateCustomer(updateId, isUpdate))
                    },
                    onBack = {
                        onBack()
                    },
                )
            }
            entry<Routes.AddUpdateCustomer> { key ->
                AddUpdateCustomerScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onUpgradeClick = {
                        navigate(Routes.Payment)
                    },
                    onHelpClick = {
                        navigate(Routes.Help)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.Payment> {
                PaymentScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.Help> {
                HelpScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onAddButtonClick: (Long, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<CustomersViewModel>()
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
            text = "Are you sure to delete this customer",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Customer deleted successfully")
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
                    context.showToast("Customer deleted successfully")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 180.dp),
                snackbarHostState = snackbarHostState,
            )
        },
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
                title = "Customers",
            )
        }
    ) { innerPadding ->
        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                },
            ) {
                Dropdown(
                    value = state.categoryName,
                    onValueChange = viewModel::onCategoryNameChange,
                    items = HP.accountCategories,
                    onItemSelected = { dropdownItem ->
                        viewModel.onCategoryIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Category")
                    },
                    placeholder = {
                        PlaceHolder(text = "Category")
                    },
                    outlined = true,
                )

                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = listOf(
                        DropdownItem(1, "Retail"),
                        DropdownItem(2, "Wholesale"),
                    ),
                    selectedItem = state.selectedSearchType,
                    onItemSelected = { item ->
                        viewModel.onSelectedSearchTypeChange(item)
                    },
                    label = {
                        Text(text = "Type")
                    },
                    placeholder = {
                        PlaceHolder(text = "Type")
                    },
                    addNone = true,
                    outlined = true,
                )

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
                        showFilterIcon = true,
                        onFilterClick = {
                            showBottomSheet = true
                        }
                    )
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
                        onItemClick = { account ->
                            onAddButtonClick(account.id!!, true)
                        },
                        onDeleteClick = { account ->
                            selectedId = account.id!!

                            if (HP.passwords.useDeleteAccount == true) {
                                showPasswordDialog = true
                            } else {
                                showDeleteDialog = true
                            }
                        },
                    )
                }

                BottomHeading(
                    text = "Total Customers: ",
                    value = state.totalCustomers.toString()
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
    isLoadingNextPage: Boolean,
    endReached: Boolean,
    loadNextItems: () -> Unit,
    items: List<Accounts>,
    onItemClick: (Accounts) -> Unit,
    onDeleteClick: (Accounts) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        isLoadingNextPage = isLoadingNextPage,
    ) {
        item{
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
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Accounts,
    onItemClick: (Accounts) -> Unit,
    onDeleteClick: (Accounts) -> Unit,
) {
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
            // Image
            ListImageView(
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .size(60.dp),
            ) {
                Spacer(Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                ListMainLabel(item.customerName.toString())

                // Contact
                item.contact?.let {
                    if (it.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ListHeading("Contact: ")
                            ListLabel(item.contact.toString())
                        }
                    }
                }

                // City
                item.city?.let {
                    if (it.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            ListHeading("City: ")
                            ListLabel(item.city.toString())
                        }
                    }
                }
            }

            if (HP.userRights.deleteAnything == true) {
                Spacer(Modifier.width(8.dp))
                DeleteIcon {
                    onDeleteClick(item)
                }
            }
        }

        // Category
        item.categoryName?.let {
            if (it.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ListHeading("Category: ")
                    ListLabel(item.categoryName.toString())
                }
            }
        }

    }
}
