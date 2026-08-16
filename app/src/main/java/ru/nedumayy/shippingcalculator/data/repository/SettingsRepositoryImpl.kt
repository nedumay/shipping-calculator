/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.data.repository

import ru.nedumayy.shippingcalculator.data.db.dao.SettingsDao
import ru.nedumayy.shippingcalculator.data.db.model.SettingsEntity
import ru.nedumayy.shippingcalculator.data.toDomain
import ru.nedumayy.shippingcalculator.domain.model.CalculatorSettings
import ru.nedumayy.shippingcalculator.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun saveCurrentSettings(settings: CalculatorSettings) {
        settingsDao.saveSettings(
            SettingsEntity(
                selectedCategoryId = settings.selectedCategoryId,
                fuelPrice = settings.fuelPrice,
                annualMileage = settings.annualMileage,
                defaultMargin = settings.defaultMargin
            )
        )
    }

    override suspend fun getCurrentSettings(): CalculatorSettings? {
        return settingsDao.getSettings()?.toDomain()
    }
}
