/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.nedumayy.shippingcalculator.data.db.AppDatabase
import ru.nedumayy.shippingcalculator.data.db.dao.CalculationDao
import ru.nedumayy.shippingcalculator.data.db.dao.MaintenanceDao
import ru.nedumayy.shippingcalculator.data.db.dao.SettingsDao
import ru.nedumayy.shippingcalculator.data.db.dao.UserVehicleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE user_vehicles ADD COLUMN residualValue REAL NOT NULL DEFAULT 0.0"
            )
            database.execSQL(
                "UPDATE user_vehicles SET fuelType = CASE fuelType " +
                    "WHEN 'Бензин' THEN 'petrol' WHEN 'Дизель' THEN 'diesel' " +
                    "WHEN 'Газ' THEN 'gas' WHEN 'Электричество' THEN 'electricity' " +
                    "ELSE fuelType END"
            )
            database.execSQL(
                "UPDATE calculations SET fuelType = CASE fuelType " +
                    "WHEN 'Бензин' THEN 'petrol' WHEN 'Дизель' THEN 'diesel' " +
                    "WHEN 'Газ' THEN 'gas' WHEN 'Электричество' THEN 'electricity' " +
                    "ELSE fuelType END"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "shipping_calculator.db")
            .addMigrations(MIGRATION_8_9)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCalculationDao(database: AppDatabase): CalculationDao =
        database.calculationDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao =
        database.settingsDao()

    @Provides
    @Singleton
    fun provideUserVehicleDao(database: AppDatabase): UserVehicleDao =
        database.userVehicleDao()

    @Provides
    @Singleton
    fun provideMaintenanceDao(database: AppDatabase): MaintenanceDao =
        database.maintenanceDao()
}
