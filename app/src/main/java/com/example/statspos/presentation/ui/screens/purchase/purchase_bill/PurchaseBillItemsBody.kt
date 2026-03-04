package com.example.statspos.presentation.ui.screens.purchase.purchase_bill

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.models.purchase.PurchaseBillItems
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingLarge
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.ListImageView
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchBox
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.purchase.purchase_bill.AddUpdatePurchaseViewModel
import com.example.statspos.presentation.viewmodels.purchase.purchase_bill.PurchaseItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun PurchaseBillItemsBody(
    sharedViewModel: SharedViewModel,
    purchaseViewModel: AddUpdatePurchaseViewModel,
    purchaseItemsViewModel: PurchaseItemsViewModel,
    snackbarHostState: SnackbarHostState,
    onAddButtonClick: (Long, Boolean, Purchase) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val salesState by purchaseViewModel.state.collectAsStateWithLifecycle()
    val state by purchaseItemsViewModel.state.collectAsStateWithLifecycle()
    val event by purchaseItemsViewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = purchaseItemsViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
            hasLoadedOnce = true
        }
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
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

    fun getPurchaseObject(): Purchase {
        val purchase = purchaseViewModel.getFormData()
        purchase.id = salesState.invoiceId
        purchase.isPostedBill = salesState.isPostedBill
        purchase.invoiceNo = salesState.invoiceNo
        purchase.totalItems = state.list.size
        return purchase
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton {
                onAddButtonClick(0L, false, getPurchaseObject())
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
                        onValueChange = purchaseItemsViewModel::onSearchChange,
                        onSearchClick = {
                            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
                            keyboardController?.hide()
                        },
                    )
                    Spacer(Modifier.height(4.dp))
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            purchaseItemsViewModel.loadData(purchaseViewModel::updateTotal)
                        },
                        items = state.list,
                        onItemClick = { purchaseBillItem ->
                            onAddButtonClick(purchaseBillItem.id!!, true, getPurchaseObject())
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
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
    items: List<PurchaseBillItems>,
    onItemClick: (PurchaseBillItems) -> Unit,
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
    item: PurchaseBillItems,
    onItemClick: (PurchaseBillItems) -> Unit
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
                showIfNull = true,
            ) {
                Spacer(Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                // Itemname
                LabelLarge(item.itemname.toString())
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(.7f)
                    ) {
                        HeadingMedium(text = "Qty: ")
                        LabelMedium(text = HP.formatDecimal(item.qty))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium(text = "Cost: ")
                        LabelMedium(text = HP.formatDecimal(item.finalCost))
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
                            HeadingMedium(text = "Crtn: ")
                            LabelMedium(text = item.crtn.toString())
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Cost Crtn: ")
                            LabelMedium(text = if(item.crtn!! != 0) HP.formatDecimal((item.finalCost!! * item.crtnSize!!)) else "0")
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HeadingMedium(text = "Gross Cost: ")
                    LabelMedium(text = HP.formatDecimal(item.cost))
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Disc: ")
                    LabelMedium(text = HP.formatDecimal(item.calculatedDisc))
                    Spacer(Modifier.width(8.dp))
                    HeadingMedium(text = "Tax: ")
                    LabelMedium(text = HP.formatDecimal(item.tax))
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingLarge(text = "Total: ")
                        LabelLarge(text = HP.formatDecimal(item.total))
                    }
                }
            }
        }
    }
}

