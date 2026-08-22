/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.nedumayy.shippingcalculator.presentation.navigation.NavGraph
import ru.nedumayy.shippingcalculator.presentation.screen.Screen
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ShippingCalculatorTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ShippingCalculatorTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            NavGraph(navController = navController)
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.primary,
                            tonalElevation = 8.dp,
                            shadowElevation = 16.dp
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                tonalElevation = 0.dp
                            ) {
                                Screen.bottomNavItems.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { 
                                            Icon(
                                                imageVector = screen.icon, 
                                                contentDescription = stringResource(screen.titleRes) 
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                text = stringResource(screen.titleRes),
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        selected = selected,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                            indicatorColor = MaterialTheme.colorScheme.onPrimary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                                        ),
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
