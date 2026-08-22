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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.common.CalculatorEvent
import ru.nedumayy.shippingcalculator.common.formatDate
import ru.nedumayy.shippingcalculator.common.formatMoney
import ru.nedumayy.shippingcalculator.common.parseDate
import ru.nedumayy.shippingcalculator.domain.model.ui.CalculatorUiState
import ru.nedumayy.shippingcalculator.domain.model.CostBreakdown
import ru.nedumayy.shippingcalculator.presentation.screen.Screen
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            effect.showMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg.asString(context))
            }
        }
    }

    if (state.showSaveVehicleDialog) {
        SaveVehicleDialog(
            name = state.newVehicleName,
            onNameChange = { viewModel.onEvent(CalculatorEvent.NewVehicleNameChanged(it)) },
            onConfirm = { viewModel.onEvent(CalculatorEvent.SaveUserVehicle) },
            onDismiss = { viewModel.onEvent(CalculatorEvent.HideSaveVehicleDialog) }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = Screen.BottomPadding)
            ) 
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            SectionLabel(stringResource(R.string.transport_category))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
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

            if (state.userVehicles.isNotEmpty()) {
                SectionLabel(
                    text = stringResource(R.string.my_vehicles),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(CalculatorEvent.ShowSaveVehicleDialog) }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.save), fontSize = 12.sp)
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp, start = 16.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.userVehicles.forEach { vehicle ->
                        val active = vehicle.id == state.selectedUserVehicle?.id
                        Box(modifier = Modifier.padding(top = 4.dp, end = 4.dp)) {
                            CategoryCard(
                                label = vehicle.name,
                                active = active,
                                onClick = { viewModel.onEvent(CalculatorEvent.UserVehicleSelected(vehicle)) }
                            )
                            if (active) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                        .clickable { viewModel.onEvent(CalculatorEvent.DeleteUserVehicle(vehicle)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                TextButton(
                    onClick = { viewModel.onEvent(CalculatorEvent.ShowSaveVehicleDialog) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.save_current_vehicle))
                }
            }

            InputGroupCard(title = stringResource(R.string.route)) {
                OutlinedTextField(
                    value = state.routeFrom,
                    onValueChange = { viewModel.onEvent(CalculatorEvent.RouteFromChanged(it)) },
                    label = { Text(stringResource(R.string.where_from)) },
                    placeholder = { Text("Город А", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.routeTo,
                    onValueChange = { viewModel.onEvent(CalculatorEvent.RouteToChanged(it)) },
                    label = { Text(stringResource(R.string.where_to)) },
                    placeholder = { Text("Город Б", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                var showDatePicker by rememberSaveable { mutableStateOf(false) }
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
                    placeholder = "15",
                    suffix = stringResource(R.string.km)
                ) { viewModel.onEvent(CalculatorEvent.DistanceChanged(it)) }
            }

            AnimatedVisibility(visible = state.selectedCategory?.hasFuel != false) {
                InputGroupCard(title = stringResource(R.string.fuel)) {
                    val fuelTypes = listOf(
                        stringResource(R.string.fuel_type_petrol),
                        stringResource(R.string.fuel_type_diesel),
                        stringResource(R.string.fuel_type_gas),
                        stringResource(R.string.fuel_type_electricity)
                    )
                    var fuelTypeExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = fuelTypeExpanded,
                        onExpandedChange = { fuelTypeExpanded = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.fuelType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.fuel_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelTypeExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = fuelTypeExpanded,
                            onDismissRequest = { fuelTypeExpanded = false }
                        ) {
                            fuelTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.onEvent(CalculatorEvent.FuelTypeChanged(type))
                                        fuelTypeExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NumberField(
                            label = stringResource(R.string.fuel_consumption),
                            value = state.fuelConsumption,
                            placeholder = state.selectedCategory?.fuelConsumption?.toString() ?: "0",
                            suffix = "л/100",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(CalculatorEvent.FuelConsumptionChanged(it)) }
                        NumberField(
                            label = stringResource(R.string.price_per_liter),
                            value = state.fuelPrice,
                            placeholder = "58",
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
                        placeholder = state.selectedCategory?.vehiclePrice?.toLong()?.toString() ?: "0",
                        suffix = "₽",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.VehiclePriceChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.resource_mileage),
                        value = state.resourceMileage,
                        placeholder = state.selectedCategory?.resourceMileage?.toLong()?.toString() ?: "0",
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
                        placeholder = state.selectedCategory?.annualTax?.toLong()?.toString() ?: "0",
                        suffix = "₽/г",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.AnnualTaxChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.annual_mileage),
                        value = state.annualMileage,
                        placeholder = state.selectedCategory?.annualMileage?.toLong()?.toString() ?: "30000",
                        suffix = "км/г",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.AnnualMileageChanged(it)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        label = stringResource(R.string.maintenance_per_km),
                        value = state.maintenancePerKm,
                        placeholder = state.selectedCategory?.maintenancePerKm?.toString() ?: "0",
                        suffix = "₽/км",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.MaintenancePerKmChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.annual_insurance),
                        value = state.annualInsurance,
                        placeholder = state.selectedCategory?.annualInsurance?.toLong()?.toString() ?: "0",
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
                        placeholder = "150",
                        suffix = "₽",
                        modifier = Modifier.weight(1f)
                    ) { viewModel.onEvent(CalculatorEvent.ExtraExpensesChanged(it)) }
                    NumberField(
                        label = stringResource(R.string.margin),
                        value = state.marginPercent,
                        placeholder = "20",
                        suffix = "%",
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Done
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

            Spacer(Modifier.height(Screen.BottomPadding))
        }
    }
}

@Composable
private fun SectionLabel(text: String, action: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        action?.invoke()
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
        targetValue = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, label = ""
    )
    val contentColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface, label = ""
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
    placeholder: String? = null,
    suffix: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.count { it == '.' || it == ',' } <= 1) {
                onChange(newValue)
            }
        },
        label = { Text(label, fontSize = 11.sp) },
        placeholder = placeholder?.let { { Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.outline) } },
        enabled = enabled,
        suffix = suffix?.let { { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        ),
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
                text = stringResource(R.string.delivery_receipt),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(16.dp))
            
            ResultRow(stringResource(R.string.fuel), breakdown.fuelTotal)
            ResultRow(stringResource(R.string.depreciation), breakdown.depreciationTotal)
            ResultRow(stringResource(R.string.tax), breakdown.taxTotal)
            ResultRow(stringResource(R.string.maintenance_and_repair), breakdown.maintenanceTotal)
            ResultRow(stringResource(R.string.insurance), breakdown.insuranceTotal)
            ResultRow(stringResource(R.string.extra_costs), breakdown.extraTotal)
            ResultRow("${stringResource(R.string.margin_label)} (${state.marginPercent}%)", breakdown.marginAmount)
            
            HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(stringResource(R.string.price_per_km), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatMoney(breakdown.finalPerKm) + " ₽ / " + stringResource(R.string.km), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.total), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${formatMoney(breakdown.finalTotal)} ₽", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${formatMoney(value)} ₽", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SaveVehicleDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.save_current_vehicle)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_vehicle_name))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.vehicle_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = parseDate(initialDate)?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                    onDateSelected(formatDate(date))
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
