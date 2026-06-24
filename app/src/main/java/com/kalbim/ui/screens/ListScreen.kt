package com.kalbim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kalbim.data.model.Measurement
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.stringResource
import com.kalbim.R

@Composable
fun ListScreen(vm: KalbimViewModel) {
    val measurements by vm.allMeasurements.collectAsState()
    var deleteTarget by remember { mutableStateOf<Measurement?>(null) }
    var editTarget   by remember { mutableStateOf<Measurement?>(null) }
    var searchQuery  by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) measurements
    else measurements.filter { it.dateLabel.contains(searchQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NavyPrimary, NavyLight)))
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    stringResource(R.string.list_title2),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${measurements.size}" + " " + stringResource(R.string.list_sub_title),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.list_date_filter)) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary
                )
            )

            Spacer(Modifier.height(16.dp))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (searchQuery.isBlank()) stringResource(R.string.no_data)
                            else stringResource(R.string.list_not_found),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isBlank()) {
                            Text(
                                stringResource(R.string.list_new_add),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                    .copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered, key = { it.id }) { m ->
                        MeasurementListCard(
                            measurement = m,
                            onEdit      = { editTarget = m },
                            onDelete    = { deleteTarget = m }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }

    // Düzenleme dialog
    editTarget?.let { m ->
        EditMeasurementDialog(
            measurement = m,
            onDismiss   = { editTarget = null },
            onSave      = { updated ->
                vm.updateMeasurement(updated)
                editTarget = null
            }
        )
    }

    // Silme onay dialog
    deleteTarget?.let { m ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, null,
                        tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.delete_title),
                        fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("${m.dateLabel}" + " " +
                        stringResource(R.string.delete_title_desc))
            },
            confirmButton = {
                Button(
                    onClick = { vm.deleteMeasurement(m); deleteTarget = null },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) { Text(stringResource(R.string.delete), color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deleteTarget = null },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun MeasurementListCard(
    measurement: Measurement,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Tarih + Butonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        measurement.dateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Düzenle butonu
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Edit, null,
                        modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.edit),
                        style = MaterialTheme.typography.labelSmall)
                }

                Spacer(Modifier.width(6.dp))

                // Sil butonu
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Delete, null,
                        modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(10.dp))

            // Değerler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (measurement.systolic != null) {
                    MetricChip(
                        modifier = Modifier.weight(1f),
                        icon  = "🩺",
                        label = stringResource(R.string.home_pressure),
                        value = "${measurement.systolic}/${measurement.diastolic}",
                        unit  = "mmHg",
                        color = ChartRed
                    )
                }
                if (measurement.pulse != null) {
                    MetricChip(
                        modifier = Modifier.weight(1f),
                        icon  = "💓",
                        label = stringResource(R.string.pulse),
                        value = "${measurement.pulse}",
                        unit  = "bpm",
                        color = AccentTeal
                    )
                }
                if (measurement.weightKg != null) {
                    MetricChip(
                        modifier = Modifier.weight(1f),
                        icon  = "⚖️",
                        label = stringResource(R.string.weight),
                        value = "${"%.1f".format(measurement.weightKg)}",
                        unit  = "kg",
                        color = ChartGreen
                    )
                }
            }

            if (measurement.ntProBnp != null || measurement.troponin != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (measurement.ntProBnp != null) {
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            icon  = "🧪",
                            label = "NT-proBNP",
                            value = "${"%.0f".format(measurement.ntProBnp)}",
                            unit  = "pg/mL",
                            color = ChartOrange
                        )
                    }
                    if (measurement.troponin != null) {
                        MetricChip(
                            modifier = Modifier.weight(1f),
                            icon  = "🔬",
                            label = "Troponin",
                            value = "${"%.2f".format(measurement.troponin)}",
                            unit  = "ng/L",
                            color = ChartPurple
                        )
                    }
                }
            }

            if (measurement.notes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Text("📝 ", fontSize = 13.sp)
                    Text(
                        measurement.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EditMeasurementDialog(
    measurement: Measurement,
    onDismiss: () -> Unit,
    onSave: (Measurement) -> Unit
) {
    var sys    by remember { mutableStateOf(measurement.systolic?.toString()  ?: "") }
    var dia    by remember { mutableStateOf(measurement.diastolic?.toString() ?: "") }
    var pulse  by remember { mutableStateOf(measurement.pulse?.toString()     ?: "") }
    var weight by remember { mutableStateOf(measurement.weightKg?.toString()  ?: "") }
    var bnp    by remember { mutableStateOf(measurement.ntProBnp?.toString()  ?: "") }
    var trop   by remember { mutableStateOf(measurement.troponin?.toString()  ?: "") }
    var notes  by remember { mutableStateOf(measurement.notes) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Edit, null,
                        tint = NavyPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.edit_measurement),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    measurement.dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                if (measurement.systolic != null) {
                    Text("🩺 ${stringResource(R.string.home_pressure)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KalbimTextField(
                            value = sys, onValueChange = { sys = it },
                            label = stringResource(R.string.systolic),
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary)
                        )
                        KalbimTextField(
                            value = dia, onValueChange = { dia = it },
                            label = stringResource(R.string.diastolic),
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary)
                        )
                    }
                    KalbimTextField(
                        value = pulse, onValueChange = { pulse = it },
                        label = "${stringResource(R.string.pulse)} (bpm)",
                        keyboardType = KeyboardType.Number,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary)
                    )
                }

                if (measurement.weightKg != null) {
                    Text("⚖️ ${stringResource(R.string.weight)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                    KalbimTextField(
                        value = weight, onValueChange = { weight = it },
                        label = "${stringResource(R.string.weight)} (kg)",
                        keyboardType = KeyboardType.Decimal,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary)
                    )
                }

                if (measurement.ntProBnp != null || measurement.troponin != null) {
                    Text("🧪 ${stringResource(R.string.nt_pro_bnp)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                    if (measurement.ntProBnp != null)
                        KalbimTextField(
                            value = bnp, onValueChange = { bnp = it },
                            label = "${stringResource(R.string.nt_pro_bnp)} (pg/mL)",
                            keyboardType = KeyboardType.Decimal,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary)
                        )
                    if (measurement.troponin != null)
                        KalbimTextField(
                            value = trop, onValueChange = { trop = it },
                            label = "${stringResource(R.string.troponin)} (ng/L)",
                            keyboardType = KeyboardType.Decimal,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary)
                        )
                }

                OutlinedTextField(
                    value         = notes,
                    onValueChange = { notes = it },
                    label         = { Text(stringResource(R.string.notes)) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    maxLines      = 3,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary)
                )

                HorizontalDivider()

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick  = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text(stringResource(R.string.cancel)) }

                    Button(
                        onClick = {
                            onSave(
                                measurement.copy(
                                    systolic  = sys.toIntOrNull()      ?: measurement.systolic,
                                    diastolic = dia.toIntOrNull()      ?: measurement.diastolic,
                                    pulse     = pulse.toIntOrNull()    ?: measurement.pulse,
                                    weightKg  = weight.toFloatOrNull() ?: measurement.weightKg,
                                    ntProBnp  = bnp.toFloatOrNull()    ?: measurement.ntProBnp,
                                    troponin  = trop.toFloatOrNull()   ?: measurement.troponin,
                                    notes     = notes.trim()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary)
                    ) { Text(stringResource(R.string.save), color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun MetricChip(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(icon, fontSize = 14.sp)
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}