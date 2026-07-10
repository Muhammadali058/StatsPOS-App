package com.graphees.statspos.presentation.ui.screens.sales.sales_bill

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.sales.Sales
import com.graphees.statspos.domain.models.sales.SalesBillItems
import com.graphees.statspos.presentation.ui.components.AppFloatingActionButton
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DeleteIcon
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ListCard
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListImageView
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainHeading
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.PullToRefreshList
import com.graphees.statspos.presentation.ui.components.SearchBox
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesViewModel
import com.graphees.statspos.presentation.viewmodels.sales.sales_bill.SalesItemsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@Composable
fun SalesBillItemsBody(
    sharedViewModel: SharedViewModel,
    salesViewModel: AddUpdateSalesViewModel,
    salesItemsViewModel: SalesItemsViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Sales) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val salesState by salesViewModel.state.collectAsStateWithLifecycle()
    val state by salesItemsViewModel.state.collectAsStateWithLifecycle()
    val event by salesItemsViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = salesItemsViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    fun getSalesObject(): Sales {
        val sales = salesViewModel.getFormData()
        sales.id = salesState.invoiceId
        sales.isPostedBill = salesState.isPostedBill
        sales.invoiceNo = salesState.invoiceNo
        sales.totalItems = state.list.size
        return sales
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            salesItemsViewModel.loadData(salesViewModel::updateTotal) {
                if (HP.appSettings.fastSales == true) {
                    onAddButtonClick(0L, false, getSalesObject())
                }
//                if (salesItemsViewModel.state.value.list.isEmpty()) {
//                    onAddButtonClick(0L, false, getSalesObject())
//                }
            }
            salesItemsViewModel.setHasLoadedOnce(true)
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            salesItemsViewModel.loadData(salesViewModel::updateTotal)
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
                salesItemsViewModel.deleteData(selectedId) {
                    salesItemsViewModel.loadData(salesViewModel::updateTotal)
                    context.showToast("Item deleted successfully")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                onAddButtonClick(0L, false, getSalesObject())
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
                    SearchBox(
                        value = state.search,
                        onValueChange = {
                            salesItemsViewModel.onSearchChange(it, salesViewModel::updateTotal)
                        },
                        onSearchClick = {
                            salesItemsViewModel.loadData(salesViewModel::updateTotal)
                            keyboardController?.hide()
                        },
                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            salesItemsViewModel.loadData(salesViewModel::updateTotal)
                        },
                        items = state.list,
                        onItemClick = { salesBillItem ->
                            onAddButtonClick(salesBillItem.id!!, true, getSalesObject())
                        },
                        onDeleteClick = { salesBillItem ->
                            selectedId = salesBillItem.id!!
                            showDeleteDialog = true
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(vertical = 8.dp)
                ) {
                    HeadingMedium(text = "Items: ")
                    LabelMedium(text = state.totalItems.toString())
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Qty: ")
                    LabelMedium(text = HP.formatDecimal(state.totalQty))
                    if (HP.settings.saleCartons == true) {
                        Spacer(Modifier.width(8.dp))
                        HeadingMedium(text = "Crtn: ")
                        LabelMedium(text = state.totalCrtn.toString())
                    }
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Disc: ")
                    LabelMedium(text = HP.formatDecimal(state.totalItemDisc))
                }
            }
        }
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<SalesBillItems>,
    onItemClick: (SalesBillItems) -> Unit,
    onDeleteClick: (SalesBillItems) -> Unit,
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
    item: SalesBillItems,
    onItemClick: (SalesBillItems) -> Unit,
    onDeleteClick: (SalesBillItems) -> Unit,
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
            verticalAlignment = Alignment.Top,
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

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // Itemname
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ListMainLabel(
                        modifier = Modifier
                            .weight(1f),
                        text = item.itemname.toString()
                    )
                    Spacer(Modifier.width(8.dp))
                    DeleteIcon {
                        onDeleteClick(item)
                    }
                }

                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        ListHeading(text = "Qty: ")
                        ListLabel(text = HP.formatDecimal(item.qty))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        ListHeading(text = "Rate: ")
                        ListLabel(text = HP.formatDecimal(item.rate))
                    }
                }
                if (HP.settings.saleCartons == true) {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(.7f)
                        ) {
                            ListHeading(text = "Crtn: ")
                            ListLabel(text = item.crtn.toString())
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            ListHeading(text = "Crtn Rate: ")
                            ListLabel(text = HP.formatDecimal(item.crtnRate))
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        ListHeading(text = "Disc: ")
                        ListLabel(text = HP.formatDecimal(item.disc))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        ListMainHeading(text = "Total: ")
                        ListMainLabel(text = HP.formatDecimal(item.total))
                    }
                }
            }
        }
    }
}

