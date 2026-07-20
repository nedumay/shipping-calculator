/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

import java.time.LocalDate

data class CalculatorUiState(
    val categories: List<VehicleCategory> = emptyList(),
    val selectedCategory: VehicleCategory? = null,
    val routeFrom: String = "",
    val routeTo: String = "",
    val deliveryDate: String = LocalDate.now().toString(),
    val distance: String = "15",
    val fuelPrice: String = "58",
    val extraExpenses: String = "150",
    val marginPercent: String = "20",
    val annualMileage: String = "30000",
    val costBreakdown: CostBreakdown? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false,
    val isSaving: Boolean = false
) {
    val canSave: Boolean
        get() = selectedCategory != null &&
                routeTo.isNotBlank() &&
                (distance.toDoubleOrNull() ?: 0.0) > 0 &&
                costBreakdown != null
}
