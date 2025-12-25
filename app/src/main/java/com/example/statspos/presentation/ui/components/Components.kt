@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.NavKey
import com.example.statspos.presentation.ui.screens.BottomRoutes
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.theme.backgroundDark
import com.example.statspos.presentation.ui.theme.backgroundLight
import com.example.statspos.presentation.ui.theme.primaryLight
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach


data class TopItem(
    val text: String,
    val screen: TopRoutes
)

data class BottomNavItem(
    val icon: ImageVector,
    val title: String,
)

val BOTTOM_DESTINATIONS = mapOf(
    BottomRoutes.Home to BottomNavItem(
        icon = Icons.Default.Home,
        title = "Home"
    ),
    BottomRoutes.Items to BottomNavItem(
        icon = Icons.Default.List,
        title = "Items"
    ),
    BottomRoutes.Sales to BottomNavItem(
        icon = Icons.Default.ShoppingCart,
        title = "Sales"
    ),
    BottomRoutes.Reports to BottomNavItem(
        icon = Icons.Default.AccountTree,
        title = "Reports"
    ),
)

@Composable
fun TopAppBar(
    title: String = "StatsPOS",
    navigationIcon:ImageVector = Icons.Default.ArrowBack,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigationClick:()-> Unit
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                CustomIcon(
                    icon = navigationIcon,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
fun BottomBar(
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
fun ChangeStatusBarColor(darkTheme: Boolean = false) {
    val colorScheme =  MaterialTheme.colorScheme
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
//        window.navigationBarColor = if(darkTheme) colorScheme.onSurface.toArgb() else backgroundLight.toArgb()
//        window.statusBarColor = if(darkTheme) colorScheme.onSurface.toArgb() else backgroundLight.toArgb()
        window.navigationBarColor = colorScheme.surface.toArgb()
        window.statusBarColor = colorScheme.surfaceVariant.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightNavigationBars = !darkTheme
            isAppearanceLightStatusBars = false
        }
    }
}