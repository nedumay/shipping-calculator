/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase

import ru.nedumayy.shippingcalculator.domain.model.CostBreakdown
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory
import javax.inject.Inject

class CalculateDeliveryCostUseCase @Inject constructor() {

    operator fun invoke(
        category: VehicleCategory,
        distance: Double,
        fuelPrice: Double,
        extraExpenses: Double,
        marginPercent: Double
    ): CostBreakdown {
        val fuelPerKm = if (category.hasFuel) {
            (category.fuelConsumption / 100.0) * fuelPrice
        } else 0.0

        val depreciationPerKm = if (category.resourceMileage > 0) {
            category.vehiclePrice / category.resourceMileage
        } else 0.0

        val taxPerKm = category.taxPerKm
        val insurancePerKm = category.insurancePerKm

        val costPerKm = fuelPerKm + depreciationPerKm + taxPerKm +
                category.maintenancePerKm + insurancePerKm

        val fuelTotal = fuelPerKm * distance
        val depreciationTotal = depreciationPerKm * distance
        val taxTotal = taxPerKm * distance
        val maintenanceTotal = category.maintenancePerKm * distance
        val insuranceTotal = insurancePerKm * distance

        val baseTotal = costPerKm * distance + extraExpenses
        val marginAmount = baseTotal * (marginPercent / 100.0)
        val finalTotal = baseTotal + marginAmount
        val finalPerKm = if (distance > 0) finalTotal / distance else 0.0

        return CostBreakdown(
            fuelTotal = fuelTotal,
            depreciationTotal = depreciationTotal,
            taxTotal = taxTotal,
            maintenanceTotal = maintenanceTotal,
            insuranceTotal = insuranceTotal,
            extraTotal = extraExpenses,
            marginAmount = marginAmount,
            finalTotal = finalTotal,
            finalPerKm = finalPerKm
        )
    }
}