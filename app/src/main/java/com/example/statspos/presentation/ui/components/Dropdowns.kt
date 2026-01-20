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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.statspos.domain.models.DropdownItem

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<DropdownItem>,
    onItemSelected: (DropdownItem) -> Unit,
    listSize: Dp = 200.dp,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
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
    // First time to add None in items list
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (items.any { it.id == 0L }) {
                items
            } else {
                listOf(DropdownItem(0L, "None")) + items
            }
        }
    }

    val filteredItems by remember(itemsWithNone, value) {
        derivedStateOf {
            if (value == "None")
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
                    }
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
                        onItemSelected(DropdownItem(0L,"None"))
                    }
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardOptions = keyboardOptions,
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
                        })
                    ,
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
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
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
    // First time to add None in items list
    val itemsWithNone by remember(items) {
        derivedStateOf {
            if (items.any { it.id == 0L }) {
                items
            } else {
                listOf(DropdownItem(0L, "None")) + items
            }
        }
    }

    val filteredItems by remember(itemsWithNone, value, mainId) {
        derivedStateOf {
            if (mainId == 0L) {
                if (items.any { it.id == 0L }) {
                    items
                } else {
                    listOf(DropdownItem(0L, "None")) + items
                }
            } else {
                if (value == "None")
                    listOf(DropdownItem(0L, "None")) + itemsWithNone.filter { it.mainId == mainId}
                else
                    listOf(DropdownItem(0L, "None")) + itemsWithNone.filter {
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
        onItemSelected(DropdownItem(0L, "None"))
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
                    }
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
                        onItemSelected(DropdownItem(0L, "None"))
                    }
                ) {
                    AppIcon(
                        icon = Icons.Default.Clear,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            keyboardOptions = keyboardOptions,
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
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Textbox(
            value = selectedItem?.name ?: "",
            onValueChange = {},
            modifier = modifier
                .menuAnchor(),
            shape = shape,
            readOnly = true,
            label = label,
            placeholder = placeholder,
            leadingIcon = {
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
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = false
                        onItemSelected(items[0])
                    }
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
fun CustomComboBox(
    modifier: Modifier = Modifier,
    items: List<DropdownItem>,
    selectedItem: DropdownItem?,
    listSize: Dp = 200.dp,
    label: String,
    onItemSelected: (DropdownItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {

        Box(
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(4.dp)
                )
                .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppIcon(
                    icon = Icons.Default.ArrowDropDown,
                    modifier = Modifier
                        .clickable {  }
                )
                Spacer(Modifier.width(16.dp))

                Text(
                    text = selectedItem?.name ?: label,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ){
                            expanded = true
                        },
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = if(selectedItem == null) 16.sp else 14.sp,
                    ),
                )
                AppIcon(
                    icon = Icons.Default.Clear,
                    size = 20.dp,
                    modifier = Modifier
                        .clickable {
                            expanded = false
                            onItemSelected(items[0])
                        }
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .heightIn(max = listSize)
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
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
