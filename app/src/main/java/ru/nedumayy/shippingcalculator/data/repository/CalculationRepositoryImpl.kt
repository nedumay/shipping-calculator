/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.nedumayy.shippingcalculator.data.db.dao.CalculationDao
import ru.nedumayy.shippingcalculator.data.toDomain
import ru.nedumayy.shippingcalculator.data.toEntity
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculationRepositoryImpl @Inject constructor(
    private val calculationDao: CalculationDao
) : CalculationRepository {

    override fun getAllCalculations(): Flow<List<DeliveryCalculation>> {
        return calculationDao.getAllCalculations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCalculation(calculation: DeliveryCalculation) {
        calculationDao.insertCalculation(calculation.toEntity())
    }

    override suspend fun deleteCalculation(id: Long) {
        calculationDao.deleteCalculationById(id)
    }

    override suspend fun clearHistory() {
        calculationDao.clearHistory()
    }
}
