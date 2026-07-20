/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.nedumayy.shippingcalculator.presentation.screen.Screen
import ru.nedumayy.shippingcalculator.presentation.screen.main.CalculatorScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Calculator.route) {
        composable(Screen.Calculator.route) {
            CalculatorScreen(
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        // composable(Screen.History.route) { HistoryScreen() } // добавим позже
    }
}