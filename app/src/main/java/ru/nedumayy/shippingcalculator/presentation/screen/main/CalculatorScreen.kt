/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.main

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.common.CalculatorEvent
import ru.nedumayy.shippingcalculator.common.formatDate
import ru.nedumayy.shippingcalculator.common.formatMoney
import ru.nedumayy.shippingcalculator.common.parseDate
import ru.nedumayy.shippingcalculator.domain.model.CalculatorUiState
import ru.nedumayy.shippingcalculator.domain.model.CostBreakdown
import ru.nedumayy.shippingcalculator.presentation.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            effect.showMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {

            SectionLabel(
                stringResource(R.string.transport_category)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { cat ->
                    val active = cat.id == state.selectedCategory?.id
                    CategoryCard(
                        label = cat.label.asString(),
                        active = active,
                        onClick = { viewModel.onEvent(CalculatorEvent.CategorySelected(cat)) }
                    )
                }
            }

            InputGroupCard(title = stringResource(R.string.route)) {
                OutlinedTextField(
                    value = state.routeFrom,
                    onValueChange = { viewModel.onEvent(CalculatorEvent.RouteFromChanged(it)) },
                    label = { Text(stringResource(R.string.where_from)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.routeTo,
                    onValueChange = { viewModel.onEvent(CalculatorEvent.RouteToChanged(it)) },
                    label = { Text(stringResource(R.string.where_to)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                var showDatePicker by remember { mutableStateOf(false) }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = state.deliveryDate,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.delivery_date)) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (showDatePicker) {
                    DatePickerDialogComponent(
                        initialDate = state.deliveryDate,
                        onDateSelected = { viewModel.onEvent(CalculatorEvent.DeliveryDateChanged(it)) },
                        onDismiss = { showDatePicker = false }
                    )
                }

                NumberField(
                    label = stringResource(R.string.distance),
                    value = state.distance,
                    suffix = stringResource(R.string.km)
                ) { viewModel.onEvent(CalculatorEvent.DistanceChanged(it)) }
            }

            AnimatedVisibility(visible = state.selectedCategory?.hasFuel == true) {
                InputGroupCard(title = stringResource(R.string.fuel)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NumberField(
                            label = stringResource(R.string.fuel_consumption),
                            value = state.fuelConsumption,
                            suffix = "л/100",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(CalculatorEvent.FuelConsumptionChanged(it)) }
                        NumberField(
                            label = stringResource(R.string.price_per_liter),
                            value = state.fuelPrice,
                            suffix = "₽",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(CalculatorEvent.FuelPriceChanged(it)) }
                    }
                }
            }

            InputGroupCard(title = stringResource(R.string.depreciation)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        label = stringResource(R.string.vehicle_price),
                        value = state.vehiclePrice,
                        suffix = "₽",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.VehiclePriceChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.resource_mileage),
                        value = state.resourceMileage,
                        suffix = "км",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.ResourceMileageChanged(it)) }
                }
            }

            InputGroupCard(title = stringResource(R.string.maintenance_and_insurance)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        label = stringResource(R.string.annual_tax),
                        value = state.annualTax,
                        suffix = "₽/г",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.AnnualTaxChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.annual_mileage),
                        value = state.annualMileage,
                        suffix = "км/г",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.AnnualMileageChanged(it)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        label = stringResource(R.string.maintenance_per_km),
                        value = state.maintenancePerKm,
                        suffix = "₽/км",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.MaintenancePerKmChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.annual_insurance),
                        value = state.annualInsurance,
                        suffix = "₽/г",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.AnnualInsuranceChanged(it)) }
                }
            }

            InputGroupCard(title = stringResource(R.string.taxes_and_other)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        label = stringResource(R.string.extra_expenses),
                        value = state.extraExpenses,
                        suffix = "₽",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.ExtraExpensesChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.margin),
                        value = state.marginPercent,
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.MarginChanged(it)) }
                }
            }

            Spacer(Modifier.height(24.dp))

            ElevatedButton(
                onClick = { viewModel.onEvent(CalculatorEvent.SaveToHistory) },
                enabled = state.canSave && !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        stringResource(R.string.save_calculation), 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(
                visible = state.costBreakdown != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                state.costBreakdown?.let { breakdown ->
                    Column {
                        Spacer(Modifier.height(24.dp))
                        ResultCard(breakdown, state)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InputGroupCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title, 
                style = MaterialTheme.typography.labelLarge, 
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(label: String, active: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    )
    val contentColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(min = 80.dp)
        ) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    suffix: String? = null,
    onChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 11.sp) },
        enabled = enabled,
        suffix = suffix?.let { { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun ResultCard(
    breakdown: CostBreakdown,
    state: CalculatorUiState
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.delivery_receipt),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                "${state.selectedCategory?.label?.asString()} · ${state.distance} ${stringResource(R.string.km)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(10.dp))

            ReceiptRow(stringResource(R.string.fuel), breakdown.fuelTotal)
            ReceiptRow(stringResource(R.string.depreciation), breakdown.depreciationTotal)
            ReceiptRow(stringResource(R.string.tax), breakdown.taxTotal)
            ReceiptRow(stringResource(R.string.maintenance_and_repair), breakdown.maintenanceTotal)
            ReceiptRow(stringResource(R.string.insurance), breakdown.insuranceTotal)
            ReceiptRow(stringResource(R.string.extra_costs), breakdown.extraTotal)
            ReceiptRow(stringResource(R.string.margin_label), breakdown.marginAmount, MaterialTheme.colorScheme.tertiary)

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))

            CostStructureBar(breakdown)
            
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.price_per_km), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${formatMoney(breakdown.finalPerKm)} ₽", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.total), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${formatMoney(breakdown.finalTotal)} ₽",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: Double, accentColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        Text("${formatMoney(value)} ₽", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CostStructureBar(breakdown: CostBreakdown) {
    val upkeepTotal = breakdown.taxTotal + breakdown.maintenanceTotal + breakdown.insuranceTotal
    
    val segments = listOf(
        Triple(breakdown.fuelTotal, MaterialTheme.colorScheme.primary, stringResource(R.string.fuel)),
        Triple(breakdown.depreciationTotal, MaterialTheme.colorScheme.secondary, stringResource(R.string.depreciation)),
        Triple(upkeepTotal, MaterialTheme.colorScheme.error, stringResource(R.string.maintenance_and_insurance)),
        Triple(breakdown.extraTotal, MaterialTheme.colorScheme.outline, stringResource(R.string.extra_costs)),
        Triple(breakdown.marginAmount, MaterialTheme.colorScheme.tertiary, stringResource(R.string.margin_label))
    ).filter { it.first > 0 }

    val total = segments.sumOf { it.first }.coerceAtLeast(1.0)

    Column {
        Row(Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))) {
            segments.forEach { (value, color, _) ->
                Box(Modifier
                    .weight((value / total).toFloat().coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(color))
            }
        }
        Spacer(Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            segments.forEach { (value, color, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier
                        .size(8.dp)
                        .background(color, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$label ${(value/total*100).toInt()}%",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogComponent(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            parseDate(initialDate).toEpochDay() * 86400000L
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = LocalDate.ofEpochDay(millis / 86400000L)
                    onDateSelected(formatDate(date))
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    )
}
