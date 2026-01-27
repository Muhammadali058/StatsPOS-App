package com.example.statspos.presentation.ui.screens.main.main

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.screens.items.AddUpdateItemScreen
import com.example.statspos.presentation.ui.screens.items.CategoriesScreen
import com.example.statspos.presentation.ui.screens.purchase.PurchaseScreen
import com.example.statspos.presentation.viewmodels.items.SharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val sharedViewModel = hiltViewModel<SharedViewModel>()

    val backStack = rememberNavBackStack(TopRoutes.Home)
    val activity = LocalActivity.current as Activity
    BackHandler {
        if (backStack.size == 1) {
            activity.finish()
        }
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<TopRoutes.Home> {
                HomeScreen(
                    sharedViewModel= sharedViewModel,
                    onTopRouteClick = { key ->
                        if (backStack.lastOrNull() != key) {
                            backStack.add(key)
                        }
                    }
                )
            }
            entry<TopRoutes.AddUpdateItem> { key ->
                AddUpdateItemScreen(
                    sharedViewModel= sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                )
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
                ) {
                    Text("Add Sales")
                }
            }
        }
    )
}
