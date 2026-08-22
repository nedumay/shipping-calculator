/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceCategory

@Entity(tableName = "maintenance_records")
data class MaintenanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val date: Long,
    val mileage: Double,
    val description: String,
    val cost: Double,
    val category: MaintenanceCategory
)
