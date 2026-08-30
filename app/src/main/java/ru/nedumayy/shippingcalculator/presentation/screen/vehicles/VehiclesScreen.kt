/**
 * Copyright © 2026 Nedumay.
 * All rights reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * @author https://github.com/nedumay
 */

package ru.nedumayy.shippingcalculator.presentation.screen.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.nedumayy.shippingcalculator.R
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceCategory
import ru.nedumayy.shippingcalculator.domain.model.MaintenanceRecord
import ru.nedumayy.shippingcalculator.domain.model.UserVehicle
import ru.nedumayy.shippingcalculator.presentation.screen.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddRecordDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_vehicles),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (selectedTabIndex == 1 && uiState.selectedVehicle != null) {
                FloatingActionButton(
                    onClick = { showAddRecordDialog = true },
                    modifier = Modifier.padding(bottom = Screen.BottomPadding)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_record))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.vehicles.isEmpty()) {
                EmptyVehiclesState()
            } else {
                VehicleSelector(
                    vehicles = uiState.vehicles,
                    selectedVehicle = uiState.selectedVehicle,
                    onVehicleSelected = { viewModel.selectVehicle(it) }
                )

                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(stringResource(R.string.vehicle_profile)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(R.string.maintenance_history)) }
                    )
                }

                when (selectedTabIndex) {
                    0 -> uiState.selectedVehicle?.let { vehicle ->
                        VehicleProfileTab(
                            vehicle = vehicle,
                            isSaving = uiState.isSaving,
                            onSave = { viewModel.updateProfile(it) }
                        )
                    }
                    1 -> {
                        Column {
                            SummaryCard(
                                totalRealCost = uiState.totalRealCost,
                                totalEstimatedCost = uiState.totalEstimatedCost,
                                costDifferencePerKm = uiState.costDifferencePerKm
                            )
                            RecordsList(
                                records = uiState.records,
                                onDelete = { viewModel.deleteRecord(it) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddRecordDialog) {
            AddMaintenanceDialog(
                onDismiss = { showAddRecordDialog = false },
                onConfirm = { mileage, desc, cost, category ->
                    viewModel.addRecord(mileage, desc, cost, category)
                    showAddRecordDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleProfileTab(
    vehicle: UserVehicle,
    isSaving: Boolean,
    onSave: (UserVehicle) -> Unit
) {
    var name by remember(vehicle) { mutableStateOf(vehicle.name) }
    var fuelType by remember(vehicle) { mutableStateOf(vehicle.fuelType) }
    var consumption by remember(vehicle) { mutableStateOf(vehicle.fuelConsumption.toString()) }
    var fuelPrice by remember(vehicle) { mutableStateOf(vehicle.fuelPrice.toString()) }
    var vehiclePrice by remember(vehicle) { mutableStateOf(vehicle.vehiclePrice.toString()) }
    var resource by remember(vehicle) { mutableStateOf(vehicle.resourceMileage.toString()) }
    var maintenance by remember(vehicle) { mutableStateOf(vehicle.maintenancePerKm.toString()) }
    var annualInsurance by remember(vehicle) { mutableStateOf(vehicle.annualInsurance.toString()) }
    var annualTax by remember(vehicle) { mutableStateOf(vehicle.annualTax.toString()) }
    var annualMileage by remember(vehicle) { mutableStateOf(vehicle.annualMileage.toString()) }

    val fuelTypes = listOf(
        stringResource(R.string.fuel_type_petrol),
        stringResource(R.string.fuel_type_diesel),
        stringResource(R.string.fuel_type_gas),
        stringResource(R.string.fuel_type_electricity)
    )
    var fuelTypeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TCO Header
        TcoCard(vehicle.totalCostPerKm)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.vehicle_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(
                expanded = fuelTypeExpanded,
                onExpandedChange = { fuelTypeExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = fuelType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.fuel_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelTypeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = fuelTypeExpanded,
                    onDismissRequest = { fuelTypeExpanded = false }
                ) {
                    fuelTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                fuelType = type
                                fuelTypeExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            val isElectric = fuelType == stringResource(R.string.fuel_type_electricity)
            OutlinedTextField(
                value = consumption,
                onValueChange = { consumption = it },
                label = { 
                    Text(
                        stringResource(
                            if (isElectric) R.string.fuel_consumption_electric 
                            else R.string.fuel_consumption
                        )
                    ) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        val isElectric = fuelType == stringResource(R.string.fuel_type_electricity)

        OutlinedTextField(
            value = fuelPrice,
            onValueChange = { fuelPrice = it },
            label = { 
                Text(
                    stringResource(
                        if (isElectric) R.string.fuel_price_electric_label 
                        else R.string.fuel_price_label
                    )
                ) 
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = vehiclePrice,
                onValueChange = { vehiclePrice = it },
                label = { Text(stringResource(R.string.vehicle_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = resource,
                onValueChange = { resource = it },
                label = { Text(stringResource(R.string.resource_mileage)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = maintenance,
            onValueChange = { maintenance = it },
            label = { Text(stringResource(R.string.maintenance_per_km)) },
            suffix = { Text("₽/км") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = annualTax,
                onValueChange = { annualTax = it },
                label = { Text(stringResource(R.string.annual_tax)) },
                suffix = { Text("₽/г") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = annualMileage,
                onValueChange = { annualMileage = it },
                label = { Text(stringResource(R.string.annual_mileage)) },
                suffix = { Text("км/г") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = annualInsurance,
            onValueChange = { annualInsurance = it },
            label = { Text(stringResource(R.string.annual_insurance)) },
            suffix = { Text("₽/г") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSave(
                    vehicle.copy(
                        name = name,
                        fuelType = fuelType,
                        fuelConsumption = consumption.toDoubleOrNull() ?: 0.0,
                        fuelPrice = fuelPrice.toDoubleOrNull() ?: 0.0,
                        vehiclePrice = vehiclePrice.toDoubleOrNull() ?: 0.0,
                        resourceMileage = resource.toDoubleOrNull() ?: 0.0,
                        maintenancePerKm = maintenance.toDoubleOrNull() ?: 0.0,
                        annualTax = annualTax.toDoubleOrNull() ?: 0.0,
                        annualInsurance = annualInsurance.toDoubleOrNull() ?: 0.0,
                        annualMileage = annualMileage.toDoubleOrNull() ?: 0.0
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.save_profile))
            }
        }
        
        Spacer(Modifier.height(Screen.BottomPadding))
    }
}

@Composable
fun TcoCard(totalCost: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.tco_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = String.format(Locale.getDefault(), "%.2f ₽/км", totalCost),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SummaryCard(totalRealCost: Double, totalEstimatedCost: Double, costDifferencePerKm: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.cost_analysis),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.total_costs), style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f ₽", totalRealCost),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.estimated_costs_by_mileage), style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f ₽", totalEstimatedCost),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val diff = costDifferencePerKm
            val color = if (diff <= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (diff <= 0) Icons.Default.DirectionsCar else Icons.Default.Warning,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (diff <= 0) 
                        stringResource(R.string.savings_per_km, Math.abs(diff))
                        else stringResource(R.string.overspending_per_km, diff),
                    color = color,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun VehicleSelector(
    vehicles: List<UserVehicle>,
    selectedVehicle: UserVehicle?,
    onVehicleSelected: (UserVehicle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = selectedVehicle?.name ?: stringResource(R.string.select_vehicle),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(text = "▼", fontSize = 12.sp)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            vehicles.forEach { vehicle ->
                DropdownMenuItem(
                    text = { Text(vehicle.name) },
                    onClick = {
                        onVehicleSelected(vehicle)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RecordsList(
    records: List<MaintenanceRecord>,
    onDelete: (MaintenanceRecord) -> Unit
) {
    Text(
        text = stringResource(R.string.maintenance_history),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
    
    if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(Screen.BottomPadding))
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = Screen.BottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records) { record ->
                RecordItem(record, onDelete)
            }
        }
    }
}

@Composable
fun RecordItem(record: MaintenanceRecord, onDelete: (MaintenanceRecord) -> Unit) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    ElevatedCard {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(record.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateFormat.format(record.date),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${record.mileage.toInt()} ${stringResource(R.string.km)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "%.2f ₽", record.cost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onDelete(record) }) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: MaintenanceCategory) {
    val (textRes, color) = when (category) {
        MaintenanceCategory.SERVICE -> R.string.category_service to Color(0xFF2196F3)
        MaintenanceCategory.REPAIR -> R.string.category_repair to Color(0xFFF44336)
        MaintenanceCategory.TIRES -> R.string.category_tires to Color(0xFF9C27B0)
        MaintenanceCategory.CONSUMABLES -> R.string.category_consumables to Color(0xFFFF9800)
        MaintenanceCategory.OTHER -> R.string.category_other to Color(0xFF9E9E9E)
    }
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyVehiclesState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.add_vehicle_first),
                modifier = Modifier.padding(32.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(Screen.BottomPadding))
        }
    }
}

@Composable
fun AddMaintenanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String, Double, MaintenanceCategory) -> Unit
) {
    var mileage by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MaintenanceCategory.SERVICE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_record_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text(stringResource(R.string.mileage_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text(stringResource(R.string.cost_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(stringResource(R.string.category_label), style = MaterialTheme.typography.labelMedium)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaintenanceCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        val categoryTextRes = when (category) {
                            MaintenanceCategory.SERVICE -> R.string.category_service
                            MaintenanceCategory.REPAIR -> R.string.category_repair
                            MaintenanceCategory.TIRES -> R.string.category_tires
                            MaintenanceCategory.CONSUMABLES -> R.string.category_consumables
                            MaintenanceCategory.OTHER -> R.string.category_other
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { 
                                Text(
                                    text = stringResource(categoryTextRes),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = mileage.toDoubleOrNull() ?: 0.0
                    val c = cost.toDoubleOrNull() ?: 0.0
                    if (description.isNotEmpty()) {
                        onConfirm(m, description, c, selectedCategory)
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
