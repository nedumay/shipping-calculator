/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.model

import java.util.Date

data class MaintenanceRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: Date,
    val mileage: Double,
    val description: String,
    val cost: Double,
    val category: MaintenanceCategory
)

enum class MaintenanceCategory {
    SERVICE,
    REPAIR,
    TIRES,
    CONSUMABLES,
    OTHER
}
