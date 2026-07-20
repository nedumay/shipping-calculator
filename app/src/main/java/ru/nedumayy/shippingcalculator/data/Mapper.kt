package ru.nedumayy.shippingcalculator.data

import ru.nedumayy.shippingcalculator.data.db.model.CalculationEntity
import ru.nedumayy.shippingcalculator.data.db.model.SettingsEntity
import ru.nedumayy.shippingcalculator.domain.model.CalculatorSettings
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation

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

// --- SettingsEntity <-> CalculatorSettings ---

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