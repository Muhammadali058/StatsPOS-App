package com.example.statspos.presentation.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.statspos.presentation.ui.components.CustomCheckbox
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.screens.TopRoutes

@Composable
fun SalesScreen(
    modifier: Modifier = Modifier,
    onClick: (NavKey) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var title by remember { mutableStateOf("") }
        OutlinedTextbox(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth(),
            label = "Itemname"
        )
        OutlinedTextbox(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth(),
            label = "Barcode"
        )

        var checked by remember { mutableStateOf(true) }
        CustomCheckbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )

        Button(
            onClick = {
                onClick(TopRoutes.AddSales)
            }
        ) {
            Text(
                text = "Add Sales"
            )
        }
    }
}