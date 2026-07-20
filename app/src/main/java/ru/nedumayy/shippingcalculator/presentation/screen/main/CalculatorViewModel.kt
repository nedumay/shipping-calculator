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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nedumayy.shippingcalculator.common.CalculatorEvent
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
    private val repository: CalculationRepository
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
            VehicleCategory("foot", "Пешком/вело", 0.0, 15_000.0, 20_000.0, 0.0, 5_000.0, 0.0, 0.0, false),
            VehicleCategory("moto", "Мотоцикл", 3.5, 180_000.0, 80_000.0, 1_200.0, 15_000.0, 1.0, 3_000.0, true),
            VehicleCategory("car", "Легковой авто", 9.0, 1_200_000.0, 300_000.0, 6_000.0, 30_000.0, 2.5, 25_000.0, true),
            VehicleCategory("van", "Фургон", 14.0, 2_200_000.0, 400_000.0, 9_000.0, 40_000.0, 4.0, 40_000.0, true)
        )
        _state.update {
            it.copy(
                categories = categories,
                selectedCategory = categories[2],
                deliveryDate = LocalDate.now().toString()
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
                _state.update { it.copy(selectedCategory = event.category) }
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
            is CalculatorEvent.SaveToHistory -> saveToHistory()
            is CalculatorEvent.DeleteFromHistory -> repository.deleteCalculation(event.id)
        }
    }

    private suspend fun calculateCost() {
        val currentState = _state.value
        val category = currentState.selectedCategory ?: return

        val breakdown = calculateUseCase(
            category = category,
            distance = currentState.distance.toDoubleOrNull() ?: 0.0,
            fuelPrice = currentState.fuelPrice.toDoubleOrNull() ?: 0.0,
            extraExpenses = currentState.extraExpenses.toDoubleOrNull() ?: 0.0,
            marginPercent = currentState.marginPercent.toDoubleOrNull() ?: 0.0,
            annualMileage = currentState.annualMileage.toDoubleOrNull() ?: 30000.0
        )

        _state.update { it.copy(costBreakdown = breakdown) }
    }

    private suspend fun saveToHistory() {
        val currentState = _state.value
        val category = currentState.selectedCategory ?: return
        val breakdown = currentState.costBreakdown ?: return

        if (!currentState.canSave) {
            _effects.emit(CalculatorUiEffect(showMessage = "Заполните обязательные поля"))
            return
        }

        _state.update { it.copy(isSaving = true) }

        try {
            val calculation = DeliveryCalculation(
                categoryName = category.label,
                routeFrom = currentState.routeFrom.ifBlank { "Не указан" },
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

            _effects.emit(
                CalculatorUiEffect(
                    showMessage = "Сохранено: ${currentState.routeFrom} → ${currentState.routeTo}"
                )
            )
            _state.update { it.copy(showSuccessMessage = true, isSaving = false) }
        } catch (e: Exception) {
            _state.update { it.copy(isSaving = false, error = e.message) }
            _effects.emit(CalculatorUiEffect(showMessage = "Ошибка: ${e.message}"))
        }
    }
}