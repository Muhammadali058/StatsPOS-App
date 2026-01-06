@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDatePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.now()
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val localDate =
                    Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: return@TextButton)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                onSelected(localDate)
                onDismiss()
            }) {
                Text("Ok")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
                headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dividerColor = MaterialTheme.colorScheme.primary,
                navigationContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            showModeToggle = false,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomTimePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSelected: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = false
    )

    TimePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val localTime = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )

                onSelected(localTime)
                onDismiss()
            }) {
                Text("Ok")
            }
        },
        title = {
//            Text("Select Time")
        },
    ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                clockDialColor = MaterialTheme.colorScheme.primaryContainer,
                clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                clockDialUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectorColor = MaterialTheme.colorScheme.primary,

                periodSelectorBorderColor = MaterialTheme.colorScheme.primary,
                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        )
    }
}

@Composable
fun ErrorDialog(
    title: String = "Error",
    error: String? = null,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { Text(title) },
        text = { Text(
            text = error ?: ""
        ) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
fun ConfirmDialog(
    title: String = "Confirm",
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("NO")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text("YES")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}
