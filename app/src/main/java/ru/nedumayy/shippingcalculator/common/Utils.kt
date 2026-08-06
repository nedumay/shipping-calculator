/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun formatMoney(v: Double): String {
    val df = java.text.DecimalFormat("#,##0.00")
    return df.format(v)
}

fun formatDate(date: LocalDate): String {
    return date.format(dateFormatter)
}

fun parseDate(dateString: String): LocalDate {
    return try {
        LocalDate.parse(dateString, dateFormatter)
    } catch (e: Exception) {
        LocalDate.now()
    }
}
