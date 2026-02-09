package com.example.statspos.presentation.ui.screens.main.main

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppDropdownMenu
import com.example.statspos.presentation.ui.components.BOTTOM_DESTINATIONS
import com.example.statspos.presentation.ui.components.BottomBar
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.BottomNavItem
import com.example.statspos.presentation.ui.components.ImageView
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.TopItem
import com.example.statspos.presentation.ui.utils.Navigator
import com.example.statspos.presentation.ui.utils.rememberNavigationState
import com.example.statspos.presentation.ui.utils.toEntries
import com.example.statspos.presentation.ui.screens.BottomRoutes
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.items.ItemsScreen
import com.example.statspos.presentation.ui.screens.reports.ReportsScreen
import com.example.statspos.presentation.ui.screens.sales.SalesScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val items = listOf(
    TopItem("Purchase", TopRoutes.Purchase, R.drawable.purchase),
    TopItem("Categories", TopRoutes.Categories, R.drawable.categories),
    TopItem("Packages", TopRoutes.Packages, R.drawable.package_icon),
    TopItem("Purchase\nOrders", TopRoutes.PurchaseOrders, R.drawable.package_icon),
)
private val accounts = listOf(
    TopItem("Customers", TopRoutes.Customers, R.drawable.customer_accounts),
    TopItem("Vendors", TopRoutes.Vendors, R.drawable.vendor_accounts),
    TopItem("Suppliers", TopRoutes.Suppliers, R.drawable.customer_accounts),
    TopItem("Banks", TopRoutes.Banks, R.drawable.bank_accounts),
    TopItem("Expenses", TopRoutes.Expenses, R.drawable.expense_accounts),
)
private val entries = listOf(
    TopItem("Receipt", TopRoutes.Categories),
    TopItem("Payment", TopRoutes.Categories),
    TopItem("Expense", TopRoutes.Categories),
    TopItem("Journal", TopRoutes.Categories),
    TopItem("Stock", TopRoutes.Categories),
)
private val warehouse = listOf(
    TopItem("Warehouses", TopRoutes.Categories),
    TopItem("Transfer Stock", TopRoutes.Categories),
    TopItem("Gatepass", TopRoutes.Categories),
)

@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel,
    onTopRouteClick: (NavKey) -> Unit,
) {
    val viewModel = hiltViewModel<LocalDataViewModel>()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current as Activity

    // Navigation
    val navigationState = rememberNavigationState(
        startRoute = BottomRoutes.Home,
        topLevelRoutes = BOTTOM_DESTINATIONS.keys,
        serializersModules = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(BottomRoutes.Home::class, BottomRoutes.Home.serializer())
                subclass(BottomRoutes.Items::class, BottomRoutes.Items.serializer())
                subclass(BottomRoutes.Sales::class, BottomRoutes.Sales.serializer())
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
                viewModel = viewModel
            ) {

            }
        },
    ) {
        Scaffold(
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
                                DropdownMenuItem(
                                    text = { AppText("Exit App") },
                                    leadingIcon = {
                                        AppIcon(Icons.Default.ExitToApp)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        activity.finish()
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
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                                    onTopRouteClick(TopRoutes.AddUpdateItem(updateId, isUpdate))
                                }
                            )
                        }
                        entry<BottomRoutes.Sales> {
                            SalesScreen(
                                sharedViewModel = sharedViewModel,
                                onClick = { key ->
                                    onTopRouteClick(key)
                                }
                            )
                        }
                        entry<BottomRoutes.Reports> {
                            ReportsScreen()
                        }
                    }
                )
            )
        }
    }
}

@Composable
fun NavigationDrawer(
    viewModel: LocalDataViewModel,
    onClick: (BottomNavItem) -> Unit,
) {
    val items = listOf(
        BottomNavItem(Icons.Default.Home, "Home"),
        BottomNavItem(Icons.Default.Home, "Users"),
        BottomNavItem(Icons.Default.Home, "Settings"),
    )

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
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
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
//            NavigationDrawerItem(
//                label = { Text(item.title) },
//                selected = false,
//                onClick = {
//                    onClick(item)
//                },
//                colors = navigationDrawerColors,
//            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            Title("Accounts", R.drawable.accounts)
        }
        item {
            HomeGrid(accounts, onTopRouteClick)
        }

        item {
            Title("Entry")
        }
        item {
            HomeGrid(entries, onTopRouteClick)
        }

        item {
            Title("Warehouse", R.drawable.warehouse)
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
) {
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
        items(items) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
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
