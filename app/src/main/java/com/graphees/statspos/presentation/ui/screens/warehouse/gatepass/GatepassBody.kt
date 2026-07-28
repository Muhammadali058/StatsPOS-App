package com.graphees.statspos.presentation.ui.screens.warehouse.gatepass

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.warehouse.GatepassVoucher
import com.graphees.statspos.domain.models.warehouse.Gatepasses
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.AppIconButton
import com.graphees.statspos.presentation.ui.components.BottomHeading
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PlaceHolder
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.components.SearchTextbox
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.warehouse.gatepass.GatepassViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
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
            text = "Are you sure to delete this gatepass",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(selectedId) {
                    selectedId = 0L
                    context.showToast("Gatepass deleted successfully")
                }
            }
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
                        },
                        onDeleteClick = { gatepass ->
                            selectedId = gatepass.id!!
                            showDeleteDialog = true
                        },
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
            placeholder = {
                PlaceHolder(text = "Warehouse")
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
    onDeleteClick: (Gatepasses) -> Unit,
) {
    PullToRefreshList(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        item{
            Spacer(Modifier.height(4.dp))
        }
        items(items) { item ->
            ListCard(
                item = item,
                onItemClick = onItemClick,
                onPrintClick = onPrintClick,
                onDeleteClick = onDeleteClick,
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
    onDeleteClick: (Gatepasses) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

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
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // region Name
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
                            text = item.gatepassName.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    if (HP.userRights.deleteAnything == true) {
                        Spacer(Modifier.width(8.dp))
                        DeleteIcon {
                            onDeleteClick(item)
                        }
                    }
                }
                // endregion

                Spacer(Modifier.height(4.dp))
                ListHorizontalDivider()
                Spacer(Modifier.height(4.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListHeading("Date: ", color = primaryColor)
                            ListLabel(item.date.toString(), color = secondaryColor)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            ListHeading("Remarks: ", color = primaryColor)
                            ListLabel(item.remarks.toString(), color = secondaryColor)
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

