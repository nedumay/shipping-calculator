/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.nedumayy.shippingcalculator.presentation.screen.Screen
import ru.nedumayy.shippingcalculator.presentation.screen.analytics.AnalyticsScreen
import ru.nedumayy.shippingcalculator.presentation.screen.history.HistoryScreen
import ru.nedumayy.shippingcalculator.presentation.screen.main.CalculatorScreen
import ru.nedumayy.shippingcalculator.presentation.screen.maintenance.MaintenanceScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Calculator.route) {
        composable(Screen.Calculator.route) {
            CalculatorScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(Screen.Maintenance.route) {
            MaintenanceScreen()
        }
    }
}
