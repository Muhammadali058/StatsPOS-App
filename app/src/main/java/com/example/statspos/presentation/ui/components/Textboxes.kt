@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.statspos.R
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.ConstantSize
import com.example.statspos.utils.HP
import java.time.LocalDate

@Composable
fun Textbox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    height: Dp = ConstantSize.DEFAULT_TEXTBOX_HEIGHT,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = Color.Transparent,
    ),
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentPadding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_INSIDE,
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusRequester.requestFocus()
            }
            .padding(padding)
            .height(height),
        singleLine = singleLine,
        readOnly = readOnly,
        enabled = enabled,
        interactionSource = interactionSource,
        textStyle = textStyle.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 15.sp,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                interactionSource = interactionSource,
                visualTransformation = visualTransformation,
                label = label,
                placeholder = placeholder,
                contentPadding = contentPadding,
                colors = colors,
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = shape,
                    )
                }
            )
        }
    )
}


@Composable
fun PasswordTextbox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    height: Dp = ConstantSize.DEFAULT_TEXTBOX_HEIGHT,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentPadding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_INSIDE,
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val icon = if (visible)
        R.drawable.hidden
    else
        R.drawable.eye

    Textbox(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        height = height,
        placeholder = placeholder,
        shape = shape,
        textStyle = textStyle,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                AppIcon(
                    icon = icon,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        contentPadding = contentPadding,
        padding = padding,
    )
}

@Composable
fun AutoCompleteItemsTextbox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    onEndIconClick: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    suggestions: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit) = {
        IconButton(onClick = {
            onEndIconClick(value)
        }) {
            AppIcon(
                icon = Icons.Default.ArrowForward,
                size = 20.dp
            )
        }
    },
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Search
    ),
    height: Dp = ConstantSize.DEFAULT_TEXTBOX_HEIGHT,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    contentPadding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_INSIDE,
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    var items by rememberSaveable { mutableStateOf(HP.autoCompleteItems) }
    var expanded by remember { mutableStateOf(false) }

    val filteredItems = remember(value) {
        items.filter {
            it.startsWith(value, ignoreCase = true)
        }
    }

    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
    ) {
        Textbox(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },
            value = value,
            onValueChange = {
                onValueChange(it)
                if (suggestions)
                    expanded = it.isNotEmpty()
            },
            height = height,
            label = label,
            placeholder = placeholder,
            shape = shape,
            textStyle = textStyle,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            leadingIcon = if (suggestions) {
                {
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }
                    ) {
                        AppIcon(
                            icon = Icons.Default.ArrowDropDown
                        )
                    }

                }
            } else null,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClick(value)
                },
                onGo = {
                    onSearchClick(value)
                }
            ),
            contentPadding = contentPadding,
            padding = padding,
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Popup(
                offset = IntOffset(
                    x = 0,
                    y = textFieldSize.height - 6
                ),
                onDismissRequest = { expanded = false }
            ) {
                Card(
                    modifier = Modifier
                        .width(with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        }),
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

@Composable
fun DiscountTextbox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isDiscRsPer: Boolean,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    Box(
        modifier = modifier,
    ) {
        val items = remember {
            listOf(
                DropdownItem(0L, "Rs."),
                DropdownItem(1L, "%"),
            )
        }
        var selectedItem by remember { mutableStateOf(if (isDiscRsPer) items[0] else items[1]) }
        var expanded by remember { mutableStateOf(false) }

        LaunchedEffect(isDiscRsPer) {
            selectedItem = if (isDiscRsPer)
                items[0]
            else
                items[1]
        }

        Textbox(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Discount")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
            ),
            padding = padding,
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(85.dp)
                .align(Alignment.CenterEnd)
        ) {
            Textbox(
                value = selectedItem.name,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .border(0.dp, Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        AppIcon(
                            icon = Icons.Default.ArrowDropDown
                        )
                    }
                },
                padding = padding,
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .heightIn(max = 100.dp)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        modifier = Modifier.height(34.dp),
                        text = {
                            Text(
                                text = item.name,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 14.sp,
                                ),
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                            )
                        },
                        onClick = {
                            selectedItem = item
                            onIsDiscRsPerChange(item.id == 0L)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTextbox(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "Search",
    onValueChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onEndIconClick: (String) -> Unit,
) {
    Textbox(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                onEndIconClick(value)
            }) {
                AppIcon(
                    icon = Icons.Default.Clear,
                    modifier = Modifier.size(20.dp)
                )
//                AppIcon(icon = R.drawable.ic_search)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClick(value)
            }
        )
    )
}

@Composable
fun DateTextbox(
    modifier: Modifier = Modifier,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    label: String = "Date",
) {
    var showDateDialog by remember { mutableStateOf(false) }
    if (showDateDialog) {
        CustomDatePickerDialog(
            initialValue = date,
            onDismiss = {
                showDateDialog = false
            },
            onSelected = {
                onDateChange(it)
            }
        )
    }

    Textbox(
        value = HP.getFormatedDate(date),
        onValueChange = { },
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            AppIconButton(
                icon = Icons.Default.CalendarMonth,
                onClick = {
                    showDateDialog = true
                }
            )
        },
        label = {
            Text(label)
        },
    )
}

@Composable
fun TextboxCB(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit = {},
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    height: Dp = ConstantSize.DEFAULT_TEXTBOX_HEIGHT,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    contentPadding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_INSIDE,
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
    ) {
        Textbox(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },
            value = value,
            onValueChange = onValueChange,
            height = height,
            label = label,
            placeholder = placeholder,
            shape = shape,
            textStyle = textStyle,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if(enabled)
                            expanded = !expanded
                    }
                ) {
                    AppIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            contentPadding = contentPadding,
            padding = padding,
        )

        if (expanded && items.isNotEmpty()) {
            Popup(
                offset = IntOffset(
                    x = 0,
                    y = textFieldSize.height - 6
                ),
                onDismissRequest = { expanded = false }
            ) {
                Card(
                    modifier = Modifier
                        .width(with(LocalDensity.current) {
                            textFieldSize.width.toDp()
                        }),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(items) { item ->
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
