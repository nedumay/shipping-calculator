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
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord

interface MaintenanceRepository {
    fun getMaintenanceRecords(vehicleId: Long): Flow<List<MaintenanceRecord>>
    suspend fun insertMaintenanceRecord(record: MaintenanceRecord)
    suspend fun deleteMaintenanceRecord(record: MaintenanceRecord)
}
