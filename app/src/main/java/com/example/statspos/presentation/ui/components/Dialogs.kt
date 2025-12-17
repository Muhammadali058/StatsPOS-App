package com.example.statspos.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun InfoDialog(
    modifier: Modifier = Modifier,
    title: String = "Info",
    text: String = "",
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
    )
}

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    title: String = "Info",
    text: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        },
    )
}