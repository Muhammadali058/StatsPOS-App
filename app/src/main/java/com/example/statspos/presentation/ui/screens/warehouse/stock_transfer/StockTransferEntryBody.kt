package com.example.statspos.presentation.ui.screens.warehouse.stock_transfer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.models.warehouse.StockEntries
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DeleteIcon
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.ListHeading
import com.example.statspos.presentation.ui.components.ListImageView
import com.example.statspos.presentation.ui.components.ListLabel
import com.example.statspos.presentation.ui.components.ListMainLabel
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.warehouse.stock_transfer.StockTransferEntriesViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast

@Composable
fun NewStockTransferEntryBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Long) -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<StockTransferEntriesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
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

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                if (state.warehouse.id == 0L) {
                    viewModel.onEvent(UiEvent.ShowSnackbar("Please select warehouse"))
                } else {
                    onAddButtonClick(0L, false, state.warehouse.id)
                }
            }
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

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
                            warehouse = state.warehouse,
                            onWaerhouseSelected = { warehouse ->
                                viewModel.onWarehouseSelected(warehouse)
                                viewModel.loadData()
                            },
                        )
                    }
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { stockEntry ->
                            onAddButtonClick(stockEntry.id!!, true, state.warehouse.id)
                        },
                        onDeleteClick = { stockEntry ->
                            selectedId = stockEntry.id!!
                            showDeleteDialog = true
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        HeadingMedium(
                            text = "Total Items: ",
                        )
                        LabelMedium(
                            text = state.totalStockEntries.toString(),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    SaveButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        text = "Transfer to Warehouse"
                    ) {
                        viewModel.transferStock {
                            sharedViewModel.notifyDataChanged()
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SaveButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        text = "Receive from Warehouse"
                    ) {
                        viewModel.receiveStock {
                            sharedViewModel.notifyDataChanged()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    warehouse: DropdownItem?,
    onWaerhouseSelected: (DropdownItem) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            selectedItem = warehouse,
            items = HP.warehouses,
            onItemSelected = onWaerhouseSelected,
            label = {
                Text("Warehouse")
            },
            addNone = true,
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<StockEntries>,
    onItemClick: (StockEntries) -> Unit,
    onDeleteClick: (StockEntries) -> Unit,
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
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: StockEntries,
    onItemClick: (StockEntries) -> Unit,
    onDeleteClick: (StockEntries) -> Unit,
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
            ) {
                Spacer(Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                ListMainLabel(item.itemname.toString())
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        ListHeading("Qty: ")
                        ListLabel(HP.formatDecimal(item.qty))
                    }
                    if (HP.settings.saleCartons == true) {
                        Spacer(Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            ListHeading("Crtn: ")
                            ListLabel(item.crtn.toString())
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
    }
}

