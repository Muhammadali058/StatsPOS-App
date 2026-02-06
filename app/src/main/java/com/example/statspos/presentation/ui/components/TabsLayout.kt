package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TabLayout(
    pagerState: PagerState,
    tabs: List<String>,
) {
    val scope = rememberCoroutineScope()
    
    SecondaryTabRow (
        selectedTabIndex = pagerState.currentPage,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(
                        selectedTabIndex = pagerState.currentPage,
                        matchContentSize = false
                    ),
                width = Dp.Unspecified,
                height = 2.dp
            )
        },
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(title) }
            )
        }
    }
}