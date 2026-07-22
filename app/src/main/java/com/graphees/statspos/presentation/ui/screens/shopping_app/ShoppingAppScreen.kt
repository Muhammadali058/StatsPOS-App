package com.graphees.statspos.presentation.ui.screens.shopping_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.presentation.ui.components.AppDropdownMenu
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.DropdownItem
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.shopping_app.sales_orders.SalesOrderItemsScreen
import com.graphees.statspos.presentation.ui.screens.shopping_app.sales_orders.SalesOrdersScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.shopping_app.ShoppingAppViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import kotlinx.serialization.Serializable

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object SalesOrdersScreen : Routes()

    @Serializable
    data class ViewOrderItems(val salesOrder: SalesOrders) : Routes()

    @Serializable
    data object Login : Routes()
}

@Composable
fun ShoppingAppScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.Home> {
                Home(
                    onLogin = {
                        navigate(Routes.Login)
                    },
                    onSalesOrdersClick = {
                        navigate(Routes.SalesOrdersScreen)
                    },
                    onBack = {
                        onBack()
                    },
                )
            }
            entry<Routes.Login> { key ->
                ShoppingAppLoginScreen(
                    onLogin = {
                        backStack.removeLastOrNull()
                        onBack()
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Routes.SalesOrdersScreen> { key ->
                SalesOrdersScreen (
                    onViewOrderItemsClick = { salesOrder ->
                        navigate(Routes.ViewOrderItems(salesOrder))
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.ViewOrderItems> { key ->
                SalesOrderItemsScreen(
                    sharedViewModel = sharedViewModel,
                    salesOrder = key.salesOrder,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}

@Composable
private fun Home(
    onLogin: () -> Unit,
    onSalesOrdersClick: () -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = hiltViewModel<ShoppingAppViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

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

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    // Show Login screen
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            if (!viewModel.isUserLoggedIn()) {
                onLogin()
            }
            viewModel.setHasLoadedOnce(true)
        }
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Shopping App",
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                menuExpanded = true
                            }
                        ) {
                            AppIcon(
                                icon = Icons.Default.MoreVert,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        AppDropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .width(200.dp),
                        ) {
                            DropdownItem(
                                text = "Sign Out",
                                onClick = {
                                    menuExpanded = false
                                    viewModel.signOut {
                                        onBack()
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onSalesOrdersClick()
                        }
                    ) {
                        Text("Sales Orders")
                    }
                }
            }
        }
    }
}
