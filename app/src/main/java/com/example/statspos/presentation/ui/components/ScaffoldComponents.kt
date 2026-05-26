@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.statspos.R
import com.example.statspos.presentation.ui.screens.BottomRoutes
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.utils.SnackbarType
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class TopItem(
    val text: String,
    val screen: TopRoutes,
    @DrawableRes val icon: Int? = null,
    val access: Boolean = true,
)

data class BottomNavItem(
    @DrawableRes val icon: Int,
    val title: String,
)

val BOTTOM_DESTINATIONS = mapOf(
    BottomRoutes.Home to BottomNavItem(
        icon = R.drawable.home,
        title = "Home"
    ),
    BottomRoutes.Items to BottomNavItem(
        icon = R.drawable.items,
        title = "Items"
    ),
    BottomRoutes.Sales to BottomNavItem(
        icon = R.drawable.sales,
        title = "Sales"
    ),
    BottomRoutes.Purchase to BottomNavItem(
        icon = R.drawable.purchase,
        title = "Purchase"
    ),
    BottomRoutes.Reports to BottomNavItem(
        icon = R.drawable.reports,
        title = "Reports"
    ),
)

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String = "StatsPOS",
    navigationIcon: ImageVector = Icons.Default.ArrowBack,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigationClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                AppIcon(
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
    val selectedColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface
    val foregroundColor = MaterialTheme.colorScheme.onSurface
//    val foregroundColor = if (HP.darkTheme)
//        MaterialTheme.colorScheme.onSurface.copy(0.5f)
//    else
//        MaterialTheme.colorScheme.onSurface


//    NavigationBar
//    val customColors = NavigationBarItemDefaults.colors(
//        indicatorColor = selectedColor,
//        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
//        unselectedIconColor = foregroundColor,
//        selectedTextColor = foregroundColor,
//        unselectedTextColor = foregroundColor,
//    )
//    NavigationBar(
//        modifier = modifier,
//        containerColor = backgroundColor,
//        contentColor = foregroundColor,
//        ) {
//        items.forEach { (key, data) ->
//            val selected = selectedKey == key
//            NavigationBarItem(
//                selected = selected,
//                onClick = { onSelectKey(key) },
//                colors = customColors,
//                icon = {
//                    Icon(
//                        imageVector = data.icon,
//                        contentDescription = data.title,
//                    )
//                },
//                label = {
//                    Text(
//                        text = data.title,
//                        fontSize = 12.sp,
//                    )
//                }
//            )
//        }
//    }

//    Custom NavigationBar
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
//                AnimatedVisibility(
//                    visible = selected,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .height(2.dp)
//                            .fillMaxWidth(0.5f)
////                            .clip(RoundedCornerShape(2.dp))
//                            .background(selectedColor)
//                    )
//                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = if (selected) 2.dp else 1.dp,
                    color = if (selected) selectedColor else MaterialTheme.colorScheme.surfaceDim,
//                    color = if (selected) selectedColor else MaterialTheme.colorScheme.primaryContainer,
                )

                Spacer(Modifier.height(6.dp))

                Icon(
                    painter = painterResource(data.icon),
                    contentDescription = data.title,
                    tint = if (selected)
                        selectedColor
                    else
                        foregroundColor,
                    modifier = Modifier
                        .size(20.dp)
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
fun AppSnackbarHost(
    snackbarHostState: SnackbarHostState,
    currentSnackbarType: SnackbarType = SnackbarType.INFORMATION,
    modifier: Modifier? = null,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter),
    )
    { data ->
        val backgroundColor = when (currentSnackbarType) {
            SnackbarType.INFORMATION -> MaterialTheme.colorScheme.primary
            SnackbarType.ERROR -> Color.Red
        }

        val customModifier = modifier ?: Modifier
            .statusBarsPadding()
            .padding(top = 60.dp)

        Snackbar(
            snackbarData = data,
            modifier = customModifier,
//            modifier = Modifier
//                .statusBarsPadding()
//                .padding(top = 60.dp),
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(8.dp),
        )
    }
}

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    tonalElevation: Dp = 0.dp,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = tonalElevation,
        shape = shape,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp),
//                horizontalArrangement = Arrangement.End,
//            ) {
//                AppIcon(
//                    icon = Icons.Default.Clear,
//                    modifier = Modifier.size(30.dp)
//                )
//            }
        },
    ) {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = false
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ConstantPaddings.BODY_HORIZONTAL),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = containerColor,
    ) {
        content()
    }
}