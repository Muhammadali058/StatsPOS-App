package com.example.statspos.presentation.ui.screens.main.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.components.BOTTOM_DESTINATIONS
import com.example.statspos.presentation.ui.components.BottomBar
import com.example.statspos.presentation.ui.components.AppIcon
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
import com.example.statspos.presentation.viewmodels.items.ItemsSharedViewModel
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.ThemeMode
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun HomeScreen(
    sharedViewModel: ItemsSharedViewModel,
    onTopRouteClick: (NavKey) -> Unit,
) {
    val viewModel = hiltViewModel<LocalDataViewModel>()

    // Navigation
    val navigationState = rememberNavigationState(
        startRoute = BottomRoutes.Items,
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
            TopAppBar(
                navigationIcon = Icons.Default.Menu,
                actions = {
                    IconButton(onClick = {
                        viewModel.setTheme(ThemeMode.DARK)
                    }) {
                        AppIcon(
                            icon = Icons.Default.MoreVert,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                onNavigationClick = {
                    viewModel.setTheme(ThemeMode.LIGHT)
                }
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
                            onTopRouteClick = { screen ->
                                onTopRouteClick(screen)
                            }
                        )
                    }
                    entry<BottomRoutes.Items> {
                        ItemsScreen(
                            sharedViewModel= sharedViewModel,
                            AddItemClick = { updateId, isUpdate ->
                                onTopRouteClick(TopRoutes.AddUpdateItem(updateId, isUpdate))
                            }
                        )
                    }
                    entry<BottomRoutes.Sales> {
                        SalesScreen { key ->
                            onTopRouteClick(key)
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
private fun HomeBody(
    modifier: Modifier = Modifier,
    onTopRouteClick: (screen: TopRoutes) -> Unit = {}
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
            .background(MaterialTheme.colorScheme.background)
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
                        .size(100.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onTopRouteClick(item.screen)
                            }
                            .background(MaterialTheme.colorScheme.primaryContainer)
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
