@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.screens.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.components.Navigator
import com.example.statspos.presentation.ui.components.rememberNavigationState
import com.example.statspos.presentation.ui.components.toEntries
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Route : NavKey{

    @Serializable
    data object TodoList: Route

    @Serializable
    data object TodoFavorite: Route

    @Serializable
    data class TodoDetail(val todo: String): Route

    @Serializable
    data object Settings: Route

}

data class BottomNavItem(
    val icon: ImageVector,
    val title: String,
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.TodoList to BottomNavItem(
        icon = Icons.Outlined.Checklist,
        title = "Todos"
    ),
    Route.TodoFavorite to BottomNavItem(
        icon = Icons.Outlined.Favorite,
        title = "Favorite"
    ),
    Route.Settings to BottomNavItem(
        icon = Icons.Outlined.Settings,
        title = "Settings"
    ),
)

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.TodoList,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys,
        serializersModules = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(Route.TodoList::class, Route.TodoList.serializer())
                subclass(Route.TodoDetail::class, Route.TodoDetail.serializer())
                subclass(Route.TodoFavorite::class, Route.TodoFavorite.serializer())
                subclass(Route.Settings::class, Route.Settings.serializer())
            }
        }
    )

    val navigator = remember { Navigator(navigationState) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("StatsPOS")
                }
            )
        },
        bottomBar = {
            TodoNavigationBar(
                selectedKey = navigationState.selectedRoute,
                onSelectKey = { key ->
                    navigator.navigate(key)
                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.TodoList> {
                        TodoListScreen()
                    }
                    entry<Route.TodoFavorite> {
                        TodoFavoriteScreen {
                            navigator.navigate(Route.TodoDetail(it))
                        }
                    }
                    entry<Route.TodoDetail> { key ->
                        TodoDetailScreen(
                            todo = key.todo
                        )
                    }
                    entry<Route.Settings> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Settings")
                        }
                    }
                }
            )
        )
    }
}