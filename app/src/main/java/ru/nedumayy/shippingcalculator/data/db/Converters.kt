/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db

import androidx.room.TypeConverter
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceCategory
import java.util.Date

class Converters {
    @TypeConverter
    fun fromMaintenanceCategory(value: MaintenanceCategory): String {
        return value.name
    }

    @TypeConverter
    fun toMaintenanceCategory(value: String): MaintenanceCategory {
        return MaintenanceCategory.valueOf(value)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
