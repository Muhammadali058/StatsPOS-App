package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.HeadingMedium
import com.example.statspos.presentation.ui.components.LabelLarge
import com.example.statspos.presentation.ui.components.LabelMedium
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.ItemsSharedViewModel
import com.example.statspos.presentation.viewmodels.items.ItemsViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun ItemsScreen(
    sharedViewModel: ItemsSharedViewModel,
    AddItemClick: (Long, Boolean) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

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
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))

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

            },
            icon = R.drawable.ic_barcode,
            buttonSize = 36.dp,
            size = 28.dp
        )
        Spacer(Modifier.width(4.dp))
        AppIconButton(
            onClick = {

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
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.onPrimary,
                color = MaterialTheme.colorScheme.primary,
                state = state
            )
        },
    ) {
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

//                if (i >= items.size - 1 && !endReached) {
//                    loadNextItems()
//                }

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
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable {
                    onItemClick(item)
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = HP.getImageUrl(item.imageUrl!!),
                    error = painterResource(R.drawable.item)
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                LabelLarge(item.itemname.toString())
                Spacer(Modifier.height(2.dp))
                Row {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium("Cost: ")
                        LabelMedium(item.cost.toString())
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium("Retail: ")
                        LabelMedium(item.retail.toString())
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium("W.Sale: ")
                        LabelMedium(item.wholesale.toString())
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        HeadingMedium("C.Rate: ")
                        LabelMedium(item.crtnRate.toString())
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
            onValueChange = {

            },
            onItemSelected = { itemname ->

            },
            onSearchClick = { text ->

            },
            onKeyboardAction = { text ->

            },
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
