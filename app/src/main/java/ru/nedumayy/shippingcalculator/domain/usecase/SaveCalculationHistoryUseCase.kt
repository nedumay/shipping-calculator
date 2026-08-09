/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase

import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation
import ru.nedumayy.shippingcalculator.domain.repository.CalculationRepository
import javax.inject.Inject

class SaveCalculationHistoryUseCase @Inject constructor(
    private val repository: CalculationRepository
) {
    suspend operator fun invoke(calculation: DeliveryCalculation) {
        repository.insertCalculation(calculation)
    }
}