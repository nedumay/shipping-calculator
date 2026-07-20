/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

data class CalculationDisplay(
    val id: Long,
    val title: String,
    val date: String,
    val category: String,
    val distance: Double,
    val totalCost: Double,
    val costPerKm: Double
) {
    companion object {
        fun fromDomain(c: DeliveryCalculation): CalculationDisplay {
            val formatted = try {
                val localDate = java.time.LocalDate.parse(c.deliveryDate)
                localDate.format(
                    java.time.format.DateTimeFormatter.ofPattern(
                        "dd MMMM yyyy", java.util.Locale("ru")
                    )
                )
            } catch (e: Exception) {
                c.deliveryDate
            }
            return CalculationDisplay(
                id = c.id,
                title = "${c.routeFrom} → ${c.routeTo}",
                date = formatted,
                category = c.categoryName,
                distance = c.distance,
                totalCost = c.totalCost,
                costPerKm = c.costPerKm
            )
        }
    }
}
