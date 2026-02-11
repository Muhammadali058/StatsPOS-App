package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.utils.ConstantPaddings

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<DropdownItem>,
    onItemSelected: (DropdownItem) -> Unit,
    listSize: Dp = 200.dp,
    noneText: String = "None",
    addType: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentPadding: PaddingValues = PaddingValues(
        start = 15.dp,
        top = 10.dp,
        end = 10.dp,
        bottom = 10.dp
    ),
    padding: PaddingValues = ConstantPaddings.CUSTOM_TEXTBOX_OUTSIDE,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // First time to add None in items list
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (items.any { it.id == 0L }) {
                items
            } else {
                listOf(DropdownItem(0L, noneText)) + items
            }
        }
    }

    val filteredItems by remember(itemsWithNone, value) {
        derivedStateOf {
            if (value == noneText)
                itemsWithNone
            else
                itemsWithNone.filter {
                    it.name.contains(value, ignoreCase = true)
                }
        }
    }

    var expanded by remember { mutableStateOf(false) }
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
                    textFieldSize = coordinates.size
                },
            shape = shape,
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
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onValueChange("")
                        onItemSelected(DropdownItem(0L, noneText))
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardActions = keyboardActions,
            contentPadding = contentPadding,
            padding = padding,
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
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
                        modifier = Modifier.heightIn(max = listSize)
                    ) {
                        items(filteredItems) { item ->
                            Text(
                                text = if (addType) item.name + " -> " + item.type else item.name,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(item.name)
                                        expanded = false
                                        onItemSelected(item)
                                        keyboardController?.hide()
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
fun SubDropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<DropdownItem>,
    onItemSelected: (DropdownItem) -> Unit,
    mainId: Long = 0L,
    listSize: Dp = 200.dp,
    noneText: String = "None",
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentPadding: PaddingValues = PaddingValues(
        start = 15.dp,
        top = 10.dp,
        end = 10.dp,
        bottom = 10.dp
    ),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // First time to add None in items list
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (items.any { it.id == 0L }) {
                items
            } else {
                listOf(DropdownItem(0L, noneText)) + items
            }
        }
    }

    val filteredItems by remember(itemsWithNone, value, mainId) {
        derivedStateOf {
            if (mainId == 0L) {
                listOf(DropdownItem(0L, noneText))
//                if (items.any { it.id == 0L }) {
//                    items
//                } else {
//                    listOf(DropdownItem(0L, noneText)) + items
//                }
            } else {
                if (value == noneText)
                    listOf(
                        DropdownItem(
                            0L,
                            noneText
                        )
                    ) + itemsWithNone.filter { it.mainId == mainId }
                else
                    listOf(DropdownItem(0L, noneText)) + itemsWithNone.filter {
                        it.mainId == mainId && it.name.contains(value, ignoreCase = true)
                    }
            }
        }
    }

    var isFirstRun by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(mainId) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }

        onValueChange("")
        onItemSelected(DropdownItem(0L, noneText))
    }

    var expanded by remember { mutableStateOf(false) }
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
                    textFieldSize = coordinates.size
                },
            shape = shape,
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
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onValueChange("")
                        onItemSelected(DropdownItem(0L, noneText))
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardActions = keyboardActions,
            contentPadding = contentPadding
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
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
                        modifier = Modifier.heightIn(max = listSize)
                    ) {
                        items(filteredItems) { item ->
                            Text(
                                text = item.name,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(item.name)
                                        expanded = false
                                        onItemSelected(item)
                                        keyboardController?.hide()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    modifier: Modifier = Modifier,
    items: List<DropdownItem>,
    selectedItem: DropdownItem?,
    onItemSelected: (DropdownItem) -> Unit,
    listSize: Dp = 200.dp,
    addNone: Boolean = false,
    noneText: String = "None",
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (addNone) {
                if (items.any { it.id == 0L }) {
                    items
                } else {
                    listOf(DropdownItem(0L, noneText)) + items
                }
            } else
                items
        }
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled)
                expanded = !expanded
        }
    ) {
        Textbox(
            value = selectedItem?.name ?: "",
            onValueChange = {},
            modifier = modifier
                .menuAnchor(),
            shape = shape,
            readOnly = true,
            enabled = enabled,
            label = label,
            placeholder = placeholder,
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (enabled)
                            expanded = true
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (enabled) {
                            expanded = false
                            onItemSelected(itemsWithNone[0])
                        }
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .heightIn(max = listSize)
        ) {
            itemsWithNone.forEach { item ->
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
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubComboBox(
    modifier: Modifier = Modifier,
    items: List<DropdownItem>,
    selectedItem: DropdownItem?,
    onItemSelected: (DropdownItem) -> Unit,
    mainId: Long = 0L,
    listSize: Dp = 200.dp,
    addNone: Boolean = false,
    noneText: String = "None",
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (addNone) {
                if (items.any { it.id == 0L }) {
                    items
                } else {
                    listOf(DropdownItem(0L, noneText)) + items
                }
            } else
                items
        }
    }

    val filteredItems by remember(itemsWithNone, mainId) {
        derivedStateOf {
            if (mainId == 0L) {
                listOf(DropdownItem(0L, noneText))
//                if (items.any { it.id == 0L }) {
//                    items
//                } else {
//                    listOf(DropdownItem(0L, noneText)) + items
//                }
            } else {
                listOf(DropdownItem(0L, noneText)) + items.filter { it.mainId == mainId }
            }
        }
    }

    var isFirstRun by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(mainId) {
        if (isFirstRun) {
            isFirstRun = false
            return@LaunchedEffect
        }

        onItemSelected(DropdownItem(0L, noneText))
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled)
                expanded = !expanded
        }
    ) {
        Textbox(
            value = selectedItem?.name ?: "",
            onValueChange = {},
            modifier = modifier
                .menuAnchor(),
            shape = shape,
            readOnly = true,
            enabled = enabled,
            label = label,
            placeholder = placeholder,
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (enabled)
                            expanded = true
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.ArrowDropDown
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (enabled) {
                            expanded = false
                            onItemSelected(itemsWithNone[0])
                        }
                    },
                    enabled = enabled,
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .heightIn(max = listSize)
        ) {
            filteredItems.forEach { item ->
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
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
