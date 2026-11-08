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
        require(distance.isFinite() && distance > 0.0) { "distance must be a finite positive value" }
        require(fuelPrice.isFinite() && fuelPrice >= 0.0) { "fuelPrice must be a finite non-negative value" }
        require(extraExpenses.isFinite() && extraExpenses >= 0.0) { "extraExpenses must be a finite non-negative value" }
        require(marginPercent.isFinite() && marginPercent >= 0.0) { "marginPercent must be a finite non-negative value" }
        require(category.annualMileage.isFinite() && category.annualMileage > 0.0) { "annualMileage must be positive" }
        require(category.fuelConsumption.isFinite() && category.fuelConsumption >= 0.0) { "fuelConsumption must be non-negative" }
        require(category.vehiclePrice.isFinite() && category.vehiclePrice >= 0.0) { "vehiclePrice must be non-negative" }
        require(category.residualValue.isFinite() && category.residualValue >= 0.0 && category.residualValue <= category.vehiclePrice) {
            "residualValue must be between zero and vehiclePrice"
        }
        require(category.resourceMileage.isFinite() && category.resourceMileage > 0.0) { "resourceMileage must be positive" }
        require(category.annualTax.isFinite() && category.annualTax >= 0.0) { "annualTax must be non-negative" }
        require(category.annualInsurance.isFinite() && category.annualInsurance >= 0.0) { "annualInsurance must be non-negative" }
        require(category.maintenancePerKm.isFinite() && category.maintenancePerKm >= 0.0) { "maintenancePerKm must be non-negative" }

        val fuelPerKm = if (category.hasFuel) {
            (category.fuelConsumption / 100.0) * fuelPrice
        } else 0.0

        val depreciationPerKm = if (category.resourceMileage > 0) {
            (category.vehiclePrice - category.residualValue) / category.resourceMileage
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
