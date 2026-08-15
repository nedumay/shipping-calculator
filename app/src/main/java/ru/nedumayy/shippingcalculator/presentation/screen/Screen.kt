/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ru.nedumayy.shippingcalculator.R

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Calculator : Screen("calculator", R.string.screen_calculator, Icons.Default.Home)
    object History : Screen("history", R.string.screen_history, Icons.AutoMirrored.Filled.List)
    object Analytics : Screen("analytics", R.string.screen_analytics, Icons.Default.Info)
    object Maintenance : Screen("maintenance", R.string.screen_maintenance, Icons.Default.Build)

    companion object {
        val BottomPadding = 130.dp

        val bottomNavItems: List<Screen>
            get() = listOf(
                Calculator,
                History,
                Analytics,
                Maintenance
            )
    }
}
