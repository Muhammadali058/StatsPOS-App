package com.graphees.statspos.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.reports.ChartReport
import com.graphees.statspos.utils.HP
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LayeredComponent
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

@Composable
fun TrendChart(
    modifier: Modifier = Modifier,
    chartFor: String,
    chartReport: List<ChartReport>,
    chartDuration: DropdownItem,
    onChartDurationChange: (DropdownItem) -> Unit,
) {
    ReportCard(modifier) {
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
                        text = "$chartFor chart for last 7 $duration",
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
            AppHorizontalDivider()
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

@Composable
internal fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackground =
        rememberShapeComponent(
            fill = Fill(MaterialTheme.colorScheme.background),
            shape = CircleShape,
            strokeFill = Fill(MaterialTheme.colorScheme.outline),
            strokeThickness = 1.dp,
        )
    val label =
        rememberTextComponent(
            style =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                ),
            padding = Insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(Fill(MaterialTheme.colorScheme.surface), CircleShape)
    val guideline = rememberAxisGuidelineComponent()

    return rememberDefaultCartesianMarker(
        label = label,
        valueFormatter = valueFormatter,
        indicator =
            if (showIndicator) {
                { color ->
                    LayeredComponent(
                        back = ShapeComponent(Fill(color.copy(alpha = 0.15f)), CircleShape),
                        front =
                            LayeredComponent(
                                back = ShapeComponent(fill = Fill(color), shape = CircleShape),
                                front = indicatorFrontComponent,
                                padding = Insets(5.dp),
                            ),
                        padding = Insets(10.dp),
                    )
                }
            } else {
                null
            },
        indicatorSize = 36.dp,
        guideline = guideline,
    )
}
