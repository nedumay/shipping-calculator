/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model.ui

import ru.nedumayy.shippingcalculator.common.parseSafeDouble
import ru.nedumayy.shippingcalculator.domain.model.CostBreakdown
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory
import java.time.LocalDate

data class CalculatorUiState(
    val categories: List<VehicleCategory> = emptyList(),
    val selectedCategory: VehicleCategory? = null,
    val userVehicles: List<UserVehicle> = emptyList(),
    val selectedUserVehicle: UserVehicle? = null,

    val routeFrom: String = "",
    val routeTo: String = "",
    val deliveryDate: String = LocalDate.now().toString(),
    val distance: String = "",
    val fuelType: String = "Бензин",
    val fuelPrice: String = "",
    val extraExpenses: String = "",
    val marginPercent: String = "",

    val fuelConsumption: String = "",
    val vehiclePrice: String = "",
    val resourceMileage: String = "",
    val annualTax: String = "",
    val annualInsurance: String = "",
    val annualMileage: String = "",
    val maintenancePerKm: String = "",

    val costBreakdown: CostBreakdown? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false,
    val isSaving: Boolean = false,
    val showSaveVehicleDialog: Boolean = false,
    val newVehicleName: String = ""
) {
    val canSave: Boolean
        get() = (selectedCategory != null || selectedUserVehicle != null) &&
                routeTo.isNotBlank() &&
                parseSafeDouble(distance) > 0 &&
                costBreakdown != null
}
