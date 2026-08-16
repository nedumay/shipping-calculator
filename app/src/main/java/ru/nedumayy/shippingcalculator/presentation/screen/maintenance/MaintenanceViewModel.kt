package ru.nedumayy.shippingcalculator.presentation.screen.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceCategory
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.model.ui.MaintenanceUiState
import ru.nedumayy.shippingcalculator.domain.usecase.maintenance.AddMaintenanceRecordUseCase
import ru.nedumayy.shippingcalculator.domain.usecase.maintenance.DeleteMaintenanceRecordUseCase
import ru.nedumayy.shippingcalculator.domain.usecase.maintenance.GetMaintenanceRecordsUseCase
import ru.nedumayy.shippingcalculator.domain.usecase.maintenance.GetVehiclesUseCase
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val getMaintenanceRecordsUseCase: GetMaintenanceRecordsUseCase,
    private val addMaintenanceRecordUseCase: AddMaintenanceRecordUseCase,
    private val deleteMaintenanceRecordUseCase: DeleteMaintenanceRecordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceUiState())
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles) }
                if (vehicles.isNotEmpty() && _uiState.value.selectedVehicle == null) {
                    selectVehicle(vehicles.first())
                }
            }
        }
    }

    fun selectVehicle(vehicle: UserVehicle) {
        _uiState.update { it.copy(selectedVehicle = vehicle, isLoading = true) }
        viewModelScope.launch {
            getMaintenanceRecordsUseCase(vehicle.id).collect { records ->
                calculateStats(vehicle, records)
            }
        }
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
