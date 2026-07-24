package com.graphees.statspos.presentation.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.items.Items
import com.graphees.statspos.presentation.ui.components.AccessDeniedBox
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.graphees.statspos.presentation.ui.components.BarcodeScannerDialog
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ChipsRow
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.FilterIcon
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.PullToRefreshLayout
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SubChipsRow
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.items.ItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
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
                    },
                    placeholder = {
                        PlaceHolder(text = "Vendor")
                    },
                    outlined = true,
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
                    placeholder = {
                        PlaceHolder(text = "Search By")
                    },
                    outlined = true,
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
                            onGoClick = {
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
    onGoClick: (String) -> Unit,
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
            onGoClick = onGoClick,
            trailingIcon = {
                AppIconButton(
                    icon = R.drawable.ic_barcode,
                    size = 20.dp,
                    onClick = {
                        onBarcodeClick()
                    }
                )
            }
        )

        Spacer(Modifier.width(8.dp))
        FilterIcon {
            onFilterClick()
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
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            modifier = modifier,
                            text = item.itemname.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )

                        // Category
                        item.categoryName?.let {
                            if (it.isNotEmpty()) {
                                Spacer(Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    ListHeading(
                                        text = "Category: ",
                                        color = primaryColor,
                                    )
                                    ListLabel(
                                        text = item.categoryName.toString(),
                                        color = secondaryColor
                                    )
                                    item.subCategoryName?.let {
                                        if (it.isNotEmpty()) {
                                            ListHeading(
                                                text = "  Sub: ",
                                                color = primaryColor
                                            )
                                            ListLabel(
                                                text = item.subCategoryName.toString(),
                                                color = secondaryColor,
                                            )
                                        }
                                    }
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

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.2f)
                )
                Spacer(Modifier.height(8.dp))

                // Rows when fourRateSystem
                if (HP.settings.fourRateSystem == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListHeading("Cost", Modifier.weight(1f))
                        ListHeading("Rate 1", Modifier.weight(1f))
                        ListHeading("Rate 2", Modifier.weight(1f))
                        ListHeading("Rate 3", Modifier.weight(1f))
                        ListHeading("Rate 4", Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListLabel(HP.formatDecimal(item.cost), Modifier.weight(1f))
                        ListLabel(HP.formatDecimal(item.retail), Modifier.weight(1f))
                        ListLabel(HP.formatDecimal(item.wholesale), Modifier.weight(1f))
                        ListLabel(HP.formatDecimal(item.rate3), Modifier.weight(1f))
                        ListLabel(HP.formatDecimal(item.rate4), Modifier.weight(1f))
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListHeading(
                            text = "Cost",
                            Modifier.weight(1f)
                        )
                        ListHeading(
                            text = "Retail",
                            Modifier.weight(1f)
                        )
                        ListHeading(
                            text = "W.Sale",
                            Modifier.weight(1f)
                        )
                        ListHeading(
                            text = if (HP.settings.saleCartons == true) "C.Rate" else "MP",
                            Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        ListLabel(
                            text = HP.formatDecimal(item.cost),
                            Modifier.weight(1f)
                        )
                        ListLabel(
                            text = HP.formatDecimal(item.retail),
                            Modifier.weight(1f)
                        )
                        ListLabel(
                            text = HP.formatDecimal(item.wholesale),
                            Modifier.weight(1f)
                        )
                        ListLabel(
                            text = if (HP.settings.saleCartons == true) HP.formatDecimal(item.crtnRate) else HP.formatDecimal(
                                item.marketPrice
                            ),
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // region Stock
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.2f)
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                icon = R.drawable.stock,
                size = 14.dp,
                tint = primaryColor,
            )
            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                ListHeading(
                    text = if (HP.settings.saleCartons == true) "Stock Pcs: " else "Stock: ",
                    color = primaryColor,
                )
                ListLabel(
                    text = HP.formatDecimal(item.stockPcs),
                    color = secondaryColor,
                )
            }
            if (HP.settings.saleCartons == true) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    ListHeading(
                        text = "Stock Crtn: ",
                        color = primaryColor,
                    )
                    ListLabel(
                        text = item.stockCrtn.toString(),
                        color = secondaryColor,
                    )
                }
            }
        }
        // endregion
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
            onGoClick = {},
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
