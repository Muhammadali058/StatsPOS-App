package com.example.statspos.presentation.ui.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBillItems
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.ListImageView
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.accounts.account_categories.AddUpdateAccountCategoryScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.sales.SalesItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class AddUpdateSalesItem(val updateId: Long, val isUpdate: Boolean) : Routes()
}

@Composable
fun SalesItemsScreen(
    sharedViewModel: SharedViewModel,
    isPostedBill: Boolean,
    sales: Sales,
    onBack: () -> Unit,
) {
    val innerSharedViewModer = hiltViewModel<SharedViewModel>()
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
                    mainSharedViewModel = sharedViewModel,
                    sharedViewModel = innerSharedViewModer,
                    isPostedBill = isPostedBill,
                    sales = sales,
                    onAddButtonClick = { updateId, isUpdate ->
                        navigate(Routes.AddUpdateSalesItem(updateId, isUpdate))
                    },
                    onBack = {
                        onBack()
                    },
                )
            }
            entry<Routes.AddUpdateSalesItem> { key ->
                AddUpdateAccountCategoryScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
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
    mainSharedViewModel: SharedViewModel,
    sharedViewModel: SharedViewModel,
    isPostedBill: Boolean,
    sales: Sales,
    onAddButtonClick: (Long, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        mainSharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SalesItemsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
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

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            viewModel.updateInitialState(
                isPostedBill = isPostedBill,
                sales = sales,
            )

            viewModel.loadData()

            hasLoadedOnce = true
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
            text = "Are you sure to delete this bill",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(sales.id!!) {
                    context.showToast("Bill deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_SALES_BILL,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(sales.id!!) {
                    context.showToast("Bill deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    Scaffold(
//        snackbarHost = {
//            AppSnackbarHost(
//                snackbarHostState = snackbarHostState,
//            )
//        },
        floatingActionButton = {
            AppFloatingActionButton(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp)
            ) {
                onAddButtonClick(0L, false)
            }
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Total: ${state.total}",
                actions = {
                    Row {
                        if (isPostedBill) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    if (HP.passwords.useDeleteSalesBill == true) {
                                        showPasswordDialog = true
                                    } else {
                                        showDeleteDialog = true
                                    }
                                }) {
                                    AppIcon(
                                        icon = Icons.Default.Delete,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        } else {
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
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
                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { packages ->
                            onAddButtonClick(packages.id!!, true)
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        HeadingMedium(text = "Items: ")
                        LabelMedium(text = state.totalItems.toString())
                        Spacer(Modifier.width(8.dp))
                        HeadingMedium(text = "Qty: ")
                        LabelMedium(text = HP.formatDecimal(state.totalQty))
                        if(HP.settings.saleCartons == true) {
                            Spacer(Modifier.width(8.dp))
                            HeadingMedium(text = "Crtn: ")
                            LabelMedium(text = state.totalCrtn.toString())
                        }
                        Spacer(Modifier.width(8.dp))
                        HeadingMedium(text = "Disc: ")
                        LabelMedium(text = HP.formatDecimal(state.totalItemDisc))
                    }
                    Box(
                        Modifier
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                            .padding(bottom = 16.dp)
                    ) {
                        if (state.isPosting) {
                            AppCircularProgressIndicator()
                        } else {
                            SaveButton(
                                text = "Post Bill"
                            ) {
                                viewModel.postBill {
                                    goBackWithResult()
                                }
                            }
                        }
                    }
                }
            }

            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
                modifier = Modifier,
            )

            if (state.isDeleting) {
                ProgressBarLayout()
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
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            onEndIconClick = {
                onValueChange("")
            },
            onSearchClick = onSearchClick,
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<SalesBillItems>,
    onItemClick: (SalesBillItems) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        items(items) { item ->
            ListCard(item = item) {
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: SalesBillItems,
    onItemClick: (SalesBillItems) -> Unit
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
                // Itemname
                LabelLarge(item.itemname.toString())
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        HeadingMedium(text = "Qty: ")
                        LabelMedium(text = HP.formatDecimal(item.qty))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium(text = "Rate: ")
                        LabelMedium(text = HP.formatDecimal(item.rate))
                    }
                }
                if (HP.settings.saleCartons == true) {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(.7f)
                        ) {
                            HeadingMedium(text = "Crtn: ")
                            LabelMedium(text = item.crtn.toString())
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Crtn Rate: ")
                            LabelMedium(text = HP.formatDecimal(item.crtnRate))
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        HeadingMedium(text = "Disc: ")
                        LabelMedium(text = HP.formatDecimal(item.disc))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium(text = "Total: ")
                        LabelMedium(text = HP.formatDecimal(item.total))
                    }
                }
            }
        }
    }
}

