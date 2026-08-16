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
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle

interface VehicleRepository {
    fun getAllUserVehicles(): Flow<List<UserVehicle>>
    suspend fun insertUserVehicle(vehicle: UserVehicle)
    suspend fun deleteUserVehicle(vehicle: UserVehicle)
    suspend fun updateUserVehicle(vehicle: UserVehicle)
}
