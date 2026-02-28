package com.example.statspos.presentation.ui.screens.reports.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.ReportButton
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.screens.items.SearchItemsScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.reports.SalesReportsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.serialization.Serializable
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SearchItem : Routes()

    @Serializable
    data class ViewReport(val updateId: Long) : Routes()
}

@Composable
fun SalesReportsScreen(
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
                    onBack = {
                        onBack()
                    },
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                    onViewReportClick = { updateId ->
                        navigate(Routes.ViewReport(updateId))
                    },
                )
            }
            entry<Routes.SearchItem> { key ->
                SearchItemsScreen(
                    sharedViewModel = sharedViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.ViewReport> { key ->

            }
        }
    )
}

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
    onSearchItemClick: () -> Unit,
    onViewReportClick: (Long) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SalesReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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

    // For search item
    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            val item = sharedViewModelState.item
            item?.run {
                viewModel.onItemnameChange(itemname!!)
                viewModel.getItem(itemname!!)
                sharedViewModel.consumeDataChanged()
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

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = {
                showBarcodeScanner = false
            },
            onScanned = {
                viewModel.onItemnameChange(it)
                viewModel.getItem(it)

                showBarcodeScanner = false
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Sales Reports",
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SearchBox(
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        onFromDateChange = viewModel::onFromDateChange,
                        onToDateChange = viewModel::onToDateChange,
                        onFilterClick = {

                        },
                    )
                    ItemnameBox(
                        value = state.itemname,
                        onValueChange = viewModel::onItemnameChange,
                        onItemSelected = {
                            viewModel.getItem(it)
                            keyboardController?.hide()
                        },
                        onSearchClick = {
                            viewModel.getItem(it)
                            keyboardController?.hide()
                        },
                        onEndIconClick = {
                            viewModel.onItemnameChange("")
                        },
                        onBarcodeClick = {
                            showBarcodeScanner = true
                        },
                        onSearchItemClick = onSearchItemClick,
                    )
                    Spacer(Modifier.height(8.dp))
                    Dropdowns(
                        categoryName = state.categoryName,
                        subCategoryName = state.subCategoryName,
                        categoryId = state.categoryId,
                        vendorName = state.vendorName,
                        customerName = state.customerName,
                        accountCategoryName = state.accountCategoryName,
                        supplierName = state.supplierName,
                        username = state.username,
                        onCategoryNameChange = viewModel::onCategoryNameChange,
                        onSubCategoryNameChange = viewModel::onSubCategoryNameChange,
                        onVendorNameChange = viewModel::onVendorNameChange,
                        onCustomerNameChange = viewModel::onCustomerNameChange,
                        onAccountCategoryNameChange = viewModel::onAccountCategoryNameChange,
                        onSupplierNameChange = viewModel::onSupplierNameChange,
                        onUsernameChange = viewModel::onUsernameChange,
                        onCategoryIdChange = viewModel::onCategoryIdChange,
                        onSubCategoryIdChange = viewModel::onSubCategoryIdChange,
                        onVendorIdChange = viewModel::onVendorIdChange,
                        onCustomerIdChange = viewModel::onCustomerIdChange,
                        onAccountCategoryIdChange = viewModel::onAccountCategoryIdChange,
                        onSupplierIdChange = viewModel::onSupplierIdChange,
                        onUserIdChange = viewModel::onUserIdChange,
                    )
                    Spacer(Modifier.height(8.dp))
                    ReportButtons()
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
private fun SearchBox(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
    }
}

@Composable
private fun ReportButtons() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReportButton("Brief Report", Modifier.width(120.dp)) { }
            Spacer(Modifier.width(8.dp))
            ReportButton("Total Bills", Modifier.width(120.dp)) { }
            Spacer(Modifier.width(8.dp))
            ReportButton("Total Items", Modifier.width(120.dp)) { }
            Spacer(Modifier.width(8.dp))
            ReportButton("Filter Report", Modifier.width(120.dp)) { }
        }
    }
}

@Composable
private fun ItemnameBox(
    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onEndIconClick: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onSearchItemClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AutoCompleteItemsTextbox(
                    value = value,
                    onValueChange = onValueChange,
                    onItemSelected = onItemSelected,
                    onEndIconClick = onEndIconClick,
                    onSearchClick = onSearchClick,
                    label = {
                        Text(
                            text = "Select Item"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            onEndIconClick(value)
                        }) {
                            AppIcon(
                                icon = Icons.Default.Clear,
                                size = 20.dp
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go
                    ),
                    padding = PaddingValues(top = 4.dp),
                )
                ReportButton(
                    modifier = Modifier.offset(y = (-1).dp),
                    shape = RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)
                ) {

                }
            }
            Spacer(Modifier.width(4.dp))
            AppIconButton(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    onBarcodeClick()
                },
                icon = R.drawable.ic_barcode,
                buttonSize = 32.dp,
                size = 26.dp
            )
            Spacer(Modifier.width(4.dp))
            AppIconButton(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    onSearchItemClick()
                },
                icon = Icons.Default.Search,
                buttonSize = 32.dp,
                size = 26.dp
            )
        }
    }
}

@Composable
private fun Dropdowns(
    categoryName: String,
    subCategoryName: String,
    categoryId: Long,
    vendorName: String,
    customerName: String,
    accountCategoryName: String,
    supplierName: String,
    username: String,
    onCategoryNameChange: (String) -> Unit,
    onSubCategoryNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onAccountCategoryNameChange: (String) -> Unit,
    onSupplierNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSubCategoryIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
    onCustomerIdChange: (Long) -> Unit,
    onAccountCategoryIdChange: (Long) -> Unit,
    onSupplierIdChange: (Long) -> Unit,
    onUserIdChange: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = categoryName,
                    onValueChange = onCategoryNameChange,
                    items = HP.categories,
                    onItemSelected = { dropdownItem ->
                        onCategoryIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Category")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                SubDropdown(
                    value = subCategoryName,
                    onValueChange = onSubCategoryNameChange,
                    items = HP.subCategories,
                    mainId = categoryId,
                    onItemSelected = { dropdownItem ->
                        onSubCategoryIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Sub-Category")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = vendorName,
                    onValueChange = onVendorNameChange,
                    items = HP.vendors,
                    onItemSelected = { dropdownItem ->
                        onVendorIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Vendor")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = customerName,
                    onValueChange = onCustomerNameChange,
                    items = HP.customers,
                    onItemSelected = { dropdownItem ->
                        onCustomerIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Customer")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = accountCategoryName,
                    onValueChange = onAccountCategoryNameChange,
                    items = HP.accountCategories,
                    onItemSelected = { dropdownItem ->
                        onAccountCategoryIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Customer Category")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = supplierName,
                    onValueChange = onSupplierNameChange,
                    items = HP.suppliers,
                    onItemSelected = { dropdownItem ->
                        onSupplierIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Supplier")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Dropdown(
                    value = username,
                    onValueChange = onUsernameChange,
                    items = HP.users,
                    onItemSelected = { dropdownItem ->
                        onUserIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "User")
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            ReportButton { }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBox(
            LocalDate.now(),
            LocalDate.now(),
            {},
            {},
            {},
        )
        ItemnameBox(
            "",
            {},
            {},
            {},
            {},
            {},
            {},
        )
        Spacer(Modifier.height(8.dp))
        Dropdowns(
            "",
            "",
            0L,
            "",
            "",
            "",
            "",
            "",
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
        )
        Spacer(Modifier.height(8.dp))
        ReportButtons()
    }
}
