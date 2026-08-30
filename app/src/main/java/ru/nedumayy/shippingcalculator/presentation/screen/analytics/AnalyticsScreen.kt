/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.common.formatMoney
import ru.nedumayy.shippingcalculator.presentation.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.analytics), fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.categoryStats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_data),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = Screen.BottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnalyticsCard(
                        label = stringResource(R.string.total_revenue),
                        value = formatMoney(state.totalRevenue),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    AnalyticsCard(
                        label = stringResource(R.string.total_expenses),
                        value = formatMoney(state.totalExpenses),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                item {
                    AnalyticsCard(
                        label = stringResource(R.string.net_profit),
                        value = formatMoney(state.netProfit),
                        color = MaterialTheme.colorScheme.secondary,
                        isHighlight = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnalyticsCard(
                            label = stringResource(R.string.avg_cost_km),
                            value = "${formatMoney(state.averageCostPerKm)}/км",
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsCard(
                            label = stringResource(R.string.most_profitable),
                            value = state.mostProfitableCategory,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.by_category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(state.categoryStats) { stat ->
                    CategoryStatRow(stat.categoryName, stat.profit, stat.count)
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    isHighlight: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (isHighlight) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                 else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun CategoryStatRow(name: String, profit: Double, count: Int) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontWeight = FontWeight.Medium)
                Text(
                    "$count ${stringResource(R.string.orders_count)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                formatMoney(profit),
                fontWeight = FontWeight.Bold,
                color = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
