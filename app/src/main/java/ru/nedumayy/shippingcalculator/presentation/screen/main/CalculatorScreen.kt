/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
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
import ru.nedumayy.shippingcalculator.common.formatMoney
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorAmberDeep
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorBg
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorInk
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorSurface
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorTeal
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorRust
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorSteel
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorMauve
import ru.nedumayy.shippingcalculator.presentation.ui.theme.ColorTextMute
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            effect.showMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBg)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.cost_calculation),
                color = ColorAmberDeep,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
            Text(
                stringResource(R.string.app_name),
                color = ColorInk,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                stringResource(R.string.transport_category),
                color = ColorTextMute,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { cat ->
                    val active = cat.id == state.selectedCategory?.id
                    Surface(
                        onClick = { viewModel.onEvent(CalculatorEvent.CategorySelected(cat)) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (active) Color(0xFFFBF2DF) else ColorSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (active) ColorAmberDeep else Color(0xFFD8D3C4)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
//                            Icon(
//                                painter = painterResource(id = when (cat.id) {
//                                    "moped" -> R.drawable.moped // или другая подходящая
//                                    "car" -> R.drawable.moped
//                                    "truck" -> R.drawable.shuttle
//                                    else -> R.drawable.shuttle
//                                }),
//                                contentDescription = cat.label.asString(),
//                                tint = if (active) ColorAmberDeep else ColorTextMute,
//                                modifier = Modifier.size(24.dp)
//                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                cat.label.asString(),
                                fontSize = 11.sp,
                                color = if (active) ColorInk else ColorTextMute
                            )
                        }
                    }
                }
            }

            SectionLabel(stringResource(R.string.route))
            OutlinedTextField(
                value = state.routeFrom,
                onValueChange = { viewModel.onEvent(CalculatorEvent.RouteFromChanged(it)) },
                label = { Text(stringResource(R.string.where_from), fontSize = 12.sp) },
                //placeholder = { Text("Например: Москва") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAmberDeep,
                    focusedLabelColor = ColorAmberDeep
                )
            )
            OutlinedTextField(
                value = state.routeTo,
                onValueChange = { viewModel.onEvent(CalculatorEvent.RouteToChanged(it)) },
                label = { Text(stringResource(R.string.where_to), fontSize = 12.sp) },
                //placeholder = { Text("Например: Санкт-Петербург") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAmberDeep,
                    focusedLabelColor = ColorAmberDeep
                )
            )

            // Дата
            var showDatePicker by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = state.deliveryDate,
                onValueChange = {},
                label = { Text(stringResource(R.string.delivery_date), fontSize = 12.sp) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAmberDeep,
                    focusedLabelColor = ColorAmberDeep
                )
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = try {
                        LocalDate.parse(state.deliveryDate).toEpochDay() * 86400000L
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = LocalDate.ofEpochDay(millis / 86400000L)
                                viewModel.onEvent(CalculatorEvent.DeliveryDateChanged(date.toString()))
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            NumberField(stringResource(R.string.distance), state.distance) {
                viewModel.onEvent(CalculatorEvent.DistanceChanged(it))
            }

            SectionLabel(stringResource(R.string.fuel))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    stringResource(R.string.fuel_consumption),
                    state.fuelConsumption,
                    enabled = state.selectedCategory?.hasFuel == true,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.FuelConsumptionChanged(it))
                }
                NumberField(
                    stringResource(R.string.price_per_liter),
                    state.fuelPrice,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.FuelPriceChanged(it))
                }
            }
            
            SectionLabel(stringResource(R.string.depreciation))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    stringResource(R.string.vehicle_price),
                    state.vehiclePrice,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.VehiclePriceChanged(it))
                }
                NumberField(
                    stringResource(R.string.resource_mileage),
                    state.resourceMileage,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.ResourceMileageChanged(it))
                }
            }

            // Налог
            SectionLabel(stringResource(R.string.taxes_and_other))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    stringResource(R.string.annual_tax),
                    state.annualTax,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.AnnualTaxChanged(it))
                }
                NumberField(
                    stringResource(R.string.annual_mileage),
                    state.annualMileage,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.AnnualMileageChanged(it))
                }
            }

            // ТО и страховка
            SectionLabel(stringResource(R.string.maintenance_and_insurance))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    stringResource(R.string.maintenance_per_km),
                    state.maintenancePerKm,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.MaintenancePerKmChanged(it))
                }
                NumberField(
                    stringResource(R.string.annual_insurance),
                    state.annualInsurance,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.AnnualInsuranceChanged(it))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    stringResource(R.string.extra_expenses),
                    state.extraExpenses,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.ExtraExpensesChanged(it))
                }
                NumberField(
                    stringResource(R.string.margin),
                    state.marginPercent,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.onEvent(CalculatorEvent.MarginChanged(it))
                }
            }

            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.onEvent(CalculatorEvent.SaveToHistory) },
                enabled = state.canSave && !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.canSave) ColorAmberDeep else ColorTextMute,
                    disabledContainerColor = ColorTextMute.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    if (state.isSaving) stringResource(R.string.saving) else stringResource(R.string.save_calculation),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))
            
            state.costBreakdown?.let { breakdown ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ColorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            stringResource(R.string.delivery_receipt),
                            fontSize = 11.sp,
                            color = ColorTextMute,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            "${state.selectedCategory?.label?.asString()} · ${state.distance.ifBlank { "0" }} ${stringResource(R.string.km)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorInk,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 12.dp)
                        )
                        HorizontalDivider(color = Color(0xFFD8D3C4))
                        Spacer(Modifier.height(10.dp))

                        ReceiptRow(stringResource(R.string.fuel), breakdown.fuelTotal)
                        ReceiptRow(stringResource(R.string.depreciation), breakdown.depreciationTotal)
                        ReceiptRow(stringResource(R.string.tax), breakdown.taxTotal)
                        ReceiptRow(stringResource(R.string.maintenance_and_repair), breakdown.maintenanceTotal)
                        ReceiptRow(stringResource(R.string.insurance), breakdown.insuranceTotal)
                        ReceiptRow(stringResource(R.string.extra_costs), breakdown.extraTotal)
                        ReceiptRow(stringResource(R.string.margin_label), breakdown.marginAmount, ColorTeal)

                        HorizontalDivider(
                            color = Color(0xFFD8D3C4),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.price_per_km), fontSize = 12.sp, color = ColorTextMute)
                            Text(
                                "${formatMoney(breakdown.finalPerKm)} ₽",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorInk
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.total),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorInk
                            )
                            Text(
                                "${formatMoney(breakdown.finalTotal)} ₽",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorAmberDeep
                            )
                        }
                    }
                }

                // Структура стоимости
                Spacer(Modifier.height(20.dp))
                Text(
                    stringResource(R.string.cost_structure),
                    fontSize = 11.sp,
                    color = ColorTextMute,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val segments = listOf(
                    stringResource(R.string.fuel) to (breakdown.fuelTotal to ColorAmberDeep),
                    stringResource(R.string.depreciation) to (breakdown.depreciationTotal to ColorInk),
                    stringResource(R.string.tax) to (breakdown.taxTotal to ColorRust),
                    stringResource(R.string.maintenance_and_repair) to (breakdown.maintenanceTotal to ColorSteel),
                    stringResource(R.string.insurance) to (breakdown.insuranceTotal to ColorMauve),
                    stringResource(R.string.extra_costs) to (breakdown.extraTotal to ColorTextMute),
                    stringResource(R.string.margin_label) to (breakdown.marginAmount to ColorTeal)
                ).filter { it.second.first > 0 }
                val total = segments.sumOf { it.second.first }.let { if (it == 0.0) 1.0 else it }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    segments.forEach { (_, pair) ->
                        val (value, color) = pair
                        Box(
                            Modifier
                                .weight(((value / total).toFloat()).coerceAtLeast(0.001f))
                                .fillMaxHeight()
                                .background(color)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Legend(segments, total)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        color = ColorTextMute,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 12.sp) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAmberDeep,
            focusedLabelColor = ColorAmberDeep
        )
    )
}

@Composable
private fun ReceiptRow(label: String, value: Double, accentColor: Color = ColorInk) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = ColorInk)
        Text(
            "${formatMoney(value)} ₽",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = accentColor
        )
    }
}

@Composable
private fun Legend(segments: List<Pair<String, Pair<Double, Color>>>, total: Double) {
    val chunked = segments.chunked(2)
    Column {
        chunked.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                row.forEach { (name, pair) ->
                    val (value, color) = pair
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier
                            .size(8.dp)
                            .background(color, RoundedCornerShape(2.dp)))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "$name · ${((value / total) * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = ColorTextMute
                        )
                    }
                }
            }
        }
    }
}