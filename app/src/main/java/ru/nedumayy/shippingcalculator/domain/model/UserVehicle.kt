/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

data class UserVehicle(
    val id: Long = 0,
    val name: String,
    val fuelType: String = "Бензин",
    val fuelConsumption: Double,
    val fuelPrice: Double = 0.0,
    val vehiclePrice: Double,
    val resourceMileage: Double,
    val maintenancePerKm: Double,
    val annualTax: Double,
    val annualInsurance: Double,
    val annualMileage: Double,
    val hasFuel: Boolean = true,
    val extraExpenses: Double = 0.0,
    val marginPercent: Double = 0.0
) {
    val taxPerKm: Double
        get() = if (annualMileage > 0) annualTax / annualMileage else 0.0

    val insurancePerKm: Double
        get() = if (annualMileage > 0) annualInsurance / annualMileage else 0.0

    val totalCostPerKm: Double
        get() {
            val fuelCost = if (hasFuel) (fuelConsumption / 100.0) * fuelPrice else 0.0
            val depreciation = if (resourceMileage > 0) vehiclePrice / resourceMileage else 0.0
            return fuelCost + depreciation + maintenancePerKm + insurancePerKm + taxPerKm
        }
}
