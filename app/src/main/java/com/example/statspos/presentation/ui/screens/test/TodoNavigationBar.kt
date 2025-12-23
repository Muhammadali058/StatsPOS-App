package com.example.statspos.presentation.ui.screens.test

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

@Composable
fun TodoNavigationBar(
    selectedKey: NavKey,
    onSelectKey:(NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (key, data) ->
            NavigationBarItem(
                selected = selectedKey == key,
                onClick = {
                    onSelectKey(key)
                },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.title
                    )
                },
                label = {
                    Text(
                        text = data.title
                    )
                }
            )
        }
    }
}