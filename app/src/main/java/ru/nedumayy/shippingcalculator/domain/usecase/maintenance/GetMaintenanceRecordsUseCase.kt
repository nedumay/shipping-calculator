/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase.maintenance

import kotlinx.coroutines.flow.Flow
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.repository.MaintenanceRepository
import javax.inject.Inject

class GetMaintenanceRecordsUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceRecord>> =
        repository.getMaintenanceRecords(vehicleId)
}
