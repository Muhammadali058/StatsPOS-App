package com.example.statspos.presentation.ui.screens.items

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.presentation.ui.components.AppFloatingActionButton
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.components.ListCard
import com.example.statspos.presentation.ui.components.PullToRefreshLayout
import com.example.statspos.presentation.ui.components.SearchTextbox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.ItemsViewModel
import com.example.statspos.presentation.viewmodels.items.SharedViewModel
import com.example.statspos.presentation.viewmodels.items.SubBarcodesViewModel
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubBarcodesScreen(
    sharedViewModel: SharedViewModel,
    itemId: Long,
    onBack: () -> Unit,
    onAddButtonClick: (Long, Boolean, Long) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<SubBarcodesViewModel>()
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
        viewModel.loadSubBarcodes()
    }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            viewModel.loadSubBarcodes()
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
                title = "Sub-Barcodes",
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
                    viewModel.loadSubBarcodes()
                    keyboardController?.hide()
                },
            )

            // Items List
            ItemsList(
                modifier = Modifier
                    .weight(1f),
                isRefreshing = state.isLoading,
                onRefresh = {
                    viewModel.loadSubBarcodes()
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
                    text = "Total Sub-Barcodes: ",
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
private fun ItemsList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    items: List<SubBarcodes>,
    onItemClick: (SubBarcodes) -> Unit,
) {
    PullToRefreshLayout(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
        ) {
            items(items) { item ->
                ListCard(item = item) {
                    onItemClick(it)
                }
            }
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: SubBarcodes,
    onItemClick: (SubBarcodes) -> Unit
) {
    ListCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            onItemClick(item)
        }
    ) {
        LabelLarge(item.subBarcode.toString())
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

        ItemsList(
            modifier = Modifier
                .weight(1f),
            isRefreshing = false,
            onRefresh = {

            },
            items = (1..50).map {
                SubBarcodes(
                    id = it.toLong(),
                    subBarcode = "$it"
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
                text = "Total Sub-Barcodes: ",
            )
            LabelMedium(
                text = "50.0",
            )
        }
    }
}
