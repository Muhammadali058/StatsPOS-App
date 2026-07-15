package com.graphees.statspos.presentation.ui.screens.main.main

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.main.Branches
import com.graphees.statspos.presentation.ui.components.AppDropdownMenu
import com.graphees.statspos.presentation.ui.components.BOTTOM_DESTINATIONS
import com.graphees.statspos.presentation.ui.components.BottomBar
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.AppText
import com.graphees.statspos.presentation.ui.components.DropdownItem
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ImageView
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.components.TopItem
import com.graphees.statspos.presentation.ui.components.UpgradeToPremiumBottomSheet
import com.graphees.statspos.presentation.ui.utils.Navigator
import com.graphees.statspos.presentation.ui.utils.rememberNavigationState
import com.graphees.statspos.presentation.ui.utils.toEntries
import com.graphees.statspos.presentation.ui.screens.BottomRoutes
import com.graphees.statspos.presentation.ui.screens.TopRoutes
import com.graphees.statspos.presentation.ui.screens.items.ItemsScreen
import com.graphees.statspos.presentation.ui.screens.purchase.main_screen.PurchaseScreen
import com.graphees.statspos.presentation.ui.screens.reports.ReportsScreen
import com.graphees.statspos.presentation.ui.screens.sales.main_screen.SalesScreen
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.graphees.statspos.presentation.viewmodels.main.MainViewModel
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.ThemeMode
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val items = listOf(
    TopItem(
        "Categories",
        TopRoutes.Categories,
        R.drawable.categories,
        HP.userRights.categories == true
    ),
    TopItem("Packages", TopRoutes.Packages, R.drawable.packages, HP.userRights.items == true),
    TopItem(
        "Purchase\nOrders",
        TopRoutes.PurchaseOrders,
        R.drawable.purchase_orders,
        HP.userRights.purchase == true
    ),
    TopItem("Users", TopRoutes.Users, R.drawable.users, HP.userRights.users == true),
    TopItem("Settings", TopRoutes.Settings, R.drawable.settings, HP.userRights.settings == true),
    TopItem(
        "Sales\nOrders",
        TopRoutes.SalesOrders,
        R.drawable.sales_orders,
        (HP.userRights.sales == true && HP.client.hasShoppingApp == true)
    ),
)
private val accounts = listOf(
    TopItem(
        "Customers",
        TopRoutes.Customers,
        R.drawable.customers,
        HP.userRights.customers == true
    ),
    TopItem(
        "Vendors",
        TopRoutes.Vendors,
        R.drawable.vendors,
        HP.userRights.vendors == true
    ),
    TopItem(
        "Suppliers",
        TopRoutes.Suppliers,
        R.drawable.suppliers,
        HP.userRights.suppliers == true
    ),
    TopItem("Banks", TopRoutes.Banks, R.drawable.banks, HP.userRights.banks == true),
    TopItem(
        "Expenses",
        TopRoutes.Expenses,
        R.drawable.expenses,
        HP.userRights.expenses == true
    ),
    TopItem(
        "Account\nCategories",
        TopRoutes.AccountCategories,
        R.drawable.account_categories,
        (HP.userRights.customers == true || HP.userRights.vendors == true)
    ),
)
private val entries = listOf(
    TopItem(
        "Receipt",
        TopRoutes.ReceiptEntry,
        R.drawable.receipt_entry,
        access = HP.userRights.entry == true
    ),
    TopItem(
        "Payment",
        TopRoutes.PaymentEntry,
        R.drawable.payment_entry,
        access = HP.userRights.entry == true
    ),
    TopItem(
        "Expense",
        TopRoutes.ExpenseEntry,
        R.drawable.expense_entry,
        access = HP.userRights.entry == true
    ),
    TopItem(
        "Journal",
        TopRoutes.JournalEntry,
        R.drawable.journal_entry,
        access = HP.userRights.entry == true
    ),
    TopItem(
        "Stock",
        TopRoutes.StockEntry,
        R.drawable.stock_entry,
        access = HP.userRights.entry == true
    ),
)
private val warehouse = listOf(
    TopItem(
        "Warehouses",
        TopRoutes.Warehouses,
        R.drawable.warehouses,
        HP.userRights.warehouse == true
    ),
    TopItem(
        "Transfer\nStock",
        TopRoutes.TransferStock,
        R.drawable.transfer_stock,
        HP.userRights.warehouse == true
    ),
    TopItem(
        "Gatepass",
        TopRoutes.Gatepass,
        R.drawable.gatepass,
        HP.userRights.warehouse == true
    ),
)

