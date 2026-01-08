package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.BottomSheet
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.PullToRefreshLayout
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.ItemsSharedViewModel
import com.example.statspos.presentation.viewmodels.items.ItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    sharedViewModel: ItemsSharedViewModel,
    AddItemClick: (Long, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<ItemsViewModel>()
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

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.itemChanged) {
        if (sharedViewModelState.itemChanged) {
            viewModel.loadItems()
            sharedViewModel.consumeItemChanged()
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
    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    AddItemClick(0L, false)
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

        if (showBottomSheet) {
            BottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ConstantPaddings.BODY_HORIZONTAL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Dropdown(
                        value = state.categoryName,
                        onValueChange = viewModel::onCategoryNameChange,
                        items = HP.categories,
                        onItemSelected = { dropdownItem ->
                            viewModel.onCategoryIdChange(dropdownItem.id)
                        },
                        label = {
                            Text(text = "Category")
                        }
                    )
                    SubDropdown(
                        value = state.subCategoryName,
                        onValueChange = viewModel::onSubCategoryNameChange,
                        items = HP.subCategories,
                        mainId = state.categoryId,
                        onItemSelected = { dropdownItem ->
                            viewModel.onSubCategoryIdChange(dropdownItem.id)
                        },
                        label = {
                            Text(text = "Sub-Category")
                        }
                    )
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

                    var selectedItem by remember { mutableStateOf<DropdownItem?>(HP.itemFilters[0]) }
                    ComboBox(
                        modifier = Modifier.fillMaxWidth(),
                        items = HP.itemFilters,
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                            viewModel.onFilterIdChange(item.id)
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
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Spacer(Modifier.height(8.dp))
            SearchBox(
                value = state.search,
                onValueChange = viewModel::onSearchChange,
                onItemSelected = {
                    viewModel.getItem()
                    keyboardController?.hide()
                },
                onSearchClick = {
                    viewModel.loadItems()
                    keyboardController?.hide()
                },
                onKeyboardAction = {
                    viewModel.getItem()
                    keyboardController?.hide()
                },
                onBarcodeClick = {

                },
                onFilterClick = {
                    showBottomSheet = true
                }
            )

            ItemsList(
                modifier = Modifier
                    .weight(1f),
                isRefreshing = state.isLoading,
                onRefresh = {
                    viewModel.loadItems()
                },
                isLoadingNextPage = state.isLoadingNextPage,
                endReached = state.endReached,
                loadNextItems = {
                    viewModel.loadNextItems()
                },
                items = state.items,
                onItemClick = { item ->
                    AddItemClick(item.id!!, true)
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
                    text = state.totalItems.toString(),
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
    onKeyboardAction: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(ConstantPaddings.BODY_HORIZONTAL)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoCompleteItemsTextbox(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            onItemSelected = onItemSelected,
            onSearchClick = onSearchClick,
            onKeyboardAction = onKeyboardAction,
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
            buttonSize = 36.dp,
            size = 28.dp
        )
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {
                onFilterClick()
            },
            icon = Icons.Default.FilterList,
            buttonSize = 36.dp,
            size = 28.dp
        )
    }
}

@Composable
fun ItemsList(
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
        modifier = modifier,
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
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

                ItemListCard(item = item) {
                    onItemClick(it)
                }
            }

            item {
                if (isLoadingNextPage) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppCircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemListCard(
    modifier: Modifier = Modifier,
    item: Items,
    onItemClick: (Items) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable {
                    onItemClick(item)
                }
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Image
                item.imageUrl?.let {
                    if(it.isNotEmpty()){
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = HP.getImageUrl(item.imageUrl!!),
                                //error = painterResource(R.drawable.item),
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }

                // Itemname & Rows
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    LabelLarge(item.itemname.toString())
                    Spacer(Modifier.height(2.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        HeadingMedium("Cost", Modifier.weight(1f))
                        HeadingMedium("Retail", Modifier.weight(1f))
                        HeadingMedium("W.Sale", Modifier.weight(1f))
                        HeadingMedium("C.Rate", Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        LabelMedium(item.cost.toString(), Modifier.weight(1f))
                        LabelMedium(item.retail.toString(), Modifier.weight(1f))
                        LabelMedium(item.wholesale.toString(), Modifier.weight(1f))
                        LabelMedium(item.crtnRate.toString(), Modifier.weight(1f))
                    }

//                    Row {
//                        Row(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            HeadingMedium("Cost: ", Modifier.width(50.dp))
//                            LabelMedium(item.cost.toString())
//                        }
//                        Row(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            HeadingMedium("Retail: ", Modifier.width(50.dp))
//                            LabelMedium(item.retail.toString())
//                        }
//                    }
//                    Spacer(Modifier.height(2.dp))
//                    Row {
//                        Row(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            HeadingMedium("W.Sale: ", Modifier.width(50.dp))
//                            LabelMedium(item.wholesale.toString())
//                        }
//                        Row(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            HeadingMedium("C.Rate: ", Modifier.width(50.dp))
//                            LabelMedium(item.crtnRate.toString())
//                        }
//                    }
                }
            }

            // Category
            item.categoryName?.let {
                if(it.isNotEmpty()){
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
                if(it.isNotEmpty()){
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
                if(it.isNotEmpty()){
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
            onKeyboardAction = {},
            onBarcodeClick = {},
            onFilterClick = {},
        )

        ItemsList(
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
