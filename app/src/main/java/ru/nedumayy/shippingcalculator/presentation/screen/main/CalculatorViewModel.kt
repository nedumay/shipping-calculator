/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.common.CalculatorEvent
import ru.nedumayy.shippingcalculator.common.UiText
import ru.nedumayy.shippingcalculator.common.formatDate
import ru.nedumayy.shippingcalculator.common.parseSafeDouble
import ru.nedumayy.shippingcalculator.domain.model.ui.CalculatorUiEffect
import ru.nedumayy.shippingcalculator.domain.model.ui.CalculatorUiState
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import ru.nedumayy.shippingcalculator.domain.repository.VehicleRepository
import ru.nedumayy.shippingcalculator.domain.usecase.CalculateDeliveryCostUseCase
import ru.nedumayy.shippingcalculator.domain.usecase.SaveCalculationHistoryUseCase
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculateUseCase: CalculateDeliveryCostUseCase,
    private val saveHistoryUseCase: SaveCalculationHistoryUseCase,
    private val calculationRepository: CalculationRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CalculatorUiEffect>()
    val effects: SharedFlow<CalculatorUiEffect> = _effects.asSharedFlow()

    init {
        loadCategories()
        observeUserVehicles()
    }

    private fun loadCategories() {
        val categories = listOf(
            VehicleCategory("moped", UiText.StringResource(R.string.moped), 3.0, 120_000.0, 50_000.0, 1000.0, 2500.0, 5000.0, 1.5, true),
            VehicleCategory("car", UiText.StringResource(R.string.car), 8.5, 1_800_000.0, 250_000.0, 10000.0, 30000.0, 25000.0, 3.5, true),
            VehicleCategory("van", UiText.StringResource(R.string.van), 13.0, 3_500_000.0, 400_000.0, 30000.0, 75000.0, 30000.0, 6.0, true),
            VehicleCategory("truck", UiText.StringResource(R.string.truck), 28.0, 10_000_000.0, 800_000.0, 100000.0, 250000.0, 50000.0, 15.0, true)
        )
        val defaultCategory = categories[1]
        _state.update {
            it.copy(
                categories = categories,
                selectedCategory = defaultCategory,
                deliveryDate = formatDate(LocalDate.now())
            )
        }
    }

    private fun observeUserVehicles() {
        vehicleRepository.getAllUserVehicles()
            .onEach { vehicles ->
                _state.update { it.copy(userVehicles = vehicles) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: CalculatorEvent) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }

    private suspend fun handleEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.CategorySelected -> {
                _state.update { 
                    it.copy(
                        selectedCategory = event.category,
                        selectedUserVehicle = null,
                        fuelConsumption = "",
                        vehiclePrice = "",
                        resourceMileage = "",
                        annualTax = "",
                        annualInsurance = "",
                        annualMileage = "",
                        maintenancePerKm = ""
                    ) 
                }
                calculateCost()
            }
            is CalculatorEvent.RouteFromChanged -> _state.update { it.copy(routeFrom = event.value) }
            is CalculatorEvent.RouteToChanged -> {
                _state.update { it.copy(routeTo = event.value) }
                calculateCost()
            }
            is CalculatorEvent.DeliveryDateChanged -> _state.update { it.copy(deliveryDate = event.value) }
            is CalculatorEvent.DistanceChanged -> {
                _state.update { it.copy(distance = event.value) }
                calculateCost()
            }
            is CalculatorEvent.FuelPriceChanged -> {
                _state.update { it.copy(fuelPrice = event.value) }
                calculateCost()
            }
            is CalculatorEvent.FuelTypeChanged -> _state.update { it.copy(fuelType = event.value) }
            is CalculatorEvent.ExtraExpensesChanged -> {
                _state.update { it.copy(extraExpenses = event.value) }
                calculateCost()
            }
            is CalculatorEvent.MarginChanged -> {
                _state.update { it.copy(marginPercent = event.value) }
                calculateCost()
            }
            is CalculatorEvent.FuelConsumptionChanged -> {
                _state.update { it.copy(fuelConsumption = event.value) }
                calculateCost()
            }
            is CalculatorEvent.VehiclePriceChanged -> {
                _state.update { it.copy(vehiclePrice = event.value) }
                calculateCost()
            }
            is CalculatorEvent.ResourceMileageChanged -> {
                _state.update { it.copy(resourceMileage = event.value) }
                calculateCost()
            }
            is CalculatorEvent.AnnualTaxChanged -> {
                _state.update { it.copy(annualTax = event.value) }
                calculateCost()
            }
            is CalculatorEvent.AnnualInsuranceChanged -> {
                _state.update { it.copy(annualInsurance = event.value) }
                calculateCost()
            }
            is CalculatorEvent.AnnualMileageChanged -> {
                _state.update { it.copy(annualMileage = event.value) }
                calculateCost()
            }
            is CalculatorEvent.MaintenancePerKmChanged -> {
                _state.update { it.copy(maintenancePerKm = event.value) }
                calculateCost()
            }
            is CalculatorEvent.SaveToHistory -> saveToHistory(event.categoryName, event.routeFromFallback)
            is CalculatorEvent.DeleteFromHistory -> {
                try {
                    calculationRepository.deleteCalculation(event.id)
                } catch (e: Exception) {
                    _effects.emit(CalculatorUiEffect(showMessage = UiText.DynamicString(e.message ?: "Unknown error")))
                }
            }
            
            is CalculatorEvent.UserVehicleSelected -> {
                event.vehicle?.let { vehicle ->
                    _state.update { 
                        it.copy(
                            selectedUserVehicle = vehicle,
                            selectedCategory = null,
                            fuelType = vehicle.fuelType,
                            fuelConsumption = vehicle.fuelConsumption.toString(),
                            vehiclePrice = vehicle.vehiclePrice.toString(),
                            resourceMileage = vehicle.resourceMileage.toString(),
                            annualTax = vehicle.annualTax.toString(),
                            annualInsurance = vehicle.annualInsurance.toString(),
                            annualMileage = vehicle.annualMileage.toString(),
                            maintenancePerKm = vehicle.maintenancePerKm.toString(),
                            fuelPrice = if (vehicle.fuelPrice > 0) vehicle.fuelPrice.toString() else it.fuelPrice,
                            extraExpenses = if (vehicle.extraExpenses > 0) vehicle.extraExpenses.toString() else it.extraExpenses,
                            marginPercent = if (vehicle.marginPercent > 0) vehicle.marginPercent.toString() else it.marginPercent
                        )
                    }
                } ?: run {
                    _state.update { it.copy(selectedUserVehicle = null) }
                }
                calculateCost()
            }
            is CalculatorEvent.ShowSaveVehicleDialog -> _state.update { it.copy(showSaveVehicleDialog = true) }
            is CalculatorEvent.HideSaveVehicleDialog -> _state.update { it.copy(showSaveVehicleDialog = false, newVehicleName = "") }
            is CalculatorEvent.NewVehicleNameChanged -> _state.update { it.copy(newVehicleName = event.name) }
            is CalculatorEvent.SaveUserVehicle -> saveUserVehicle()
            is CalculatorEvent.DeleteUserVehicle -> {
                try {
                    vehicleRepository.deleteUserVehicle(event.vehicle)
                } catch (e: Exception) {
                    _effects.emit(CalculatorUiEffect(showMessage = UiText.DynamicString(e.message ?: "Unknown error")))
                }
            }
        }
    }

    private suspend fun saveUserVehicle() {
        val currentState = _state.value
        if (currentState.newVehicleName.isBlank()) return

        try {
            val vehicle = UserVehicle(
                name = currentState.newVehicleName,
                fuelType = currentState.fuelType,
                fuelConsumption = parseSafeDouble(currentState.fuelConsumption),
                vehiclePrice = parseSafeDouble(currentState.vehiclePrice),
                resourceMileage = parseSafeDouble(currentState.resourceMileage),
                annualTax = parseSafeDouble(currentState.annualTax),
                annualInsurance = parseSafeDouble(currentState.annualInsurance),
                annualMileage = parseSafeDouble(currentState.annualMileage),
                maintenancePerKm = parseSafeDouble(currentState.maintenancePerKm),
                hasFuel = true,
                fuelPrice = parseSafeDouble(currentState.fuelPrice),
                extraExpenses = parseSafeDouble(currentState.extraExpenses),
                marginPercent = parseSafeDouble(currentState.marginPercent)
            )

            vehicleRepository.insertUserVehicle(vehicle)
            _state.update { it.copy(showSaveVehicleDialog = false, newVehicleName = "") }
            _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.saved)))
        } catch (e: Exception) {
            _effects.emit(CalculatorUiEffect(showMessage = UiText.DynamicString(e.message ?: "Unknown error")))
        }
    }

    private fun calculateCost() {
        val currentState = _state.value
        
        val fuelConsumption = parseSafeDouble(currentState.fuelConsumption)
        val vehiclePrice = parseSafeDouble(currentState.vehiclePrice)
        val resourceMileage = parseSafeDouble(currentState.resourceMileage)
        val annualTax = parseSafeDouble(currentState.annualTax)
        val annualInsurance = parseSafeDouble(currentState.annualInsurance)
        val annualMileage = parseSafeDouble(currentState.annualMileage)
        val maintenancePerKm = parseSafeDouble(currentState.maintenancePerKm)

        val baseCategory = currentState.selectedCategory ?: currentState.categories.getOrNull(1) ?: return

        val virtualCategory = baseCategory.copy(
            fuelConsumption = fuelConsumption,
            vehiclePrice = vehiclePrice,
            resourceMileage = resourceMileage,
            annualTax = annualTax,
            annualInsurance = annualInsurance,
            annualMileage = annualMileage,
            maintenancePerKm = maintenancePerKm
        )

        val breakdown = calculateUseCase(
            category = virtualCategory,
            distance = parseSafeDouble(currentState.distance),
            fuelPrice = parseSafeDouble(currentState.fuelPrice),
            extraExpenses = parseSafeDouble(currentState.extraExpenses),
            marginPercent = parseSafeDouble(currentState.marginPercent)
        )

        _state.update { it.copy(costBreakdown = breakdown) }
    }

    private suspend fun saveToHistory(categoryName: String, routeFromFallback: String) {
        val currentState = _state.value
        val breakdown = currentState.costBreakdown ?: return

        if (currentState.routeTo.isBlank() || parseSafeDouble(currentState.distance) <= 0.0) {
            _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.fill_required_fields)))
            return
        }

        _state.update { it.copy(isSaving = true) }

        try {
            val calculation = DeliveryCalculation(
                categoryName = categoryName,
                routeFrom = currentState.routeFrom.ifBlank { routeFromFallback },
                routeTo = currentState.routeTo,
                fuelType = currentState.fuelType,
                deliveryDate = currentState.deliveryDate,
                distance = parseSafeDouble(currentState.distance),
                fuelCost = breakdown.fuelTotal,
                depreciationCost = breakdown.depreciationTotal,
                taxCost = breakdown.taxTotal,
                maintenanceCost = breakdown.maintenanceTotal,
                insuranceCost = breakdown.insuranceTotal,
                extraExpenses = breakdown.extraTotal,
                marginPercent = parseSafeDouble(currentState.marginPercent),
                totalCost = breakdown.finalTotal,
                costPerKm = breakdown.finalPerKm
            )

            saveHistoryUseCase(calculation)

            currentState.selectedUserVehicle?.let { vehicle ->
                val updatedVehicle = vehicle.copy(
                    fuelConsumption = parseSafeDouble(currentState.fuelConsumption),
                    vehiclePrice = parseSafeDouble(currentState.vehiclePrice),
                    resourceMileage = parseSafeDouble(currentState.resourceMileage),
                    annualTax = parseSafeDouble(currentState.annualTax),
                    annualInsurance = parseSafeDouble(currentState.annualInsurance),
                    annualMileage = parseSafeDouble(currentState.annualMileage),
                    maintenancePerKm = parseSafeDouble(currentState.maintenancePerKm),
                    fuelPrice = parseSafeDouble(currentState.fuelPrice),
                    extraExpenses = parseSafeDouble(currentState.extraExpenses),
                    marginPercent = parseSafeDouble(currentState.marginPercent)
                )
                vehicleRepository.updateUserVehicle(updatedVehicle)
            }

            _effects.emit(CalculatorUiEffect(
                    showMessage = UiText.StringResource(R.string.saved)
                )
            )
            _state.update { it.copy(showSuccessMessage = true, isSaving = false) }
        } catch (e: Exception) {
            _state.update { it.copy(isSaving = false, error = e.message) }
            _effects.emit(CalculatorUiEffect(
                showMessage = UiText.DynamicString(e.message ?: "Unknown error"))
            )
        }
    }
}
