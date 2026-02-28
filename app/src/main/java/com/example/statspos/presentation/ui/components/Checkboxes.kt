package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.statspos.domain.models.RadioItem

@Composable
fun AppCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    label: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(20.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier
                .scale(.95f)
                .size(20.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier
                .padding(start = 6.dp)
        )
    }
}

@Composable
fun CircleCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    label: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Filled.Circle
    val tint =
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
    val background =
        if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onCheckedChange(!checked)
            },
            modifier = Modifier
                .size(24.dp),
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
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }
}


@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    label: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(20.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier
                .scale(.7f)
                .size(40.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = MaterialTheme.colorScheme.background,
                uncheckedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier
                .padding(start = 4.dp),
            overflow = TextOverflow.Visible
        )
    }
}


@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    items: List<RadioItem>,
    selectedItem: RadioItem?,
    onItemSelected: (RadioItem) -> Unit,
    horizontal: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outline,
) {
    val container: @Composable (@Composable () -> Unit) -> Unit =
        if (horizontal) {
            { content ->
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) { content() }
            }
        } else {
            { content ->
                Column(
                    modifier = modifier
                ) { content() }
            }
        }

    container {
        items.forEach { item ->
            val isSelected = item == selectedItem

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 0.dp, horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onItemSelected(item) }
                    .padding(4.dp)
            ) {
                RadioButton(
                    modifier = Modifier
                        .scale(.9f),
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = selectedColor,
                        unselectedColor = unselectedColor
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}
