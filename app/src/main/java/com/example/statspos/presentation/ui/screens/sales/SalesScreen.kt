package com.example.statspos.presentation.ui.screens.sales

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.components.AutoCompleteTextbox
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.viewmodels.main.LoginViewModel
import com.example.statspos.utils.showToast

@Composable
fun SalesScreen(
    modifier: Modifier = Modifier,
    onClick: (NavKey) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val items = listOf(
            DropdownItem(0,"None"),
            DropdownItem(1,"Sugar"),
            DropdownItem(2,"Daal Chana"),
            DropdownItem(3,"Daal Mash"),
            DropdownItem(4,"Sprite"),
            DropdownItem(5,"Coca Cola"),
            DropdownItem(6,"Olivia Color"),
            DropdownItem(7,"Masar sabat Color"),
        )

        Dropdown(
            modifier = Modifier,
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            items = items,
            onItemSelected = {
                context.showToast(it.name)
            },
            label = {
                Text("Select Category")
            },
        )

        Button(
            onClick = {

            }
        ) {
            Text("Button")
        }
    }
}