@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel,
    onLogout: () -> Unit,
    onTopRouteClick: (NavKey) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localDataViewModel = hiltViewModel<LocalDataViewModel>()
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val activity = LocalActivity.current as Activity
    var branches by remember { mutableStateOf<List<Branches>>(emptyList()) }
    var showBranchesList by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showPremiumSheet by remember { mutableStateOf(false) }

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

    // Navigation
    val navigationState = rememberNavigationState(
        startRoute = BottomRoutes.Home,
        topLevelRoutes = BOTTOM_DESTINATIONS.keys,
        serializersModules = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(BottomRoutes.Home::class, BottomRoutes.Home.serializer())
                subclass(BottomRoutes.Items::class, BottomRoutes.Items.serializer())
                subclass(BottomRoutes.Sales::class, BottomRoutes.Sales.serializer())
                subclass(BottomRoutes.Purchase::class, BottomRoutes.Purchase.serializer())
                subclass(BottomRoutes.Reports::class, BottomRoutes.Reports.serializer())
            }
        }
    )
    val bottomNavigator = remember { Navigator(navigationState) }

    // Only for NavigationDrawer
    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    if (showPremiumSheet) {
        UpgradeToPremiumBottomSheet(
            onDismiss = {
                showPremiumSheet = false
            },
            onUpgradeClick = {
                onTopRouteClick(TopRoutes.Payment)
            },
            onContactClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:03030454625".toUri()
                }
                context.startActivity(intent)
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                viewModel = localDataViewModel,
                onClick = { key ->
                    onTopRouteClick(key)
                    scope.launch { drawerState.close() }
                },
                onUpgradeToPremium = {
                    showPremiumSheet = true
                    scope.launch { drawerState.close() }
                }
            )
        },
    ) {
        Scaffold(
            snackbarHost = {
                AppSnackbarHost(
                    snackbarHostState = snackbarHostState,
                )
            },
            topBar = {
                TopAppBar(
                    navigationIcon = Icons.Default.Menu,
                    onNavigationClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    actions = {
                        Row {
                            IconButton(
                                onClick = {
                                    showPremiumSheet = true
                                }
                            ) {
                                AppIcon(
                                    icon = Icons.Default.Refresh,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

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
                                if (HP.user.isMainUser == true) {
                                    DropdownItem(
                                        text = "Update branches",
                                        icon = {
                                            AppIcon(R.drawable.update, size = 20.dp)
                                        },
                                        onClick = {
                                            menuExpanded = false

                                            viewModel.updateBranches {
                                                context.showToast("Branches updated successfully")
                                            }
                                        }
                                    )
                                    DropdownItem(
                                        text = "Change branch",
                                        icon = {
                                            AppIcon(R.drawable.branch, size = 20.dp)
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            scope.launch {
                                                branches = localDataViewModel.getBranches().first()
                                                showBranchesList = true
                                            }
                                        }
                                    )
                                }

                                DropdownItem(
                                    text = "Reset",
                                    icon = {
                                        AppIcon(R.drawable.reset, size = 20.dp)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch {
                                            localDataViewModel.setClientId(0)
                                            activity.finish()
                                        }
                                    }
                                )

                                DropdownItem(
                                    text = "Logout",
                                    icon = {
                                        AppIcon(R.drawable.logout, size = 20.dp)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onLogout()
                                    }
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BottomBar(
                    items = BOTTOM_DESTINATIONS,
                    selectedKey = navigationState.selectedRoute,
                    onSelectKey = { key ->
                        bottomNavigator.navigate(key)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    Modifier
                        .fillMaxSize(),
                ) {
                    NavDisplay(
                        modifier = Modifier
                            .fillMaxSize(),
                        onBack = bottomNavigator::goBack,
                        entries = navigationState.toEntries(
                            entryProvider {
                                entry<BottomRoutes.Home> {
                                    HomeBody(
                                        onTopRouteClick = { key ->
                                            onTopRouteClick(key)
                                        }
                                    )
                                }
                                entry<BottomRoutes.Items> {
                                    ItemsScreen(
                                        sharedViewModel = sharedViewModel,
                                        onAddButtonClick = { updateId, isUpdate ->
                                            onTopRouteClick(
                                                TopRoutes.AddUpdateItem(
                                                    updateId,
                                                    isUpdate
                                                )
                                            )
                                        }
                                    )
                                }
                                entry<BottomRoutes.Sales> {
                                    SalesScreen(
                                        sharedViewModel = sharedViewModel,
                                        onViewClick = { salesBill ->
                                            onTopRouteClick(TopRoutes.ViewSalesBillItems(salesBill))
                                        },
                                        onAddUpdateButtonClick = { updateId, isPendingBill, isPostedBill, salesBill ->
                                            onTopRouteClick(
                                                TopRoutes.AddUpdateSales(
                                                    updateId,
                                                    isPendingBill,
                                                    isPostedBill,
                                                    salesBill
                                                )
                                            )
                                        }
                                    )
                                }
                                entry<BottomRoutes.Purchase> {
                                    PurchaseScreen(
                                        sharedViewModel = sharedViewModel,
                                        onViewClick = { purchaseBill ->
                                            onTopRouteClick(
                                                TopRoutes.ViewPurchaseBillItems(
                                                    purchaseBill
                                                )
                                            )
                                        },
                                        onAddUpdateButtonClick = { updateId, isPendingBill, isPostedBill, purchaseBill ->
                                            onTopRouteClick(
                                                TopRoutes.AddUpdatePurchase(
                                                    updateId,
                                                    isPendingBill,
                                                    isPostedBill,
                                                    purchaseBill
                                                )
                                            )
                                        }
                                    )
                                }
                                entry<BottomRoutes.Reports> {
                                    ReportsScreen(
                                        sharedViewModel = sharedViewModel,
                                        onTopRouteClick = { key ->
                                            onTopRouteClick(key)
                                        }
                                    )
                                }
                            }
                        )
                    )
                }

                if (showBranchesList) {
                    BranchesList(
                        branches = branches,
                        onDismiss = {
                            showBranchesList = false
                        },
                        onBranchClick = { branch ->
                            showBranchesList = false

                            if (DB.isOnlineMode) {
                                HP.branchId = branch.id!!
                                viewModel.loadMainData {
                                    sharedViewModel.notifyBranchChanged()
                                }
                            } else {
                                localDataViewModel.setBaseUrl(branch.baseUrl.toString())
                                context.showToast("Branch changed restart app")
                                onTopRouteClick(TopRoutes.CloseApp)
                            }
                        }
                    )
                }

                if (state.isLoading) {
                    ProgressBarLayout()
                }
            }
        }
    }
}

@Composable
fun NavigationDrawer(
    viewModel: LocalDataViewModel,
    onClick: (NavKey) -> Unit,
    onUpgradeToPremium: () -> Unit,
) {
    var darkModeChecked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val theme = viewModel.getTheme().first()
        darkModeChecked = when (theme) {
            ThemeMode.DARK -> true
            else -> {
                false
            }
        }
    }

    ModalDrawerSheet(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.systemBars
            )
            .width(280.dp),
        drawerShape = RectangleShape,
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageView(
                imageUrl = HP.user.imageUrl,
                showIfNull = true,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.select_image),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
            )

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier,
            ) {
                AppText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = HP.user.username ?: "Name",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                AppText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = HP.user.userTypeDescription ?: "User Type",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }

        HorizontalDivider(
            thickness = 1.5.dp,
            color = MaterialTheme.colorScheme.primary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(TopRoutes.UpdateUser)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(R.drawable.profile, size = 18.dp)
            Spacer(Modifier.width(16.dp))
            AppText(
                text = "Profile",
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }

        if (HP.userRights.users == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(TopRoutes.Users)
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(
                    icon = R.drawable.users,
                    size = 22.dp,
                )
                Spacer(Modifier.width(12.dp))
                AppText(
                    text = "Users",
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onUpgradeToPremium()
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                icon = Icons.Default.WorkspacePremium,
                size = 22.dp,
            )
            Spacer(Modifier.width(12.dp))
            AppText(
                text = "Upgrade to Premium",
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                AppIcon(R.drawable.dark_mode, size = 18.dp)
                Spacer(Modifier.width(16.dp))
                AppText(
                    text = "Night mode",
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
            }
            AppSwitch(
                label = "",
                checked = darkModeChecked,
                onCheckedChange = {
                    darkModeChecked = it
                    if (darkModeChecked)
                        viewModel.setTheme(ThemeMode.DARK)
                    else
                        viewModel.setTheme(ThemeMode.LIGHT)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBody(
    modifier: Modifier = Modifier,
    onTopRouteClick: (TopRoutes) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
            HomeGrid(items, onTopRouteClick)
        }

        item {
            Title("Create accounts", R.drawable.accounts, HP.userRights.accounts == true)
        }
        item {
            HomeGrid(accounts, onTopRouteClick)
        }

        item {
            Title("Entry", R.drawable.entry, HP.userRights.entry == true)
        }
        item {
            HomeGrid(entries, onTopRouteClick)
        }

        item {
            Title("Warehouse", R.drawable.warehouse, HP.userRights.warehouse == true)
        }
        item {
            HomeGrid(warehouse, onTopRouteClick)
        }

        item {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Title1(
    title: String,
    @DrawableRes icon: Int? = null,
    access: Boolean = true,
) {
    if (access) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            if (icon != null) {
                AppIcon(
                    icon = icon,
                    size = 22.dp,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun Title(
    title: String,
    @DrawableRes icon: Int? = null,
    access: Boolean = true,
) {
    if (access) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            if (icon != null) {
                AppIcon(
                    icon = icon,
                    size = 26.dp,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun HomeGrid1(
    items: List<TopItem>,
    onClick: (TopRoutes) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val filteredItems = items.filter { it.access }
        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .width(80.dp)
                    .height(92.dp)
                    .padding(vertical = 5.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(8.dp))
                    item.icon?.run {
                        AppIcon(
                            modifier = Modifier
                                .weight(1f),
                            icon = item.icon,
                            size = 20.dp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeGrid(
    items: List<TopItem>,
    onClick: (TopRoutes) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val filteredItems = items.filter { it.access }
        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(12.dp))
                    item.icon?.run {
                        AppIcon(
                            modifier = Modifier
                                .weight(1f),
                            icon = item.icon,
                            size = 26.dp,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BranchesList(
    branches: List<Branches>,
    onBranchClick: (Branches) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Select Branch:",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    )
                }
                items(branches) { branch ->
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        onClick = {
                            onBranchClick(branch)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = branch.branchName.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                maxLines = 1,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp)
                            )

                            if (DB.isOnlineMode) {
                                if (branch.id == HP.branchId) {
                                    Spacer(Modifier.width(8.dp))
                                    AppIcon(
                                        icon = Icons.Default.Done,
                                        size = 20.dp,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                            } else {
                                if (branch.baseUrl.equals(DB.HOST, ignoreCase = true)) {
                                    Spacer(Modifier.width(8.dp))
                                    AppIcon(
                                        icon = Icons.Default.Done,
                                        size = 20.dp,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {

        },
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

}

