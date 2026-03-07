package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
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
            shape = RoundedCornerShape(16.dp),
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
    heading:String,
    subHeading:String,
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
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(12.dp))
                AppText(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    text = heading,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(Modifier.height(2.dp))
                AppText(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    text = subHeading,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
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
        }
    }
}
