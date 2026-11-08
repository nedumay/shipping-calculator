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
import ru.nedumayy.shippingcalculator.common.FuelType
import ru.nedumayy.shippingcalculator.common.UiText
import ru.nedumayy.shippingcalculator.common.formatDate
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
            VehicleCategory("moped", UiText.StringResource(R.string.moped), 3.0, 120_000.0, 0.0, 50_000.0, 1000.0, 2500.0, 5000.0, 1.5, true),
            VehicleCategory("car", UiText.StringResource(R.string.car), 8.5, 1_800_000.0, 0.0, 250_000.0, 10000.0, 30000.0, 25000.0, 3.5, true),
            VehicleCategory("van", UiText.StringResource(R.string.van), 13.0, 3_500_000.0, 0.0, 400_000.0, 30000.0, 75000.0, 30000.0, 6.0, true),
            VehicleCategory("truck", UiText.StringResource(R.string.truck), 28.0, 10_000_000.0, 0.0, 800_000.0, 100000.0, 250000.0, 50000.0, 15.0, true)
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
                        fuelType = FuelType.PETROL,
                        fuelConsumption = "",
                        vehiclePrice = "",
                        residualValue = "",
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
            is CalculatorEvent.ResidualValueChanged -> {
                _state.update { it.copy(residualValue = event.value) }
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
                } catch (_: Exception) {
                    _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.generic_error)))
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
                            residualValue = vehicle.residualValue.toString(),
                            resourceMileage = vehicle.resourceMileage.toString(),
                            annualTax = vehicle.annualTax.toString(),
                            annualInsurance = vehicle.annualInsurance.toString(),
                            annualMileage = vehicle.annualMileage.toString(),
                            maintenancePerKm = vehicle.maintenancePerKm.toString(),
                            fuelPrice = vehicle.fuelPrice.toString(),
                            extraExpenses = vehicle.extraExpenses.toString(),
                            marginPercent = vehicle.marginPercent.toString()
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
                } catch (_: Exception) {
                    _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.generic_error)))
                }
            }
        }
    }

    private suspend fun saveUserVehicle() {
        val currentState = _state.value
        if (currentState.newVehicleName.isBlank()) return

        val numericValues = listOf(
            currentState.fuelConsumption,
            currentState.vehiclePrice,
            currentState.residualValue,
            currentState.resourceMileage,
            currentState.annualTax,
            currentState.annualInsurance,
            currentState.annualMileage,
            currentState.maintenancePerKm,
            currentState.fuelPrice,
            currentState.extraExpenses,
            currentState.marginPercent
        ).map { parseFiniteDouble(it) }
        if (numericValues.any { it == null } ||
            numericValues.any { it!! < 0.0 } ||
            numericValues[3]!! <= 0.0 || numericValues[6]!! <= 0.0 ||
            numericValues[2]!! > numericValues[1]!!) {
            _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.fill_required_fields)))
            return
        }

        try {
            val vehicle = UserVehicle(
                name = currentState.newVehicleName,
                fuelType = currentState.fuelType,
                fuelConsumption = numericValues[0]!!,
                vehiclePrice = numericValues[1]!!,
                residualValue = numericValues[2]!!,
                resourceMileage = numericValues[3]!!,
                annualTax = numericValues[4]!!,
                annualInsurance = numericValues[5]!!,
                annualMileage = numericValues[6]!!,
                maintenancePerKm = numericValues[7]!!,
                hasFuel = true,
                fuelPrice = numericValues[8]!!,
                extraExpenses = numericValues[9]!!,
                marginPercent = numericValues[10]!!
            )

            vehicleRepository.insertUserVehicle(vehicle)
            _state.update { it.copy(showSaveVehicleDialog = false, newVehicleName = "") }
            _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.saved)))
        } catch (_: Exception) {
            _effects.emit(CalculatorUiEffect(showMessage = UiText.StringResource(R.string.generic_error)))
        }
    }

    private fun calculateCost() {
        val currentState = _state.value

        val baseCategory = currentState.selectedCategory
            ?: currentState.categories.getOrNull(1)
            ?: run {
                _state.update { it.copy(costBreakdown = null, calculationError = UiText.StringResource(R.string.select_transport)) }
                return
            }
        val hasFuel = currentState.selectedUserVehicle?.hasFuel ?: baseCategory.hasFuel
        val fuelConsumption = parseFiniteDouble(currentState.fuelConsumption)
        val vehiclePrice = parseFiniteDouble(currentState.vehiclePrice)
        val residualValue = parseFiniteDouble(currentState.residualValue)
        val resourceMileage = parseFiniteDouble(currentState.resourceMileage)
        val annualTax = parseFiniteDouble(currentState.annualTax)
        val annualInsurance = parseFiniteDouble(currentState.annualInsurance)
        val annualMileage = parseFiniteDouble(currentState.annualMileage)
        val maintenancePerKm = parseFiniteDouble(currentState.maintenancePerKm)
        val distance = parseFiniteDouble(currentState.distance)
        val fuelPrice = parseFiniteDouble(currentState.fuelPrice)
        val extraExpenses = parseFiniteDouble(currentState.extraExpenses)
        val marginPercent = parseFiniteDouble(currentState.marginPercent)

        val numbers = listOf(
            fuelConsumption, vehiclePrice, residualValue, resourceMileage, annualTax,
            annualInsurance, annualMileage, maintenancePerKm, distance, extraExpenses,
            marginPercent
        ) + if (hasFuel) listOf(fuelPrice) else emptyList()
        val invalidMessage = when {
            numbers.any { it == null } -> R.string.invalid_numeric_input
            numbers.any { it!!.isFinite().not() } -> R.string.invalid_numeric_input
            distance!! <= 0.0 -> R.string.distance_must_be_positive
            annualMileage!! <= 0.0 -> R.string.annual_mileage_must_be_positive
            resourceMileage!! <= 0.0 -> R.string.resource_mileage_must_be_positive
            numbers.any { it!! < 0.0 } -> R.string.values_must_be_non_negative
            residualValue!! > vehiclePrice!! -> R.string.residual_value_too_high
            else -> null
        }
        if (invalidMessage != null) {
            _state.update {
                it.copy(costBreakdown = null, calculationError = UiText.StringResource(invalidMessage))
            }
            return
        }

        val virtualCategory = baseCategory.copy(
            hasFuel = hasFuel,
            fuelConsumption = fuelConsumption!!,
            vehiclePrice = vehiclePrice!!,
            residualValue = residualValue!!,
            resourceMileage = resourceMileage!!,
            annualTax = annualTax!!,
            annualInsurance = annualInsurance!!,
            annualMileage = annualMileage!!,
            maintenancePerKm = maintenancePerKm!!
        )

        val breakdown = calculateUseCase(
            category = virtualCategory,
            distance = distance!!,
            fuelPrice = if (hasFuel) fuelPrice!! else 0.0,
            extraExpenses = extraExpenses!!,
            marginPercent = marginPercent!!
        )

        _state.update { it.copy(costBreakdown = breakdown, calculationError = null) }
    }

    private suspend fun saveToHistory(categoryName: String, routeFromFallback: String) {
        calculateCost()
        val currentState = _state.value
        val breakdown = currentState.costBreakdown ?: return

        val distance = parseFiniteDouble(currentState.distance)
        val marginPercent = parseFiniteDouble(currentState.marginPercent)
        if (currentState.routeTo.isBlank() || distance == null || marginPercent == null) {
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
                distance = distance,
                fuelCost = breakdown.fuelTotal,
                depreciationCost = breakdown.depreciationTotal,
                taxCost = breakdown.taxTotal,
                maintenanceCost = breakdown.maintenanceTotal,
                insuranceCost = breakdown.insuranceTotal,
                extraExpenses = breakdown.extraTotal,
                marginPercent = marginPercent,
                totalCost = breakdown.finalTotal,
                costPerKm = breakdown.finalPerKm
            )

            saveHistoryUseCase(calculation)

            currentState.selectedUserVehicle?.let { vehicle ->
                val updatedVehicle = vehicle.copy(
                    fuelConsumption = parseFiniteDouble(currentState.fuelConsumption) ?: vehicle.fuelConsumption,
                    vehiclePrice = parseFiniteDouble(currentState.vehiclePrice) ?: vehicle.vehiclePrice,
                    residualValue = parseFiniteDouble(currentState.residualValue) ?: vehicle.residualValue,
                    resourceMileage = parseFiniteDouble(currentState.resourceMileage) ?: vehicle.resourceMileage,
                    annualTax = parseFiniteDouble(currentState.annualTax) ?: vehicle.annualTax,
                    annualInsurance = parseFiniteDouble(currentState.annualInsurance) ?: vehicle.annualInsurance,
                    annualMileage = parseFiniteDouble(currentState.annualMileage) ?: vehicle.annualMileage,
                    maintenancePerKm = parseFiniteDouble(currentState.maintenancePerKm) ?: vehicle.maintenancePerKm,
                    fuelPrice = parseFiniteDouble(currentState.fuelPrice) ?: vehicle.fuelPrice,
                    extraExpenses = parseFiniteDouble(currentState.extraExpenses) ?: vehicle.extraExpenses,
                    marginPercent = parseFiniteDouble(currentState.marginPercent) ?: vehicle.marginPercent
                )
                vehicleRepository.updateUserVehicle(updatedVehicle)
            }

            _effects.emit(CalculatorUiEffect(
                    showMessage = UiText.StringResource(R.string.saved)
                )
            )
            _state.update { it.copy(showSuccessMessage = true, isSaving = false) }
        } catch (_: Exception) {
            _state.update { it.copy(isSaving = false, error = null) }
            _effects.emit(CalculatorUiEffect(
                showMessage = UiText.StringResource(R.string.generic_error))
            )
        }
    }
}

private fun parseFiniteDouble(value: String): Double? =
    value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }
