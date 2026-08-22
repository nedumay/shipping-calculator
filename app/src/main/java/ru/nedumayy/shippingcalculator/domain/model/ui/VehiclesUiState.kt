/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model.ui

import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle

data class VehiclesUiState(
    val vehicles: List<UserVehicle> = emptyList(),
    val selectedVehicle: UserVehicle? = null,
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val totalRealCost: Double = 0.0,
    val totalEstimatedCost: Double = 0.0,
    val costDifferencePerKm: Double = 0.0
)
