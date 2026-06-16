package com.kalbim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var searchQuery  by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) measurements
    else measurements.filter { it.dateLabel.contains(searchQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ─────────────────────────────────────────
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

            // Arama kutusu
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered, key = { it.id }) { m ->
                        MeasurementListCard(
                            measurement = m,
                            onDelete    = { deleteTarget = m }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
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
                    Text(stringResource(R.string.delete_title), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("${m.dateLabel}" + " " + stringResource(R.string.delete_title_desc))
            },
            confirmButton = {
                Button(
                    onClick = { vm.deleteMeasurement(m); deleteTarget = null },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
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
fun MeasurementListCard(measurement: Measurement, onDelete: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Tarih + Sil butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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