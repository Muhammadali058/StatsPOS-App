package com.example.statspos.presentation.ui.screens.reports

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.statspos.R
import com.example.statspos.domain.models.RadioItem
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.RadioGroup
import com.example.statspos.presentation.ui.components.TopItem
import com.example.statspos.presentation.ui.screens.TopRoutes
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.showToast
import kotlinx.coroutines.launch

private val reports = listOf(
    TopItem("Sales", TopRoutes.SalesReports),
    TopItem("Purchase", TopRoutes.PurchaseReports),
    TopItem("Profit", TopRoutes.ProfitReports),
    TopItem("Stock", TopRoutes.StockReports),
    TopItem("Accounts", TopRoutes.AccountsReports),
    TopItem("Items", TopRoutes.ItemsReports),
    TopItem("Audit", TopRoutes.AuditReports),
)

@Composable
fun ReportsScreen(
    onTopRouteClick: (NavKey) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
            Title("Reports", R.drawable.reports)
        }
        item {
            ReportsGrid(reports, onTopRouteClick)
        }
    }
}

@Composable
private fun Title(
    title: String,
    @DrawableRes icon: Int? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
//        if (icon != null) {
//            AppIcon(
//                icon = icon,
//                size = 24.dp,
//            )
//            Spacer(Modifier.width(16.dp))
//        }
        AppText(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ReportsGrid(
    items: List<TopItem>,
    onClick: (TopRoutes) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(112.dp)
                    .padding(vertical = 6.dp),
                onClick = { onClick(item.screen) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item.icon?.run {
                        AppIcon(
                            icon = item.icon,
                            size = 30.dp,
                        )
                    }
//                    Spacer(Modifier.height(4.dp))
                    AppText(
                        text = item.text,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
    }
}
