/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceCategory
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.model.ui.VehiclesUiState
import ru.nedumayy.shippingcalculator.domain.usecase.maintenance.*
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val addMaintenanceRecordUseCase: AddMaintenanceRecordUseCase,
    private val deleteMaintenanceRecordUseCase: DeleteMaintenanceRecordUseCase,
    private val updateUserVehicleUseCase: UpdateUserVehicleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehiclesUiState())
    val uiState: StateFlow<VehiclesUiState> = _uiState.asStateFlow()

    private val _selectedVehicleId = MutableStateFlow<Long?>(null)

    init {
        loadVehicles()
        observeMaintenanceRecords()
    }

    private fun loadVehicles() {
        getVehiclesUseCase()
            .onEach { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles) }
                if (vehicles.isNotEmpty() && _uiState.value.selectedVehicle == null) {
                    selectVehicle(vehicles.first())
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeMaintenanceRecords() {
        _selectedVehicleId
            .filterNotNull()
            .flatMapLatest { vehicleId ->
                getMaintenanceRecordsUseCase(vehicleId)
            }
            .onEach { records ->
                val vehicle = _uiState.value.selectedVehicle ?: return@onEach
                calculateStats(vehicle, records)
            }
            .launchIn(viewModelScope)
    }

    fun selectVehicle(vehicle: UserVehicle) {
        _uiState.update { it.copy(selectedVehicle = vehicle, isLoading = true) }
        _selectedVehicleId.value = vehicle.id
    }

    private fun calculateStats(vehicle: UserVehicle, records: List<MaintenanceRecord>) {
        val totalReal = records.sumOf { it.cost }
        
        val mileageRange = if (records.size > 1) {
            val maxMileage = records.maxOf { it.mileage }
            val minMileage = records.minOf { it.mileage }
            maxMileage - minMileage
        } else 0.0

        val estimated = mileageRange * vehicle.maintenancePerKm
        
        val realPerKm = if (mileageRange > 0) totalReal / mileageRange else 0.0
        val diffPerKm = realPerKm - vehicle.maintenancePerKm

        _uiState.update { 
            it.copy(
                records = records,
                totalRealCost = totalReal,
                totalEstimatedCost = estimated,
                costDifferencePerKm = diffPerKm,
                isLoading = false
            )
        }
    }

    fun updateProfile(updatedVehicle: UserVehicle) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            updateUserVehicleUseCase(updatedVehicle)
            _uiState.update { it.copy(selectedVehicle = updatedVehicle, isSaving = false) }
        }
    }

    fun addRecord(
        mileage: Double,
        description: String,
        cost: Double,
        category: MaintenanceCategory
    ) {
        val vehicleId = _uiState.value.selectedVehicle?.id ?: return
        viewModelScope.launch {
            val record = MaintenanceRecord(
                vehicleId = vehicleId,
                date = Date(),
                mileage = mileage,
                description = description,
                cost = cost,
                category = category
            )
            addMaintenanceRecordUseCase(record)
        }
    }

    fun deleteRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            deleteMaintenanceRecordUseCase(record)
        }
    }
}
