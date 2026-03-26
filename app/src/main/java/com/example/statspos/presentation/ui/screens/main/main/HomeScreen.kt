package com.example.statspos.presentation.ui.screens.main.main

import android.app.Activity
import android.util.Log
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.domain.models.main.Branches
import com.example.statspos.presentation.ui.components.AppDropdownMenu
import com.example.statspos.presentation.ui.components.BOTTOM_DESTINATIONS
import com.example.statspos.presentation.ui.components.BottomBar
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.BottomNavItem
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ImageView
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.TopItem
import com.example.statspos.presentation.ui.utils.Navigator
import com.example.statspos.presentation.ui.utils.rememberNavigationState
import com.example.statspos.presentation.ui.utils.toEntries
import com.example.statspos.presentation.ui.screens.BottomRoutes
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.items.ItemsScreen
import com.example.statspos.presentation.ui.screens.purchase.main_screen.PurchaseScreen
import com.example.statspos.presentation.ui.screens.reports.ReportsScreen
import com.example.statspos.presentation.ui.screens.sales.main_screen.SalesScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.presentation.viewmodels.main.MainViewModel
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.ThemeMode
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.system.exitProcess

private val items = listOf(
    TopItem(
        "Categories",
        TopRoutes.Categories,
        R.drawable.categories,
        HP.userRights.categories == true
    ),
    TopItem("Packages", TopRoutes.Packages, R.drawable.package_icon, HP.userRights.items == true),
    TopItem(
        "Purchase\nOrders",
        TopRoutes.PurchaseOrders,
        R.drawable.package_icon,
        HP.userRights.purchase == true
    ),
    TopItem("Users", TopRoutes.Users, R.drawable.users, HP.userRights.users == true),
    TopItem("Settings", TopRoutes.Settings, R.drawable.settings, HP.userRights.settings == true),
)
private val accounts = listOf(
    TopItem(
        "Customers",
        TopRoutes.Customers,
        R.drawable.customer_accounts,
        HP.userRights.customers == true
    ),
    TopItem(
        "Vendors",
        TopRoutes.Vendors,
        R.drawable.vendor_accounts,
        HP.userRights.vendors == true
    ),
    TopItem(
        "Suppliers",
        TopRoutes.Suppliers,
        R.drawable.customer_accounts,
        HP.userRights.suppliers == true
    ),
    TopItem("Banks", TopRoutes.Banks, R.drawable.bank_accounts, HP.userRights.banks == true),
    TopItem(
        "Expenses",
        TopRoutes.Expenses,
        R.drawable.expense_accounts,
        HP.userRights.expenses == true
    ),
    TopItem(
        "Account\nCategories",
        TopRoutes.AccountCategories,
        R.drawable.categories,
        (HP.userRights.customers == true || HP.userRights.vendors == true)
    ),
)
private val entries = listOf(
    TopItem("Receipt", TopRoutes.ReceiptEntry, access = HP.userRights.entry == true),
    TopItem("Payment", TopRoutes.PaymentEntry, access = HP.userRights.entry == true),
    TopItem("Expense", TopRoutes.ExpenseEntry, access = HP.userRights.entry == true),
    TopItem("Journal", TopRoutes.JournalEntry, access = HP.userRights.entry == true),
    TopItem("Stock", TopRoutes.StockEntry, access = HP.userRights.entry == true),
)
private val warehouse = listOf(
    TopItem("Warehouses", TopRoutes.Warehouses, access = HP.userRights.warehouse == true),
    TopItem("Transfer Stock", TopRoutes.TransferStock, access = HP.userRights.warehouse == true),
    TopItem("Gatepass", TopRoutes.Gatepass, access = HP.userRights.warehouse == true),
)

@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel,
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

    var menuExpanded by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                viewModel = localDataViewModel,
                onClick = {

                },
                onUserClick = {
                    onTopRouteClick(TopRoutes.UpdateUser)
                    scope.launch { drawerState.close() }
                },
                onSettingsClick = {
                    onTopRouteClick(TopRoutes.Settings)
                    scope.launch { drawerState.close() }
                },
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
                                    DropdownMenuItem(
                                        text = { AppText("Update Branches") },
                                        onClick = {
                                            menuExpanded = false

                                            viewModel.updateBranches {
                                                context.showToast("Branches updated successfully")
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { AppText("Change Branch") },
                                        onClick = {
                                            menuExpanded = false
                                            scope.launch {
                                                branches = localDataViewModel.getBranches().first()
                                                showBranchesList = true
                                            }
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { AppText("Reset") },
                                    onClick = {
                                        menuExpanded = false
                                        scope.launch {
                                            localDataViewModel.setClientId(0)
                                            activity.finish()
                                        }
                                    }
                                )
//                                DropdownMenuItem(
//                                    text = { AppText("Exit App") },
////                                    leadingIcon = {
////                                        AppIcon(Icons.Default.ExitToApp)
////                                    },
//                                    onClick = {
//                                        menuExpanded = false
//                                        activity.finish()
//                                    }
//                                )
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
    onClick: (BottomNavItem) -> Unit,
    onUserClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
//    val items = listOf(
//        BottomNavItem(Icons.Default.Home, "Home"),
//        BottomNavItem(Icons.Default.Home, "Users"),
//        BottomNavItem(Icons.Default.Home, "Settings"),
//    )
    val items = emptyList<BottomNavItem>()

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
//                    .border(
//                        width = 2.dp,
//                        color = MaterialTheme.colorScheme.primary,
//                        shape = CircleShape
//                    )
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

//        NavigationDrawerItem(
//            label = { Text("Profile") },
//            selected = false,
//            onClick = {
//                onUserClick()
//            },
////            colors = navigationDrawerColors,
//        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onUserClick()
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(R.drawable.ic_user)
            Spacer(Modifier.width(16.dp))
            AppText("Profile")
        }

        if (HP.userRights.settings == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSettingsClick()
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(R.drawable.settings, size = 20.dp)
                Spacer(Modifier.width(16.dp))
                AppText("Settings")
            }
        }

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(item)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AppText(item.title)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText("Dark mode")

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
            Title("Accounts", R.drawable.accounts, HP.userRights.accounts == true)
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
//        if (icon != null) {
//            AppIcon(
//                icon = icon,
//                size = 24.dp,
//            )
//            Spacer(Modifier.width(16.dp))
//        }
            AppText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
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
                    item.icon?.run {
                        AppIcon(
                            icon = item.icon,
                            size = 30.dp,
                        )
                    }
//                    Spacer(Modifier.height(4.dp))
                    AppText(
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BranchesList(
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                onBranchClick(branch)
                            },
                    ) {
                        Text(
                            text = branch.branchName.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .padding(16.dp)
                        )
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
