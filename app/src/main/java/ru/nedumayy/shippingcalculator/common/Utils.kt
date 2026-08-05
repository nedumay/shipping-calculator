/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.common

fun formatMoney(v: Double): String {
    val df = java.text.DecimalFormat("#,##0.00")
    return df.format(v)
}


