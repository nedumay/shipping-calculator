/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.nedumayy.shippingcalculator.domain.model.AnalyticsData
import ru.nedumayy.shippingcalculator.domain.usecase.GetAnalyticsUseCase
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {

    val state: StateFlow<AnalyticsData> = getAnalyticsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsData()
        )
}
