package ru.nedumayy.shippingcalculator.domain.model.ui

import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle

data class MaintenanceUiState(
    val vehicles: List<UserVehicle> = emptyList(),
    val selectedVehicle: UserVehicle? = null,
    val records: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val totalRealCost: Double = 0.0,
    val totalEstimatedCost: Double = 0.0,
    val costDifferencePerKm: Double = 0.0
)
