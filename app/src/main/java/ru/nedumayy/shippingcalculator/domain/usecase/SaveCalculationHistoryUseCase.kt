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