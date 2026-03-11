package com.example.statspos.presentation.ui.screens.warehouse.gatepass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.warehouse.GatepassVoucher
import com.example.statspos.domain.models.warehouse.Gatepasses
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.BottomHeading
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.openPdf
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.warehouse.gatepass.GatepassViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import java.time.LocalDate

@Composable
fun GatepassBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Long, String) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<GatepassViewModel>()
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

    fun showVoucher(
        gatepass: List<GatepassVoucher>,
    ) {
        val file = gatepassVoucher(
            context = context,
            gatepass = gatepass,
        )

        openPdf(context, file)
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                if (state.warehouse.id == 0L) {
                    viewModel.onEvent(UiEvent.ShowSnackbar("Please select warehouse"))
                } else {
                    onAddButtonClick(0L, false, state.warehouse.id, HP.getZonedDate(state.date))
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
                            value = state.search,
                            onValueChange = viewModel::onSearchChange,
                            onSearchClick = {
                                viewModel.loadData()
                                keyboardController?.hide()
                            },
                            warehouse = state.warehouse,
                            onWaerhouseSelected = { warehouse ->
                                viewModel.onWarehouseSelected(warehouse)
                                viewModel.loadData()
                            },
                            date = state.date,
                            onDateChange = { date ->
                                viewModel.onDateChange(date)
                                viewModel.loadData()
                            },
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            viewModel.loadData()
                        },
                        items = state.list,
                        onItemClick = { gatepass ->
                            onAddButtonClick(
                                gatepass.id!!,
                                true,
                                state.warehouse.id,
                                HP.getZonedDate(state.date)
                            )
                        },
                        onPrintClick = { gatepass ->
                            viewModel.getGatepass(
                                gatepassId = gatepass.id!!,
                                onSuccess = { gatepasses ->
                                    showVoucher(gatepasses)
                                }
                            )
                        }
                    )
                }

                BottomHeading(
                    text = "Total Gatepass: ",
                    value = state.totalGatepasses.toString()
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
    onSearchClick: (String) -> Unit,
    warehouse: DropdownItem?,
    onWaerhouseSelected: (DropdownItem) -> Unit,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
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
        DateTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            date = date,
            onDateChange = onDateChange,
            label = "Date"
        )
        SearchTextbox(
            value = value,
            onValueChange = onValueChange,
            onSearchClick = onSearchClick,
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<Gatepasses>,
    onItemClick: (Gatepasses) -> Unit,
    onPrintClick: (Gatepasses) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        items(items) { item ->
            ListCard(
                item = item,
                onItemClick = onItemClick,
                onPrintClick = onPrintClick,
            )
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: Gatepasses,
    onItemClick: (Gatepasses) -> Unit,
    onPrintClick: (Gatepasses) -> Unit,
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
            Column {
                LabelLarge(item.gatepassName.toString())
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            HeadingMedium("Date: ")
                            LabelMedium(item.date.toString())
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            HeadingMedium("Remarks: ")
                            LabelMedium(item.remarks.toString())
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    AppIconButton(
                        icon = Icons.Default.Print,
                        onClick = {
                            onPrintClick(item)
                        }
                    )
                }
            }
        }
    }
}

