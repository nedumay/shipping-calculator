/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.repository

import ru.nedumayy.shippingcalculator.domain.model.CalculatorSettings

interface SettingsRepository {
    suspend fun saveCurrentSettings(settings: CalculatorSettings)
    suspend fun getCurrentSettings(): CalculatorSettings?
}
