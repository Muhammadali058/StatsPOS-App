package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.reports.ChartReport
import com.example.statspos.presentation.ui.screens.reports.sales.rememberMarker
import com.example.statspos.utils.HP
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent

@Composable
fun TrendChart(
    modifier: Modifier = Modifier,
    chartFor:String,
    chartReport: List<ChartReport>,
    chartDuration: DropdownItem,
    onChartDurationChange:(DropdownItem) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(top = 8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        AppText(
                            modifier = Modifier
                                .padding(horizontal = 12.dp),
                            text = "$chartFor Chart",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Spacer(Modifier.height(2.dp))
                        val duration = when (chartDuration.id) {
                            1L -> {
                                "days"
                            }

                            2L -> {
                                "weeks"
                            }

                            3L -> {
                                "months"
                            }

                            else -> {
                                "years"
                            }
                        }
                        AppText(
                            modifier = Modifier
                                .padding(horizontal = 12.dp),
                            text = "$chartFor trend chart for last 7 $duration",
                            style = TextStyle(
                                fontSize = 12.sp,
                            )
                        )
                    }
                    ComboBox(
                        modifier = Modifier
                            .width(120.dp),
                        items = HP.chartDurations,
                        selectedItem = chartDuration,
                        onItemSelected = { item ->
                            onChartDurationChange(item)
                        },
                        showEndIcon = false,
                        showBorder = false,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                        )
                    )
                }
                Spacer(Modifier.height(2.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    DailySalesChart(chartReport)
                }
            }
        }
    }
}

@Composable
private fun DailySalesChart(data: List<ChartReport>) {
    if (data.isNotEmpty()) {
        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(data) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        data.map { it.total }
                    )
                }
            }
        }
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = Fill(MaterialTheme.colorScheme.primary),
                            thickness = 16.dp,
                            shape = RectangleShape
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberAxisLabelComponent(
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = remember(data) {
                        CartesianValueFormatter { _, value, _ ->
                            val index = value.toInt().coerceIn(data.indices)
                            data[index].date
                        }
                    },
                    label = rememberAxisLabelComponent(
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    )
                ),
                marker = rememberMarker(
                    valueFormatter = DefaultCartesianMarker.ValueFormatter.default(
                        prefix = "Rs."
                    ),
                    showIndicator = true
                ),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
