/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data

import ru.nedumayy.shippingcalculator.data.db.model.CalculationEntity
import ru.nedumayy.shippingcalculator.data.db.model.MaintenanceRecordEntity
import ru.nedumayy.shippingcalculator.data.db.model.SettingsEntity
import ru.nedumayy.shippingcalculator.data.db.model.UserVehicleEntity
import ru.nedumayy.shippingcalculator.domain.model.CalculatorSettings
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import java.util.Date

fun CalculationEntity.toDomain(): DeliveryCalculation = DeliveryCalculation(
    id = id,
    categoryName = categoryName,
    routeFrom = routeFrom,
    routeTo = routeTo,
    deliveryDate = deliveryDate,
    distance = distance,
    fuelCost = fuelCost,
    depreciationCost = depreciationCost,
    taxCost = taxCost,
    maintenanceCost = maintenanceCost,
    insuranceCost = insuranceCost,
    extraExpenses = extraExpenses,
    marginPercent = marginPercent,
    totalCost = totalCost,
    costPerKm = costPerKm,
    createdAt = createdAt
)

fun DeliveryCalculation.toEntity(): CalculationEntity = CalculationEntity(
    id = id,
    categoryName = categoryName,
    routeFrom = routeFrom,
    routeTo = routeTo,
    deliveryDate = deliveryDate,
    distance = distance,
    fuelCost = fuelCost,
    depreciationCost = depreciationCost,
    taxCost = taxCost,
    maintenanceCost = maintenanceCost,
    insuranceCost = insuranceCost,
    extraExpenses = extraExpenses,
    marginPercent = marginPercent,
    totalCost = totalCost,
    costPerKm = costPerKm,
    createdAt = createdAt
)

fun SettingsEntity.toDomain(): CalculatorSettings = CalculatorSettings(
    selectedCategoryId = selectedCategoryId,
    fuelPrice = fuelPrice,
    annualMileage = annualMileage,
    defaultMargin = defaultMargin
)

fun CalculatorSettings.toEntity(): SettingsEntity = SettingsEntity(
    selectedCategoryId = selectedCategoryId,
    fuelPrice = fuelPrice,
    annualMileage = annualMileage,
    defaultMargin = defaultMargin
)

fun UserVehicleEntity.toDomain(): UserVehicle = UserVehicle(
    id = id,
    name = name,
    fuelConsumption = fuelConsumption,
    vehiclePrice = vehiclePrice,
    resourceMileage = resourceMileage,
    annualTax = annualTax,
    annualMileage = annualMileage,
    maintenancePerKm = maintenancePerKm,
    annualInsurance = annualInsurance,
    hasFuel = hasFuel,
    fuelPrice = fuelPrice,
    extraExpenses = extraExpenses,
    marginPercent = marginPercent
)

fun UserVehicle.toEntity(): UserVehicleEntity = UserVehicleEntity(
    id = id,
    name = name,
    fuelConsumption = fuelConsumption,
    vehiclePrice = vehiclePrice,
    resourceMileage = resourceMileage,
    annualTax = annualTax,
    annualMileage = annualMileage,
    maintenancePerKm = maintenancePerKm,
    annualInsurance = annualInsurance,
    hasFuel = hasFuel,
    fuelPrice = fuelPrice,
    extraExpenses = extraExpenses,
    marginPercent = marginPercent
)

fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord = MaintenanceRecord(
    id = id,
    vehicleId = vehicleId,
    date = Date(date),
    mileage = mileage,
    description = description,
    cost = cost,
    category = category
)

fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity = MaintenanceRecordEntity(
    id = id,
    vehicleId = vehicleId,
    date = date.time,
    mileage = mileage,
    description = description,
    cost = cost,
    category = category
)
