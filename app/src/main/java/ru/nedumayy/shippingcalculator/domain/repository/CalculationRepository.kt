/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.nedumayy.shippingcalculator.domain.model.CalculatorSettings
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation

interface CalculationRepository {
    fun getAllCalculations(): Flow<List<DeliveryCalculation>>
    suspend fun insertCalculation(calculation: DeliveryCalculation)
    suspend fun deleteCalculation(id: Long)
    suspend fun clearHistory()

    suspend fun saveCurrentSettings(settings: CalculatorSettings)
    suspend fun getCurrentSettings(): CalculatorSettings?
}