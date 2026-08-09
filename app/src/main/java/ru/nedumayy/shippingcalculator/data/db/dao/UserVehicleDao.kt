/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.nedumayy.shippingcalculator.data.db.model.UserVehicleEntity

@Dao
interface UserVehicleDao {
    @Query("SELECT * FROM user_vehicles")
    fun getAllVehicles(): Flow<List<UserVehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: UserVehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: UserVehicleEntity)

    @Update
    suspend fun updateVehicle(vehicle: UserVehicleEntity)
}
