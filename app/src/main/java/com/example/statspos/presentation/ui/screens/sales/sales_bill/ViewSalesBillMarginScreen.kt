package com.example.statspos.presentation.ui.screens.sales.sales_bill

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
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.sales.Sales
import com.example.statspos.domain.models.sales.SalesBillItems
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
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.accounts.banks.BanksViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.AddUpdateSalesViewModel
import com.example.statspos.presentation.viewmodels.sales.sales_bill.SalesItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun ViewSalesBillMarginScreen(
    invoiceId: Long,
    isPostedBill: Boolean,
    totalDisc: Double,
    onBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SalesItemsViewModel>()
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

    fun loadData() {
        viewModel.loadData { a, b ->

        }
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedOnce) {
            viewModel.updateInitialState(invoiceId, isPostedBill)
            loadData()
            hasLoadedOnce = true
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
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Total Profit: ${
                    HP.formatDecimal(
                        rate = (state.totalProfit - totalDisc),
                        mustDecimals = 1,
                    )
                }",
            )
        },
    ) { innerPadding ->
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
                        value = state.search,
                        onValueChange = {
                            viewModel.onSearchChange(it, {a, b->})
                        },
                        onSearchClick = {
                            loadData()
                            keyboardController?.hide()
                        },
                    )
                    BodyList(
                        modifier = Modifier
                            .weight(1f)
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            loadData()
                        },
                        items = state.list,
                        onItemClick = { salesBillItem ->

                        }
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
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Total Bill: ")
                            LabelMedium(text = HP.formatDecimal(state.totalBill))
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Total Cost: ")
                            LabelMedium(text = HP.formatDecimal(state.totalCost))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Gross Profit: ")
                            LabelMedium(text = HP.formatDecimal(state.totalProfit))
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            HeadingMedium(text = "Bill Discount: ")
                            LabelMedium(text = HP.formatDecimal(totalDisc))
                        }
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
    items: List<SalesBillItems>,
    onItemClick: (SalesBillItems) -> Unit,
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
            ListCard(item = item) {
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: SalesBillItems,
    onItemClick: (SalesBillItems) -> Unit
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
                        HeadingMedium(text = "Rate: ")
                        LabelMedium(text = HP.formatDecimal(item.rate))
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
                            HeadingMedium(text = "Crtn Rate: ")
                            LabelMedium(text = HP.formatDecimal(item.crtnRate))
                        }
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
                        HeadingMedium(text = "Disc: ")
                        LabelMedium(text = HP.formatDecimal(item.disc))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium(text = "Total: ")
                        LabelMedium(text = HP.formatDecimal(item.total))
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
                        HeadingMedium(text = "Cost: ")
                        LabelMedium(text = HP.formatDecimal(item.cost))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingLarge(text = "Profit: ")
                        LabelLarge(text = HP.formatDecimal(item.profit))
                    }
                }
            }
        }
    }
}

