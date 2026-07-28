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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
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
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.graphees.statspos.presentation.ui.components.BarcodeScannerDialog
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.BottomSheet
import com.graphees.statspos.presentation.ui.components.ChipsRow
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.FilterIcon
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.PullToRefreshLayout
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SubChipsRow
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.items.SearchItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
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
    val itemFocusRequester = remember { FocusRequester() }
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
            itemFocusRequester.requestFocus()
            viewModel.setHasLoadedOnce(true)
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
                        itemFocusRequester = itemFocusRequester,
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
                    BodyList2(
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
    itemFocusRequester: FocusRequester? = null,
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
            suggestions = false,
            trailingIcon = {
                AppIconButton(
                    icon = R.drawable.ic_barcode,
                    size = 20.dp,
                    onClick = {
                        onBarcodeClick()
                    }
                )
            },
            focusRequester = itemFocusRequester,
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
    onItemClick: (Items) -> Unit,
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
                                        color = primaryColor
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
                        ListHeading("Rate 1", Modifier.weight(1f))
                        ListHeading("Rate 2", Modifier.weight(1f))
                        ListHeading("Rate 3", Modifier.weight(1f))
                        ListHeading("Rate 4", Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
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


@Composable
private fun BodyList2(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    isLoadingNextPage: Boolean,
    endReached: Boolean,
    loadNextItems: () -> Unit,
    items: List<Items>,
    onItemClick: (Items) -> Unit,
) {
    PullToRefreshLayout(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
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

                ListCard2(item = item) {
                    onItemClick(it)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (isLoadingNextPage) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppCircularProgressIndicator()
                    }
                }else{
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ListCard2(
    modifier: Modifier = Modifier,
    item: Items,
    onItemClick: (Items) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        onClick = {
            onItemClick(item)
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            ListImageView(
                imageUrl = item.imageUrl,
                modifier = Modifier
                    .size(80.dp),
                showIfNull = true,
            ) {
                Spacer(Modifier.height(8.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = item.itemname.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 13.sp,
                lineHeight = 16.sp,
//                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(8.dp))
            ListHorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Itemname & Rows
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = "Retail",
                        Modifier.weight(1f)
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.retail),
                        Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = "W.Sale",
                        Modifier.weight(1f)
                    )
                    ListLabel(
                        text = HP.formatDecimal(item.wholesale),
                        Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    ListHeading(
                        text = if (HP.settings.saleCartons == true) "C.Rate" else "MP",
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

            // region Stock
            Spacer(Modifier.height(8.dp))
            ListHorizontalDivider()
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
                        text = if (HP.settings.saleCartons == true) "Qty: " else "Qty: ",
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
                            text = "Crtn: ",
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

        BodyList2(
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
