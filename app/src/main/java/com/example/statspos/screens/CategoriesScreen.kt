package com.example.statspos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.statspos.ui.theme.StatsPOSTheme
import com.example.statspos.viewmodels.CategoriesViewModel


@Preview(showBackground = true)
@Composable
private fun Greeting(modifier: Modifier = Modifier) {

    val viewModel = viewModel<CategoriesViewModel>()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    StatsPOSTheme {

        Scaffold(

        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Hello World",
                )
                Button(
                    onClick = {
                        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Click")
                }

                OutlinedTextField(
                    modifier = Modifier,
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Name")
                    }
                )
            }
        }
    }
}