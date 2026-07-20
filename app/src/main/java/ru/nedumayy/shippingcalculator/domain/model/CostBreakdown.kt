/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

data class CostBreakdown(
    val fuelTotal: Double,
    val depreciationTotal: Double,
    val taxTotal: Double,
    val maintenanceTotal: Double,
    val insuranceTotal: Double,
    val extraTotal: Double,
    val marginAmount: Double,
    val finalTotal: Double,
    val finalPerKm: Double
)
