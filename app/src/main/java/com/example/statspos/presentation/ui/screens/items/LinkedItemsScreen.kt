package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.items.LinkedItems
import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PullToRefreshList
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.items.LinkedItemsViewModel
import com.example.statspos.presentation.viewmodels.items.SubBarcodesViewModel
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedItemsScreen(
    sharedViewModel: SharedViewModel,
    itemId: Long,
    onBack: () -> Unit,
    onAddButtonClick: (Long, Boolean, Long) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<LinkedItemsViewModel>()
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

    LaunchedEffect(Unit) {
        viewModel.updateInitialState(itemId = itemId)
        viewModel.loadData()
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

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        floatingActionButton = {
            AppFloatingActionButton {
                onAddButtonClick(0L, false, itemId)
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = {
                    onBack()
                },
                title = "Linked Items",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Search Box
            Spacer(Modifier.height(8.dp))
            SearchBox(
                modifier = Modifier
                    .padding(bottom = 4.dp),
                value = state.search,
                onValueChange = viewModel::onSearchChange,
                onSearchClick = {
                    viewModel.loadData()
                    keyboardController?.hide()
                },
            )

            // List
            BodyList(
                modifier = Modifier
                    .weight(1f),
                isRefreshing = state.isLoading,
                onRefresh = {
                    viewModel.loadData()
                },
                items = state.list,
                onItemClick = { item ->
                    onAddButtonClick(item.id!!, true, itemId)
                }
            )

            // Total Items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                HeadingMedium(
                    text = "Total Linked Items: ",
                )
                LabelMedium(
                    text = state.totalLinkedItems.toString(),
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
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(ConstantPaddings.BODY_HORIZONTAL)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            onSearchClick = onSearchClick
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<LinkedItems>,
    onItemClick: (LinkedItems) -> Unit,
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
    item: LinkedItems,
    onItemClick: (LinkedItems) -> Unit
) {
    ListCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        onClick = {
            onItemClick(item)
        }
    ) {
        LabelLarge(item.itemname.toString())
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
            onSearchClick = {},
        )

        BodyList(
            modifier = Modifier
                .weight(1f),
            isRefreshing = false,
            onRefresh = {

            },
            items = (1..50).map {
                LinkedItems(
                    id = it.toLong(),
                    itemname = "Item $it"
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
                text = "Total Linked Items: ",
            )
            LabelMedium(
                text = "50.0",
            )
        }
    }
}
