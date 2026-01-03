package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun AutoCompleteTextbox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = Color.Red,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentPadding: PaddingValues = PaddingValues(
        start = 15.dp,
        top = 10.dp,
        end = 10.dp,
        bottom = 10.dp
    ),
) {
    var expanded by remember { mutableStateOf(false) }

    val filteredItems = remember(value) {
        items.filter {
            it.startsWith(value, ignoreCase = true)
        }
    }

//    var textFieldPosition by remember { mutableStateOf(Offset.Zero) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
    ) {
        Textbox(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = it.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
//                    textFieldPosition = coordinates.localToWindow(Offset.Zero)
                    textFieldSize = coordinates.size
                },
            shape = shape,
            colors = colors,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            label = label,
            textStyle = textStyle,
            placeholder = placeholder,
            leadingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    CustomIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            contentPadding = contentPadding
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Popup(
//                alignment = Alignment.TopStart,
                offset = IntOffset(
                    x = 0,
//                    x = textFieldPosition.x.toInt(),
                    y = textFieldSize.height
//                    y = (textFieldPosition.y + textFieldSize.height).toInt()
                ),
                onDismissRequest = { expanded = false }
            ) {
                Card(
                    modifier = Modifier
                        .width(with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        })
                    ,
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(filteredItems) { item ->
                            Text(
                                text = item,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(item)
                                        expanded = false
                                        onItemSelected(item)
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
