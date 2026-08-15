/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.nedumayy.shippingcalculator.domain.model.AnalyticsData
import ru.nedumayy.shippingcalculator.domain.model.CategoryStat
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import javax.inject.Inject

class GetAnalyticsUseCase @Inject constructor(
    private val repository: CalculationRepository
) {
    operator fun invoke(): Flow<AnalyticsData> {
        return repository.getAllCalculations().map { calculations ->
            if (calculations.isEmpty()) return@map AnalyticsData()

            val totalRevenue = calculations.sumOf { it.totalCost }
            val totalExpenses = calculations.sumOf {
                it.fuelCost + it.depreciationCost + it.taxCost + 
                it.maintenanceCost + it.insuranceCost + it.extraExpenses
            }
            val netProfit = totalRevenue - totalExpenses
            val totalDistance = calculations.sumOf { it.distance }
            val averageCostPerKm = if (totalDistance > 0) totalExpenses / totalDistance else 0.0

            val categoryStats = calculations.groupBy { it.categoryName }
                .map { (name, list) ->
                    val profit = list.sumOf { calc ->
                        calc.totalCost - (calc.fuelCost + calc.depreciationCost + calc.taxCost + 
                                       calc.maintenanceCost + calc.insuranceCost + calc.extraExpenses)
                    }
                    CategoryStat(name, profit, list.size)
                }
                .sortedByDescending { it.profit }

            val mostProfitable = categoryStats.firstOrNull()?.categoryName ?: "-"

            AnalyticsData(
                totalRevenue = totalRevenue,
                totalExpenses = totalExpenses,
                netProfit = netProfit,
                averageCostPerKm = averageCostPerKm,
                mostProfitableCategory = mostProfitable,
                categoryStats = categoryStats
            )
        }
    }
}
