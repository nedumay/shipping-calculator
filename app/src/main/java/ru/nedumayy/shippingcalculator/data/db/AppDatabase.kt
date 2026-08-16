/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.nedumayy.shippingcalculator.data.db.dao.CalculationDao
import ru.nedumayy.shippingcalculator.data.db.dao.MaintenanceDao
import ru.nedumayy.shippingcalculator.data.db.dao.SettingsDao
import ru.nedumayy.shippingcalculator.data.db.dao.UserVehicleDao
import ru.nedumayy.shippingcalculator.data.db.model.CalculationEntity
import ru.nedumayy.shippingcalculator.data.db.model.MaintenanceRecordEntity
import ru.nedumayy.shippingcalculator.data.db.model.SettingsEntity
import ru.nedumayy.shippingcalculator.data.db.model.UserVehicleEntity

@Database(
    entities = [
        CalculationEntity::class,
        SettingsEntity::class,
        UserVehicleEntity::class,
        MaintenanceRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculationDao(): CalculationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun userVehicleDao(): UserVehicleDao
    abstract fun maintenanceDao(): MaintenanceDao
}
