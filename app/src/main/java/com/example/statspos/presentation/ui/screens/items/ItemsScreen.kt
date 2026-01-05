package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.domain.models.items.Items
import com.example.statspos.presentation.ui.components.AutoCompleteItemsTextbox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.utils.HP
import com.example.statspos.utils.showToast

@Preview(showBackground = true)
@Composable
fun ItemsScreen() {
    val context = LocalContext.current

    var barcode by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    if(showDialog){
        ConfirmDialog(
            text = "Are you sure to delete this Item?",
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SearchBox(
            value = barcode,
            onValueChange = { barcode = it },
            onItemSelected = { itemname ->
                context.showToast(itemname)
            },
            onSearchClick = { text ->

            },
            onKeyboardAction = { text ->

            },
        )

        ItemsList {
            showDialog = true
        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onKeyboardAction: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutoCompleteItemsTextbox(
            modifier = Modifier
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            onItemSelected = onItemSelected,
            onSearchClick = onSearchClick,
            onKeyboardAction = onKeyboardAction,
            label = {
                Text(
                    text = "Search"
                )
            },
        )
        IconButton(
            onClick = {

            },
            modifier = Modifier
        ) {
            CustomIcon(
                icon = R.drawable.ic_barcode,
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}

@Composable
fun ItemsList(
    modifier: Modifier = Modifier,
    onItemClick: (Items) -> Unit,
) {
    val items = (1..50).map {
        Items(
            id = it.toLong(),
            itemname = "Coca cola 1.5 ltr item $it",
            imageUrl = HP.getImageUrl("43512549.png")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        items(items) { item ->
            ItemListCard(item = item){
                onItemClick(it)
            }
        }
    }
}

@Composable
private fun ItemListCard(
    modifier: Modifier = Modifier,
    item: Items,
    onItemClick: (Items) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable {
                    onItemClick(item)
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = item.imageUrl,
                    error = painterResource(R.drawable.item)
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = item.itemname.toString(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Spacer(Modifier.height(2.dp))
                Row {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "Cost: ",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Text(
                            text = "958.58",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "Retail: ",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Text(
                            text = "1458.58",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "W.Sale: ",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Text(
                            text = "1235.78",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "C.Rate: ",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Text(
                            text = "145858",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}