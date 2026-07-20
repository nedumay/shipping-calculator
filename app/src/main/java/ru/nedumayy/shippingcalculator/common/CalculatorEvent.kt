package ru.nedumayy.shippingcalculator.common

import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory

/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */
sealed class CalculatorEvent {
    data class CategorySelected(val category: VehicleCategory) : CalculatorEvent()
    data class RouteFromChanged(val value: String) : CalculatorEvent()
    data class RouteToChanged(val value: String) : CalculatorEvent()
    data class DeliveryDateChanged(val value: String) : CalculatorEvent()
    data class DistanceChanged(val value: String) : CalculatorEvent()
    data class FuelPriceChanged(val value: String) : CalculatorEvent()
    data class ExtraExpensesChanged(val value: String) : CalculatorEvent()
    data class MarginChanged(val value: String) : CalculatorEvent()
    data class AnnualMileageChanged(val value: String) : CalculatorEvent()
    data object SaveToHistory : CalculatorEvent()
    data class DeleteFromHistory(val id: Long) : CalculatorEvent()
}