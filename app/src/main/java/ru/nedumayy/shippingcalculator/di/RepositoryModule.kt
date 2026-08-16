/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nedumayy.shippingcalculator.data.repository.CalculationRepositoryImpl
import ru.nedumayy.shippingcalculator.data.repository.MaintenanceRepositoryImpl
import ru.nedumayy.shippingcalculator.data.repository.SettingsRepositoryImpl
import ru.nedumayy.shippingcalculator.data.repository.VehicleRepositoryImpl
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import ru.nedumayy.shippingcalculator.domain.repository.MaintenanceRepository
import ru.nedumayy.shippingcalculator.domain.repository.SettingsRepository
import ru.nedumayy.shippingcalculator.domain.repository.VehicleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCalculationRepository(
        impl: CalculationRepositoryImpl
    ): CalculationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        impl: VehicleRepositoryImpl
    ): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(
        impl: MaintenanceRepositoryImpl
    ): MaintenanceRepository
}
