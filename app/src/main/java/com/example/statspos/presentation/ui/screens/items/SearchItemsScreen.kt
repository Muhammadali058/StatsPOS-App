package com.example.statspos.presentation.ui.screens.items

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ChipsRow
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.ImageView
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SubChipsRow
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.items.SearchItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchItemsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<SearchItemsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
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
                viewModel.onSearchChange(it)
                viewModel.getItem(it)
                showBarcodeScanner = false
            }
        )
    }

//    var selectedCategory by remember { mutableStateOf(HP.getNoneDropdownItem()) }
//    var selectedSubCategory by remember { mutableStateOf(HP.getNoneDropdownItem()) }

    Scaffold(
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
                title = "Search Item",
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
//                Dropdown(
//                    value = state.categoryName,
//                    onValueChange = viewModel::onCategoryNameChange,
//                    items = HP.categories,
//                    onItemSelected = { dropdownItem ->
//                        viewModel.onCategoryIdChange(dropdownItem.id)
//                    },
//                    label = {
//                        Text(text = "Category")
//                    }
//                )
//                SubDropdown(
//                    value = state.subCategoryName,
//                    onValueChange = viewModel::onSubCategoryNameChange,
//                    items = HP.subCategories,
//                    mainId = state.categoryId,
//                    onItemSelected = { dropdownItem ->
//                        viewModel.onSubCategoryIdChange(dropdownItem.id)
//                    },
//                    label = {
//                        Text(text = "Sub-Category")
//                    }
//                )
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
                            sharedViewModel.setItem(item)
                            goBackWithResult()
                        }
                    )
                }

                BottomHeading(
                    text = "Total Items: ",
                    value = state.totalItems.toString()
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
            suggestions = false,
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

            ListCard(item = item) {
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Items,
    onItemClick: (Items) -> Unit
) {
    ListCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
            ImageView(
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .size(60.dp),
                showIfNull = false,
            ) {
                Spacer(Modifier.width(8.dp))
            }

            // Itemname & Rows
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // Itemname
                LabelLarge(item.itemname.toString())
                Spacer(Modifier.height(2.dp))

                // Rows when fourRateSystem
                if (HP.settings.fourRateSystem == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        HeadingMedium("Rate 1", Modifier.weight(1f))
                        HeadingMedium("Rate 2", Modifier.weight(1f))
                        HeadingMedium("Rate 3", Modifier.weight(1f))
                        HeadingMedium("Rate 4", Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        LabelMedium(HP.formatDecimal(item.retail), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.wholesale), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.rate3), Modifier.weight(1f))
                        LabelMedium(HP.formatDecimal(item.rate4), Modifier.weight(1f))
                    }
                } else {
                    // Rows else fourRateSystem
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
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

        // Location
        item.location?.let {
            if (it.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HeadingMedium("Location: ")
                    LabelMedium(item.location.toString())
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
