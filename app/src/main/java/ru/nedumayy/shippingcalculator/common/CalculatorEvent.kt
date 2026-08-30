/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.common

import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.model.VehicleCategory

sealed class CalculatorEvent {
    data class CategorySelected(val category: VehicleCategory) : CalculatorEvent()
    data class RouteFromChanged(val value: String) : CalculatorEvent()
    data class RouteToChanged(val value: String) : CalculatorEvent()
    data class DeliveryDateChanged(val value: String) : CalculatorEvent()
    data class DistanceChanged(val value: String) : CalculatorEvent()
    data class FuelPriceChanged(val value: String) : CalculatorEvent()
    data class ExtraExpensesChanged(val value: String) : CalculatorEvent()
    data class MarginChanged(val value: String) : CalculatorEvent()
    data class FuelTypeChanged(val value: String) : CalculatorEvent()

    data class FuelConsumptionChanged(val value: String) : CalculatorEvent()
    data class VehiclePriceChanged(val value: String) : CalculatorEvent()
    data class ResourceMileageChanged(val value: String) : CalculatorEvent()
    data class AnnualTaxChanged(val value: String) : CalculatorEvent()
    data class AnnualInsuranceChanged(val value: String) : CalculatorEvent()
    data class AnnualMileageChanged(val value: String) : CalculatorEvent()
    data class MaintenancePerKmChanged(val value: String) : CalculatorEvent()

    data class SaveToHistory(val categoryName: String, val routeFromFallback: String) : CalculatorEvent()
    data class DeleteFromHistory(val id: Long) : CalculatorEvent()

    data class UserVehicleSelected(val vehicle: UserVehicle?) : CalculatorEvent()
    data object ShowSaveVehicleDialog : CalculatorEvent()
    data object HideSaveVehicleDialog : CalculatorEvent()
    data class NewVehicleNameChanged(val name: String) : CalculatorEvent()
    data object SaveUserVehicle : CalculatorEvent()
    data class DeleteUserVehicle(val vehicle: UserVehicle) : CalculatorEvent()
}
