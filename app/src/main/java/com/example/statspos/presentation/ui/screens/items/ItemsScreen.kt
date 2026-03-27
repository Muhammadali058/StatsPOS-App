package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AccessDeniedBox
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ChipsRow
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DeleteIcon
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.ListImageView
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SubChipsRow
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.ItemsViewModel
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    sharedViewModel: SharedViewModel,
    onAddButtonClick: (Long, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<ItemsViewModel>()
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
            viewModel.loadItems()
            sharedViewModel.consumeDataChanged()
        }
    }

    // When branch changed
    LaunchedEffect(sharedViewModelState.refreshItemsScreen) {
        if (sharedViewModelState.refreshItemsScreen) {
            viewModel.loadItems()
            sharedViewModel.consumeRefreshItemsScreen()
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
            text = "Are you sure to delete this item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Item deleted successfully")
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ITEM,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Item deleted successfully")
                }
            }
        )
    }

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = {
                showBarcodeScanner = false
            },
            onScanned = {
                viewModel.onSearchChange(it)
                viewModel.getItem(it)
                showBarcodeScanner = false
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
            if (HP.userRights.items == true) {
                AppFloatingActionButton {
                    onAddButtonClick(0L, false)
                }
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

        // Bottom Sheet
        if (showBottomSheet) {
            BottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                },
            ) {
                Dropdown(
                    value = state.vendorName,
                    onValueChange = viewModel::onVendorNameChange,
                    items = HP.vendors,
                    onItemSelected = { dropdownItem ->
                        viewModel.onVendorIdChange(dropdownItem.id)
                    },
                    label = {
                        Text(text = "Vendor")
                    }
                )

                ComboBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    items = HP.itemFilters,
                    selectedItem = state.selectedSearchBy,
                    onItemSelected = { item ->
                        viewModel.onSelectedSearchByChange(item)
                    },
                    label = {
                        Text(text = "Search By")
                    },
                )

                Button(onClick = {
                    viewModel.loadItems()
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
            if (HP.userRights.items == true) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        SearchBox(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(ConstantPaddings.BODY_HORIZONTAL)
                                .padding(vertical = 8.dp),
                            value = state.search,
                            onValueChange = viewModel::onSearchChange,
                            onItemSelected = {
                                viewModel.getItem(it)
                                keyboardController?.hide()
                            },
                            onSearchClick = {
                                viewModel.loadItems()
                                keyboardController?.hide()
                            },
                            onEndIconClick = {
                                viewModel.getItem(it)
                                keyboardController?.hide()
                            },
                            onBarcodeClick = {
                                showBarcodeScanner = true
                            },
                            onFilterClick = {
                                showBottomSheet = true
                            }
                        )
                        ChipsRow(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(ConstantPaddings.BODY_HORIZONTAL)
                                .padding(bottom = 12.dp),
                            items = HP.categories,
                            selectedItem = state.category,
                            onItemSelected = {
                                viewModel.onCategoryChange(it)
                                viewModel.loadItems()
                            }
                        )
                        SubChipsRow(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(ConstantPaddings.BODY_HORIZONTAL)
                                .padding(bottom = 12.dp),
                            items = HP.subCategories,
                            selectedItem = state.subCategory,
                            mainId = state.category.id,
                            onItemSelected = {
                                viewModel.onSubCategoryChange(it)
                                viewModel.loadItems()
                            },
                        )
                        BodyList(
                            modifier = Modifier
                                .weight(1f)
                                .padding(ConstantPaddings.BODY_HORIZONTAL),
                            isRefreshing = state.isLoading,
                            onRefresh = {
                                viewModel.loadItems()
                            },
                            isLoadingNextPage = state.isLoadingNextPage,
                            endReached = state.endReached,
                            loadNextItems = {
                                viewModel.loadNextItems()
                            },
                            items = state.list,
                            onItemClick = { item ->
                                onAddButtonClick(item.id!!, true)
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
                        text = "Total Items: ",
                        value = state.totalItems.toString()
                    )
                }
            } else {
                AccessDeniedBox()
            }
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onEndIconClick: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onFilterClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoCompleteItemsTextbox(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            onItemSelected = onItemSelected,
            onEndIconClick = onEndIconClick,
            onSearchClick = onSearchClick,
            label = {
                Text(
                    text = "Search"
                )
            },
        )
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {
                onBarcodeClick()
            },
            icon = R.drawable.ic_barcode,
            buttonSize = 32.dp,
            size = 26.dp
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

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    isLoadingNextPage: Boolean,
    endReached: Boolean,
    loadNextItems: () -> Unit,
    items: List<Items>,
    onItemClick: (Items) -> Unit,
    onDeleteClick: (Items) -> Unit,
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
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Items,
    onItemClick: (Items) -> Unit,
    onDeleteClick: (Items) -> Unit,
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
                showIfNull = true,
            ) {
                Spacer(Modifier.width(8.dp))
            }

            // Itemname & Rows
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // Itemname
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelLarge(
                        modifier = Modifier
                            .weight(1f),
                        text = item.itemname.toString()
                    )

                    if (HP.userRights.deleteAnything == true) {
                        Spacer(Modifier.width(8.dp))
                        DeleteIcon {
                            onDeleteClick(item)
                        }
                    }
                }
                // Itemname
