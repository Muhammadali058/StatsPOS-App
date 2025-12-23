@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.screens.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.Navigator
import com.example.statspos.presentation.ui.components.rememberNavigationState
import com.example.statspos.presentation.ui.components.toEntries
import com.example.statspos.presentation.ui.screens.BottomRoutes
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.items.CategoriesScreen
import com.example.statspos.presentation.ui.screens.items.ItemsScreen
import com.example.statspos.presentation.ui.screens.purchase.PurchaseScreen
import com.example.statspos.presentation.ui.screens.reports.ReportsScreen
import com.example.statspos.presentation.ui.screens.sales.SalesScreen
import com.example.statspos.presentation.ui.theme.backgroundVariantLight
import com.example.statspos.presentation.ui.theme.primaryLight
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private data class TopItem(
    val text: String,
    val screen: TopRoutes
)

private data class BottomNavItem(
    val icon: ImageVector,
    val title: String,
)

private val BOTTOM_DESTINATIONS = mapOf(
    BottomRoutes.Home to BottomNavItem(
        icon = Icons.Default.Home,
        title = "Home"
    ),
    BottomRoutes.Items to BottomNavItem(
        icon = Icons.Default.ShoppingCart,
        title = "Items"
    ),
    BottomRoutes.Sales to BottomNavItem(
        icon = Icons.Default.List,
        title = "Sales"
    ),
    BottomRoutes.Reports to BottomNavItem(
        icon = Icons.Default.AccountTree,
        title = "Reports"
    ),
)

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(TopRoutes.Home)
    val activity = LocalActivity.current as Activity
    BackHandler {
        if (backStack.size == 1) {
            activity.finish()
        }
    }
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<TopRoutes.Home> {
                Main { key ->
                    backStack.add(key)
                }
            }
            entry<TopRoutes.Categories> {
                CategoriesScreen()
            }
            entry<TopRoutes.Purchase> {
                PurchaseScreen()
            }
            entry<TopRoutes.AddSales> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue),
                    contentAlignment = Alignment.Center
                ){
                    Text("Add Sales")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Main(
    onClick: (NavKey) -> Unit = {},
) {
    // Change Status Bar Color
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = primaryLight.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
//            isAppearanceLightNavigationBars = !darkTheme
            isAppearanceLightStatusBars = false
        }
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
                subclass(BottomRoutes.Reports::class, BottomRoutes.Reports.serializer())
            }
        }
    )
    val bottomNavigator = remember { Navigator(navigationState) }
    Scaffold(
        topBar = {
            TopAppBar()
        },
        bottomBar = {
            CustomBottomBar(
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
                        Body(
                            onClick = { screen ->
                                onClick(screen)
                            }
                        )
                    }
                    entry<BottomRoutes.Items> {
                        ItemsScreen()
                    }
                    entry<BottomRoutes.Sales> {
                        SalesScreen{key ->
                            onClick(key)
                        }
                    }
                    entry<BottomRoutes.Reports> {
                        ReportsScreen()
                    }
                }
            )
        )
    }
}

@Composable
private fun CustomBottomBar(
    items: Map<BottomRoutes, BottomNavItem>,
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val foregroundColor = MaterialTheme.colorScheme.onSurface
    val selectedColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .navigationBarsPadding()
            .height(54.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (key, data) ->
            val selected = selectedKey == key

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelectKey(key) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(0.5f)
//                            .clip(RoundedCornerShape(2.dp))
                            .background(selectedColor)
                    )
                }

                Spacer(Modifier.height(6.dp))

                Icon(
                    imageVector = data.icon,
                    contentDescription = data.title,
                    tint = if (selected)
                        selectedColor
                    else
                        foregroundColor
                )

                Text(
                    text = data.title,
                    fontSize = 12.sp,
                    color = if (selected)
                        selectedColor
                    else
                        foregroundColor
                )
            }
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    onClick: (screen: TopRoutes) -> Unit = {}
) {
    val items = listOf(
        TopItem("Categories", TopRoutes.Categories),
        TopItem("Purchase", TopRoutes.Purchase),
        TopItem("Warehouses", TopRoutes.Categories),
        TopItem("Customers", TopRoutes.Categories),
        TopItem("Vendors", TopRoutes.Categories),
        TopItem("Suppliers", TopRoutes.Categories),
        TopItem("Banks", TopRoutes.Categories),
        TopItem("Expenses", TopRoutes.Categories),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundVariantLight)
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .height(100.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onClick(item.screen)
                            }
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = item.text,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text("StatsPOS")
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                CustomIcon(
                    icon = Icons.Default.Menu,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                CustomIcon(
                    icon = Icons.Default.MoreVert,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
//            IconButton(onClick = {  }) {
//                CustomIcon(
//                    icon = Icons.Default.Favorite,
//                    tint = MaterialTheme.colorScheme.onPrimary,
//                )
//            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}