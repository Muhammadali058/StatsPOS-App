package com.example.statspos.presentation.ui.screens.warehouse.stock_transfer

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.warehouse.WarehouseEntries
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.warehouse.stock_transfer.WarehouseEntriesViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import java.time.LocalDate

@Composable
fun WarehouseEntriesBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
    onClick: (Long) -> Unit,
) {
    val viewModel = hiltViewModel<WarehouseEntriesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
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
                    .padding(ConstantPaddings.BODY_HORIZONTAL)
            ) {
                Spacer(Modifier.height(8.dp))
                SearchBox(
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    warehouse = state.warehouse,
                    fromDate = state.fromDate,
                    toDate = state.toDate,
                    onWaerhouseSelected = { warehouse ->
                        viewModel.onWarehouseSelected(warehouse)
                        viewModel.loadData()
                    },
                    onFromDateChange = { date ->
                        viewModel.onFromDateChange(date)
                        viewModel.loadData()
                    },
                    onToDateChange = { date ->
                        viewModel.onToDateChange(date)
                        viewModel.loadData()
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
                    onItemClick = { entry ->
                        onClick(entry.id!!)
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                HeadingMedium(text = "Total Entries: ")
                LabelMedium(text = state.totalWarehouseEntries.toString())
            }
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    warehouse: DropdownItem?,
    fromDate: LocalDate,
    toDate: LocalDate,
    onWaerhouseSelected: (DropdownItem) -> Unit,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
        }
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<WarehouseEntries>,
    onItemClick: (WarehouseEntries) -> Unit
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
    item: WarehouseEntries,
    onItemClick: (WarehouseEntries) -> Unit
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
        ) {
            HeadingMedium("Date: ")
            LabelMedium(item.date.toString())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HeadingMedium("Type: ")
            LabelMedium(item.type.toString())
        }
    }
}

