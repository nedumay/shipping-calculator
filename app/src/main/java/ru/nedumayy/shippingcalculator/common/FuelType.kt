package ru.nedumayy.shippingcalculator.common

import androidx.annotation.StringRes
import ru.nedumayy.shippingcalculator.R

object FuelType {
    const val PETROL = "petrol"
    const val DIESEL = "diesel"
    const val GAS = "gas"
    const val ELECTRICITY = "electricity"

    @StringRes
    fun labelResource(type: String): Int = when (type) {
        DIESEL -> R.string.fuel_type_diesel
        GAS -> R.string.fuel_type_gas
        ELECTRICITY -> R.string.fuel_type_electricity
        else -> R.string.fuel_type_petrol
    }
}
