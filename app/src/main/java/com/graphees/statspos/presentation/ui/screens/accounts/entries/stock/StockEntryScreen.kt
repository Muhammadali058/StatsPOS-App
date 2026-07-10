package com.graphees.statspos.presentation.ui.screens.accounts.entries.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.TabLayout
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.viewmodels.SharedViewModel

@Composable
fun StockEntryScreen(
    mainSharedViewModel: SharedViewModel,
    onSearchItemClick: () -> Unit,
    onBack: () -> Unit,
) {
    val tabs = listOf("New Entry", "Posted Entries")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                title = "Stock Entry",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabLayout(
                pagerState = pagerState,
                tabs = tabs,
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
            ) { page ->
                when (page) {
                    0 ->
                        NewStockEntryBody(
                            mainSharedViewModel = mainSharedViewModel,
                            sharedViewModel = sharedViewModel,
                            onSearchItemClick = onSearchItemClick,
                            snackbarHostState = snackbarHostState,
                        )

                    1 ->
                        StockPostedEntriesBody(
                            sharedViewModel = sharedViewModel,
                            snackbarHostState = snackbarHostState,
                        )
                }
            }
        }
    }
}
