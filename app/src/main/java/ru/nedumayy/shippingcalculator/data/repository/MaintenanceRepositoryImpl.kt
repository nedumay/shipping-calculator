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
import ru.nedumayy.shippingcalculator.data.db.dao.MaintenanceDao
import ru.nedumayy.shippingcalculator.data.toDomain
import ru.nedumayy.shippingcalculator.data.toEntity
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.repository.MaintenanceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepositoryImpl @Inject constructor(
    private val maintenanceDao: MaintenanceDao
) : MaintenanceRepository {

    override fun getMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>> {
        return maintenanceDao.getRecordsForVehicle(vehicleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceDao.insertRecord(record.toEntity())
    }

    override suspend fun deleteMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceDao.deleteRecord(record.toEntity())
    }
}
