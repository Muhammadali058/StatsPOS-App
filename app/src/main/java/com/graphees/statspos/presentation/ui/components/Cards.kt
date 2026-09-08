package com.graphees.statspos.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings.DEFAULT_RADIUS

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 3.dp
    ),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        border = border,
        onClick = onClick,
        colors = colors,
        content = {
            content()
        }
    )
}

@Composable
fun ListCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(DEFAULT_RADIUS),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 3.dp
    ),
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        border = border,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        content = {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                content()
            }
        }
    )
}

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            content()
        }
    }
}

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    heading: String,
    subHeading: String,
    @DrawableRes icon: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ReportCard(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(DEFAULT_RADIUS))
                        .background(MaterialTheme.colorScheme.primary.copy(.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (icon != null) {
                        AppIcon(
                            modifier = Modifier
                                .padding(10.dp),
                            icon = icon,
                            size = 20.dp,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = heading,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subHeading,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            AppHorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                content()
            }
        }
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            Spacer(Modifier.height(12.dp))
//            AppText(
//                modifier = Modifier
//                    .padding(horizontal = 12.dp),
//                text = heading,
//                style = TextStyle(
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                )
//            )
//            Spacer(Modifier.height(2.dp))
//            AppText(
//                modifier = Modifier
//                    .padding(horizontal = 12.dp),
//                text = subHeading,
//                style = TextStyle(
//                    fontSize = 12.sp,
//                )
//            )
//            Spacer(Modifier.height(8.dp))
//            AppHorizontalDivider()
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//            ) {
//                content()
//            }
//        }
    }
}