//                LabelLarge(item.itemname.toString())
                Spacer(Modifier.height(2.dp))

                // Rows when fourRateSystem
                if (HP.settings.fourRateSystem == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        HeadingMedium("Cost", Modifier.weight(1f))
                        HeadingMedium("Rate 1", Modifier.weight(1f))
                        HeadingMedium("Rate 2", Modifier.weight(1f))
                        HeadingMedium("Rate 3", Modifier.weight(1f))
                        HeadingMedium("Rate 4", Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        LabelMedium(HP.formatDecimal(item.cost), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.retail), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.wholesale), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.rate3), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.rate4), Modifier.weight(1f))
                    }
                } else
                {
                    // Rows else fourRateSystem
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        HeadingMedium(
                            text = "Cost",
                            Modifier.weight(1f)
                        )
                        HeadingMedium(
                            text = "Retail",
                            Modifier.weight(1f)
                        )
                        HeadingMedium(
                            text = "W.Sale",
                            Modifier.weight(1f)
                        )
                        HeadingMedium(
                            text = if (HP.settings.saleCartons == true) "C.Rate" else "MP",
                            Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        LabelMedium(
                            text = HP.formatDecimal(item.cost),
                            Modifier.weight(1f)
                        )
                        LabelMedium(
                            text = HP.formatDecimal(item.retail),
                            Modifier.weight(1f)
                        )
                        LabelMedium(
                            text = HP.formatDecimal(item.wholesale),
                            Modifier.weight(1f)
                        )
                        LabelMedium(
                            text = if (HP.settings.saleCartons == true) HP.formatDecimal(item.crtnRate) else HP.formatDecimal(
                                item.marketPrice
                            ),
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Stock
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                HeadingMedium("Stock Pcs: ")
                LabelMedium(HP.formatDecimal(item.stockPcs))
            }
            if (HP.settings.saleCartons == true) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    HeadingMedium("Stock Crtn: ")
                    LabelMedium(item.stockCrtn.toString())
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
                    HeadingMedium("Category: ")
                    LabelMedium(item.categoryName.toString())
                }
            }
        }

        // Sub-Category
        item.subCategoryName?.let {
            if (it.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HeadingMedium("Sub-Category: ")
                    LabelMedium(item.subCategoryName.toString())
                }
            }
        }

        // Vendor
        item.vendorName?.let {
            if (it.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HeadingMedium("Vendor: ")
                    LabelMedium(item.vendorName.toString())
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Spacer(Modifier.height(8.dp))
        SearchBox(
            value = "",
            onValueChange = {},
            onItemSelected = {},
            onSearchClick = {},
            onEndIconClick = {},
            onBarcodeClick = {},
            onFilterClick = {},
        )

        BodyList(
            modifier = Modifier
                .weight(1f),
            isRefreshing = false,
            onRefresh = {

            },
            isLoadingNextPage = false,
            endReached = false,
            loadNextItems = {

            },
            items = (1..50).map {
                Items(
                    id = it.toLong(),
                    itemname = "Coca cola 1.5 ltr item $it",
                    imageUrl = HP.getImageUrl("43512549.png")
                )
            },
            onItemClick = { item ->

            },
            onDeleteClick = { item ->

            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            HeadingMedium(
                text = "Total Items: ",
            )
            LabelMedium(
                text = "500.0",
            )
        }
    }
}
