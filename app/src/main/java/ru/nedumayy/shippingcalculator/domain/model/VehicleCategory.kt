/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

import ru.nedumayy.shippingcalculator.common.UiText

data class VehicleCategory(
    val id: String,
    val label: UiText,
    val fuelConsumption: Double,
    val vehiclePrice: Double,
    val resourceMileage: Double,
    val annualTax: Double,
    val annualInsurance: Double,
    val annualMileage: Double,
    val maintenancePerKm: Double,
    val hasFuel: Boolean
) {
    val taxPerKm: Double
        get() = if (annualMileage > 0) annualTax / annualMileage else 0.0

    val insurancePerKm: Double
        get() = if (annualMileage > 0) annualInsurance / annualMileage else 0.0
}
