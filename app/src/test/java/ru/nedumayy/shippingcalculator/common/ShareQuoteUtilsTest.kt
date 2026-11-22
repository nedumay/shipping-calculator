/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.common

import org.junit.Assert.assertTrue
import org.junit.Test

class ShareQuoteUtilsTest {

    @Test
    fun formatQuoteString_buildsFormattedOutput() {
        val header = "🚚 Delivery Cost Quote"
        val routeStr = "📍 Route: Moscow → Tver"
        val distStr = "📏 Distance: 180.0 km"
        val dateStr = "📅 Delivery date: 20.07.2026"
        val vehicleStr = "🚗 Vehicle: Van"
        val totalStr = "💰 Total cost: 8 500 ₽"
        val rateStr = "(Rate: 47.22 ₽/km)"
        val footer = "Calculated in Shipping Calculator"

        val result = ShareQuoteUtils.formatQuoteString(
            header = header,
            routeStr = routeStr,
            distStr = distStr,
            dateStr = dateStr,
            vehicleStr = vehicleStr,
            totalStr = totalStr,
            rateStr = rateStr,
            footer = footer
        )

        assertTrue(result.contains("🚚 Delivery Cost Quote"))
        assertTrue(result.contains("📍 Route: Moscow → Tver"))
        assertTrue(result.contains("📏 Distance: 180.0 km"))
        assertTrue(result.contains("💰 Total cost: 8 500 ₽"))
        assertTrue(result.contains("Calculated in Shipping Calculator"))
    }
}
