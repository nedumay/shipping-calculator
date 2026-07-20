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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.nedumayy.shippingcalculator.data.db.AppDatabase
import ru.nedumayy.shippingcalculator.data.db.dao.CalculationDao
import ru.nedumayy.shippingcalculator.data.db.dao.SettingsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "shipping_calculator.db")
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
}