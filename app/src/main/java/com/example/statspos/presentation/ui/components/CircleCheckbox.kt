package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleCheckbox(checked: Boolean, enabled: Boolean = true, onCheckedChange: () -> Unit) {
    val imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Filled.Circle
    val tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
    val background = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = {
                onCheckedChange()
            },
            modifier = Modifier
                .size(24.dp)
            ,
            enabled = enabled
        ) {
            Icon(
                imageVector = imageVector,
                tint = tint,
                modifier = Modifier
                    .background(background, shape = CircleShape)
                    .size(22.dp),
                contentDescription = "checkbox"
            )
        }
        Text(
            text = "Remember me",
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }
}
