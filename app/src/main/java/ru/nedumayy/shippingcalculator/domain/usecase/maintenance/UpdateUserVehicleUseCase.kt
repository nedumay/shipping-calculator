/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.domain.usecase.maintenance

import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.domain.repository.VehicleRepository
import javax.inject.Inject

class UpdateUserVehicleUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: UserVehicle) {
        repository.updateUserVehicle(vehicle)
    }
}
