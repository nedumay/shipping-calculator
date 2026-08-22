/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.nedumayy.shippingcalculator.data.db.model.DeliveryCalculationEntity

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY createdAt DESC")
    fun getAllCalculations(): Flow<List<DeliveryCalculationEntity>>

    @Query("SELECT * FROM calculations WHERE id = :id")
    suspend fun getCalculationById(id: Long): DeliveryCalculationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: DeliveryCalculationEntity): Long

    @Delete
    suspend fun deleteCalculation(calculation: DeliveryCalculationEntity)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)

    @Query("DELETE FROM calculations")
    suspend fun clearHistory()
}
