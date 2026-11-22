/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import ru.nedumayy.shippingcalculator.common.UiText
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory

class CalculateDeliveryCostUseCaseTest {

    private val useCase = CalculateDeliveryCostUseCase()

    @Test
    fun calculateCost_validInput_returnsCorrectBreakdown() {
        val category = VehicleCategory(
            id = "car",
            label = UiText.DynamicString("Car"),
            fuelConsumption = 10.0,
            vehiclePrice = 1_000_000.0,
            residualValue = 0.0,
            resourceMileage = 200_000.0,
            annualTax = 10_000.0,
            annualInsurance = 20_000.0,
            annualMileage = 30_000.0,
            maintenancePerKm = 2.0,
            hasFuel = true
        )

        val breakdown = useCase(
            category = category,
            distance = 100.0,
            fuelPrice = 50.0,
            extraExpenses = 100.0,
            marginPercent = 10.0
        )

        assertNotNull(breakdown)
        assertEquals(500.0, breakdown.fuelTotal, 0.01)
        assertEquals(500.0, breakdown.depreciationTotal, 0.01)
        assertEquals(200.0, breakdown.maintenanceTotal, 0.01)
        assertEquals(100.0, breakdown.extraTotal, 0.01)
    }

    @Test
    fun calculateCost_unfilledOptionalFields_returnsZeroCostForUnfilled() {
        val category = VehicleCategory(
            id = "car",
            label = UiText.DynamicString("Car"),
            fuelConsumption = 10.0,
            vehiclePrice = 0.0,
            residualValue = 0.0,
            resourceMileage = 0.0,
            annualTax = 0.0,
            annualInsurance = 0.0,
            annualMileage = 0.0,
            maintenancePerKm = 0.0,
            hasFuel = true
        )

        val breakdown = useCase(
            category = category,
            distance = 100.0,
            fuelPrice = 60.0,
            extraExpenses = 0.0,
            marginPercent = 0.0
        )

        assertNotNull(breakdown)
        assertEquals(600.0, breakdown.fuelTotal, 0.01) // (10/100) * 60 * 100
        assertEquals(0.0, breakdown.depreciationTotal, 0.01)
        assertEquals(0.0, breakdown.taxTotal, 0.01)
        assertEquals(0.0, breakdown.insuranceTotal, 0.01)
        assertEquals(0.0, breakdown.maintenanceTotal, 0.01)
        assertEquals(600.0, breakdown.finalTotal, 0.01)
    }
}
