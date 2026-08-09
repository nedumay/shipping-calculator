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
    val fuelConsumption: Double,
    val vehiclePrice: Double,
    val resourceMileage: Double,
    val annualTax: Double,
    val annualMileage: Double,
    val maintenancePerKm: Double,
    val annualInsurance: Double,
    val hasFuel: Boolean = true
)
