/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class DeliveryCalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String,
    val routeFrom: String,
    val routeTo: String,
    val fuelType: String,
    val deliveryDate: String,
    val distance: Double,
    val fuelCost: Double,
    val depreciationCost: Double,
    val taxCost: Double,
    val maintenanceCost: Double,
    val insuranceCost: Double,
    val extraExpenses: Double,
    val marginPercent: Double,
    val totalCost: Double,
    val costPerKm: Double,
    val createdAt: Long
)

