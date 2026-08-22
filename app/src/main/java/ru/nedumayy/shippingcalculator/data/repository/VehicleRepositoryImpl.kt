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
import ru.nedumayy.shippingcalculator.data.db.dao.UserVehicleDao
import ru.nedumayy.shippingcalculator.data.toDomain
import ru.nedumayy.shippingcalculator.data.toEntity
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.repository.VehicleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val userVehicleDao: UserVehicleDao
) : VehicleRepository {

    override fun getAllUserVehicles(): Flow<List<UserVehicle>> {
        return userVehicleDao.getAllVehicles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertUserVehicle(vehicle: UserVehicle) {
        userVehicleDao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun deleteUserVehicle(vehicle: UserVehicle) {
        userVehicleDao.deleteVehicle(vehicle.toEntity())
    }

    override suspend fun updateUserVehicle(vehicle: UserVehicle) {
        userVehicleDao.updateVehicle(vehicle.toEntity())
    }
}
