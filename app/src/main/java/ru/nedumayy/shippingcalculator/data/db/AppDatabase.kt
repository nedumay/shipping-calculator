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
import ru.nedumayy.shippingcalculator.data.db.dao.CalculationDao
import ru.nedumayy.shippingcalculator.data.db.dao.SettingsDao
import ru.nedumayy.shippingcalculator.data.db.model.CalculationEntity
import ru.nedumayy.shippingcalculator.data.db.model.SettingsEntity

@Database(
    entities = [CalculationEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculationDao(): CalculationDao
    abstract fun settingsDao(): SettingsDao
}