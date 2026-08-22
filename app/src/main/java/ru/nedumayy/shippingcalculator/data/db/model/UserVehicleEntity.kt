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

@Entity(tableName = "user_vehicles")
data class UserVehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val fuelType: String,
    val fuelConsumption: Double,
    val fuelPrice: Double,
    val vehiclePrice: Double,
    val resourceMileage: Double,
    val maintenancePerKm: Double,
    val annualTax: Double,
    val annualInsurance: Double,
    val annualMileage: Double,
    val hasFuel: Boolean,
    val extraExpenses: Double,
    val marginPercent: Double
)
