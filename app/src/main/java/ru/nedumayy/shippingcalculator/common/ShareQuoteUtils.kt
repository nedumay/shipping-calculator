/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.common

import android.content.Context
import android.content.Intent
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.domain.model.DeliveryCalculation

object ShareQuoteUtils {

    fun buildQuoteText(
        context: Context,
        routeFrom: String,
        routeTo: String,
        deliveryDate: String,
        vehicleName: String,
        distance: Double,
        totalCost: Double,
        costPerKm: Double
    ): String {
        val fromText = routeFrom.ifBlank { context.getString(R.string.not_specified) }
        val toText = routeTo.ifBlank { context.getString(R.string.not_specified) }
        val routeStr = context.getString(R.string.quote_route, fromText, toText)
        val distStr = context.getString(R.string.quote_distance, String.format(java.util.Locale.US, "%.1f", distance))
        val dateStr = context.getString(R.string.quote_date, deliveryDate)
        val vehicleStr = context.getString(R.string.quote_vehicle, vehicleName)
        val totalStr = context.getString(R.string.quote_total, formatMoney(totalCost))
        val rateStr = context.getString(R.string.quote_rate, formatMoney(costPerKm))
        val header = context.getString(R.string.quote_header)
        val footer = context.getString(R.string.quote_footer)

        return formatQuoteString(header, routeStr, distStr, dateStr, vehicleStr, totalStr, rateStr, footer)
    }

    fun formatQuoteString(
        header: String,
        routeStr: String,
        distStr: String,
        dateStr: String,
        vehicleStr: String,
        totalStr: String,
        rateStr: String,
        footer: String
    ): String {
        return "$header\n\n$routeStr\n$distStr\n$dateStr\n$vehicleStr\n\n$totalStr\n$rateStr\n\n---\n$footer"
    }

    fun buildQuoteTextFromCalculation(
        context: Context,
        calculation: DeliveryCalculation
    ): String {
        return buildQuoteText(
            context = context,
            routeFrom = calculation.routeFrom,
            routeTo = calculation.routeTo,
            deliveryDate = calculation.deliveryDate,
            vehicleName = calculation.categoryName,
            distance = calculation.distance,
            totalCost = calculation.totalCost,
            costPerKm = calculation.costPerKm
        )
    }

    fun shareText(context: Context, title: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooserIntent = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}
