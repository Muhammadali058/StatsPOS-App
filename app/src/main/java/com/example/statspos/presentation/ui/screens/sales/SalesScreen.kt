package com.example.statspos.presentation.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.ThemeMode

@Composable
fun SalesScreen(
    sharedViewModel: SharedViewModel,
    onClick: (NavKey) -> Unit,
) {
    var item by remember { mutableStateOf<Items?>(null) }

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            item = sharedViewModelState.item
            sharedViewModel.consumeDataChanged()
        }
    }

    Column{
        item?.run {
            Text(
                text = itemname!!
            )
        }
        Spacer(Modifier.height(16.dp))
        Body(
            onClick = { key ->
                onClick(key)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun Body(
    modifier: Modifier = Modifier,
    onClick: (NavKey) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                onClick(TopRoutes.SearchItem)
            }
        ) {
            Text("Search Item")
        }
    }
}
