/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.maintenance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ru.nedumayy.shippingcalculator.presentation.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Обслуживание",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = Screen.BottomPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Экран обслуживания в разработке")
        }
    }
}
