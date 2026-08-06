/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.common.CalculatorEvent
import ru.nedumayy.shippingcalculator.common.UiText
import ru.nedumayy.shippingcalculator.common.formatDate
import ru.nedumayy.shippingcalculator.domain.model.CalculatorUiEffect
import ru.nedumayy.shippingcalculator.domain.model.CalculatorUiState
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import ru.nedumayy.shippingcalculator.domain.usecase.CalculateDeliveryCostUseCase
import ru.nedumayy.shippingcalculator.domain.usecase.SaveCalculationHistoryUseCase
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculateUseCase: CalculateDeliveryCostUseCase,
    private val saveHistoryUseCase: SaveCalculationHistoryUseCase,
    private val repository: CalculationRepository,
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CalculatorUiEffect>()
    val effects: SharedFlow<CalculatorUiEffect> = _effects.asSharedFlow()

    init {
        loadCategories()
        loadSavedSettings()
    }

    private fun loadCategories() {
        val categories = listOf(
            VehicleCategory("moped", UiText.StringResource(R.string.moped), 3.0, 120_000.0, 50_000.0, 500.0, 12_000.0, 1.5, 3_000.0, true),
            VehicleCategory("car", UiText.StringResource(R.string.car), 8.5, 1_800_000.0, 250_000.0, 4_500.0, 25_000.0, 3.5, 15_000.0, true),
            VehicleCategory("van", UiText.StringResource(R.string.van), 13.0, 3_500_000.0, 400_000.0, 12_000.0, 45_000.0, 6.0, 40_000.0, true),
            VehicleCategory("truck", UiText.StringResource(R.string.truck), 28.0, 10_000_000.0, 800_000.0, 45_000.0, 90_000.0, 15.0, 80_000.0, true)
        )
        val defaultCategory = categories[1]
        _state.update {
            it.copy(
                categories = categories,
                selectedCategory = defaultCategory,
                deliveryDate = formatDate(LocalDate.now()),
                fuelConsumption = defaultCategory.fuelConsumption.toString(),
                vehiclePrice = defaultCategory.vehiclePrice.toString(),
                resourceMileage = defaultCategory.resourceMileage.toString(),
                annualTax = defaultCategory.annualTax.toString(),
                maintenancePerKm = defaultCategory.maintenancePerKm.toString(),
                annualInsurance = defaultCategory.annualInsurance.toString()
            )
        }
    }

    private fun loadSavedSettings() {
        viewModelScope.launch {
            repository.getCurrentSettings()?.let { settings ->
                _state.update { currentState ->
                    currentState.copy(
                        fuelPrice = settings.fuelPrice.toString(),
                        annualMileage = settings.annualMileage.toString(),
                        marginPercent = settings.defaultMargin.toString()
                    )
                }
            }
        }
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
                        fuelConsumption = event.category.fuelConsumption.toString(),
                        vehiclePrice = event.category.vehiclePrice.toString(),
                        resourceMileage = event.category.resourceMileage.toString(),
                        annualTax = event.category.annualTax.toString(),
                        maintenancePerKm = event.category.maintenancePerKm.toString(),
                        annualInsurance = event.category.annualInsurance.toString()
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
            is CalculatorEvent.ExtraExpensesChanged -> {
                _state.update { it.copy(extraExpenses = event.value) }
                calculateCost()
            }
            is CalculatorEvent.MarginChanged -> {
                _state.update { it.copy(marginPercent = event.value) }
                calculateCost()
            }
            is CalculatorEvent.AnnualMileageChanged -> {
                _state.update { it.copy(annualMileage = event.value) }
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
            is CalculatorEvent.MaintenancePerKmChanged -> {
                _state.update { it.copy(maintenancePerKm = event.value) }
                calculateCost()
            }
            is CalculatorEvent.AnnualInsuranceChanged -> {
                _state.update { it.copy(annualInsurance = event.value) }
                calculateCost()
            }
            is CalculatorEvent.SaveToHistory -> saveToHistory()
            is CalculatorEvent.DeleteFromHistory -> repository.deleteCalculation(event.id)
        }
    }

    private suspend fun calculateCost() {
        val currentState = _state.value
        val category = currentState.selectedCategory ?: return

        // Create a virtual category with overridden parameters
        val virtualCategory = category.copy(
            fuelConsumption = currentState.fuelConsumption.toDoubleOrNull() ?: 0.0,
            vehiclePrice = currentState.vehiclePrice.toDoubleOrNull() ?: 0.0,
            resourceMileage = currentState.resourceMileage.toDoubleOrNull() ?: 0.0,
            annualTax = currentState.annualTax.toDoubleOrNull() ?: 0.0,
            maintenancePerKm = currentState.maintenancePerKm.toDoubleOrNull() ?: 0.0,
            annualInsurance = currentState.annualInsurance.toDoubleOrNull() ?: 0.0
        )

        val breakdown = calculateUseCase(
            category = virtualCategory,
            distance = currentState.distance.toDoubleOrNull() ?: 0.0,
            fuelPrice = currentState.fuelPrice.toDoubleOrNull() ?: 0.0,
            extraExpenses = currentState.extraExpenses.toDoubleOrNull() ?: 0.0,
            marginPercent = currentState.marginPercent.toDoubleOrNull() ?: 0.0,
            annualMileage = currentState.annualMileage.toDoubleOrNull() ?: virtualCategory.annualMileage
        )

        _state.update { it.copy(costBreakdown = breakdown) }
    }

    private suspend fun saveToHistory() {
        val currentState = _state.value
        val category = currentState.selectedCategory ?: return
        val breakdown = currentState.costBreakdown ?: return

        if (!currentState.canSave) {
            _effects.emit(CalculatorUiEffect(showMessage = context.getString(R.string.fill_required_fields)))
            return
        }

        _state.update { it.copy(isSaving = true) }

        try {
            val calculation = DeliveryCalculation(
                categoryName = category.label.asString(context),
                routeFrom = currentState.routeFrom.ifBlank { context.getString(R.string.not_specified) },
                routeTo = currentState.routeTo,
                deliveryDate = currentState.deliveryDate,
                distance = currentState.distance.toDoubleOrNull() ?: 0.0,
                fuelCost = breakdown.fuelTotal,
                depreciationCost = breakdown.depreciationTotal,
                taxCost = breakdown.taxTotal,
                maintenanceCost = breakdown.maintenanceTotal,
                insuranceCost = breakdown.insuranceTotal,
                extraExpenses = breakdown.extraTotal,
                marginPercent = currentState.marginPercent.toDoubleOrNull() ?: 0.0,
                totalCost = breakdown.finalTotal,
                costPerKm = breakdown.finalPerKm
            )

            saveHistoryUseCase(calculation)

            _effects.emit(CalculatorUiEffect(
                    showMessage = "${context.getString(R.string.saved)}: ${currentState.routeFrom} → ${currentState.routeTo}"
                )
            )
            _state.update { it.copy(showSuccessMessage = true, isSaving = false) }
        } catch (e: Exception) {
            _state.update { it.copy(isSaving = false, error = e.message) }
            _effects.emit(CalculatorUiEffect(
                showMessage = "${context.getString(R.string.error)}: ${e.message}")
            )
        }
    }
}
