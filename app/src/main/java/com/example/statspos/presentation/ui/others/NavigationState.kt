package com.example.statspos.presentation.ui.others

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule

class NavigationState(
    val startRoute: NavKey,
    selectedRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var selectedRoute by selectedRoute

    val stacksInUse: List<NavKey>
        get() = if(selectedRoute == startRoute)
            listOf(startRoute)
        else
            listOf(startRoute, selectedRoute)
}

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    serializersModules: SerializersModule
) : NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = SavedStateConfiguration {
            serializersModule = serializersModules
        },
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class)),
    ){
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = serializersModules
            },
            key
        )
    }

    return remember(startRoute, topLevelRoutes){
        NavigationState(
            startRoute = startRoute,
            selectedRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator(),
        )

        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    return stacksInUse.flatMap { key ->
        decoratedEntries[key] ?: emptyList()
    }.toMutableStateList()
}

//val serializerConfig = SavedStateConfiguration{
//    serializersModule = SerializersModule{
//        polymorphic(NavKey::class){
//            subclass(Route.TodoList::class, Route.TodoList.serializer())
//            subclass(Route.TodoDetail::class, Route.TodoDetail.serializer())
//            subclass(Route.TodoFavorite::class, Route.TodoFavorite.serializer())
//            subclass(Route.Settings::class, Route.Settings.serializer())
//        }
//    }
//}
