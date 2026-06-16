package com.kalbim.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.kalbim.R
import com.kalbim.pdf.PdfGenerator
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(vm: KalbimViewModel) {
    val context      = LocalContext.current
    val profile      by vm.profile.collectAsState()
    val allMeasurements by vm.allMeasurements.collectAsState()
    val medications  by vm.allMedications.collectAsState()

    var isGenerating by remember { mutableStateOf(false) }
    var lastFileName by remember { mutableStateOf("") }

    // Tarih aralığı state'leri
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker   by remember { mutableStateOf(false) }

    // Varsayılan: son 30 gün
    val defaultFrom = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -30)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val defaultTo = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }

    val fromPickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultFrom.timeInMillis
    )
    val toPickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultTo.timeInMillis
    )

    val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val fromDate = fromPickerState.selectedDateMillis?.let { fmt.format(Date(it)) }
        ?: fmt.format(defaultFrom.time)
    val toDate = toPickerState.selectedDateMillis?.let { fmt.format(Date(it)) }
        ?: fmt.format(defaultTo.time)

    // Seçilen aralıktaki ölçümleri filtrele
    val fromMillis = fromPickerState.selectedDateMillis
        ?: defaultFrom.timeInMillis
    val toMillis = (toPickerState.selectedDateMillis ?: defaultTo.timeInMillis).let {
        // Bitiş gününün sonunu al (23:59:59)
        Calendar.getInstance().apply {
            timeInMillis = it
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis
    }

    val filteredMeasurements = allMeasurements.filter {
        it.timestamp in fromMillis..toMillis
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
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
                    stringResource(R.string.report_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.report_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Tarih Aralığı Seçimi ────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.report_date_from_to),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Başlangıç tarihi
                        OutlinedButton(
                            onClick  = { showFromPicker = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(R.string.report_date_begin),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    fromDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }
                        }

                        Icon(
                            Icons.Filled.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        // Bitiş tarihi
                        OutlinedButton(
                            onClick  = { showToPicker = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    stringResource(R.string.report_date_end),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    toDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }
                        }
                    }

                    // Hızlı seçim butonları
                    Spacer(Modifier.height(10.dp))
                    Text(
                        stringResource(R.string.report_short_cut),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            stringResource(R.string.report_date_last7)  to 7,
                            stringResource(R.string.report_date_last30) to 30,
                            stringResource(R.string.report_date_last90) to 90
                        ).forEach { (label, days) ->
                            FilterChip(
                                selected = false,
                                onClick  = {
                                    val from = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_YEAR, -days)
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                    }
                                    fromPickerState.selectedDateMillis = from.timeInMillis
                                    toPickerState.selectedDateMillis =
                                        Calendar.getInstance().timeInMillis
                                },
                                label = { Text(label,
                                    style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor     = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // ── Özet Bilgi Kartı ────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.report_box_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(10.dp))

                    ReportInfoRow(
                        stringResource(R.string.report_box_person),
                        "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}"
                    )
                    ReportInfoRow(
                        stringResource(R.string.report_select),
                        "$fromDate — $toDate"
                    )
                    ReportInfoRow(
                        stringResource(R.string.report_date_from_to),
                        "${filteredMeasurements.size} " + stringResource(R.string.report_box_measurement_text) +
                                " (${allMeasurements.size})"
                    )
                    ReportInfoRow(
                        stringResource(R.string.report_box_medical_history),
                        if (profile?.medicalHistory.isNullOrBlank())
                            stringResource(R.string.report_box_medical_history_text) else stringResource(R.string.report_box_medical_history_text2)
                    )
                    ReportInfoRow(
                        stringResource(R.string.report_box_medicinelist),
                        if (medications.isEmpty()) stringResource(R.string.report_box_medicinelist_null)
                        else "${medications.size} " + stringResource(R.string.report_box_medicinelist_text)  + " " +
                                "(${medications.count { it.isActive }} "+ stringResource(R.string.report_box_medicinelist_text2) + ")"
                    )
                }
            }

            // Uyarı — filtrelenmiş veri yoksa
            if (filteredMeasurements.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                            .copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️", fontSize = 16.sp)
                        Text(
                            stringResource(R.string.report_isempty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── PDF Oluştur Butonu ──────────────────────────
            Button(
                onClick = {
                    isGenerating = true
                    val file = PdfGenerator.generate(
                        context      = context,
                        profile      = profile ?: return@Button,
                        measurements = filteredMeasurements,
                        medications  = medications,
                        fromDate     = fromDate,
                        toDate       = toDate
                    )
                    isGenerating = false
                    if (file != null) {
                        lastFileName = file.name
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, context.getString(R.string.report_pdf_share)))
                        }
                    } else {
                        Toast.makeText(context,
                            context.getString(R.string.report_pdf_exep), Toast.LENGTH_SHORT).show()
                    }
                },
                enabled  = !isGenerating && filteredMeasurements.isNotEmpty()
                        && profile != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color    = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.PictureAsPdf, null,
                        tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.report_pdf_creat),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }

            if (lastFileName.isNotBlank()) {
                Text(
                    "✅ $lastFileName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Yasal uyarı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                        .copy(alpha = 0.5f))
            ) {
                Text(
                    stringResource(R.string.report_pdf_info),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // ── DatePicker Dialog'ları ──────────────────────────────
    if (showFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = { showFromPicker = false }) {
                    Text(stringResource(R.string.c_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFromPicker = false }) {
                    Text(stringResource(R.string.c_cencel))
                }
            }
        ) {
            DatePicker(
                state = fromPickerState,
                showModeToggle = true
            )
        }
    }

    if (showToPicker) {
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = { showToPicker = false }) {
                    Text(stringResource(R.string.c_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showToPicker = false }) {
                    Text(stringResource(R.string.c_cencel))
                }
            }
        ) {
            DatePicker(
                state = toPickerState,
                showModeToggle = true
            )
        }
    }
}

@Composable
fun ReportInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    )
}