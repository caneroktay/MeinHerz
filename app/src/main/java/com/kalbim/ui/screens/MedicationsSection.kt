package com.kalbim.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kalbim.data.model.Medication
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.stringResource
import com.kalbim.R
@Composable
fun MedicationsSection(vm: KalbimViewModel) {
    val medications by vm.allMedications.collectAsState()
    var showAddDialog    by remember { mutableStateOf(false) }
    var editTarget       by remember { mutableStateOf<Medication?>(null) }
    var deleteTarget     by remember { mutableStateOf<Medication?>(null) }
    var deactivateTarget by remember { mutableStateOf<Medication?>(null) }

    ProfileSectionCard(title = stringResource(R.string.medi_title)) {
        if (medications.isEmpty()) {
            Text(
                stringResource(R.string.medi_title_null),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                medications.forEach { med ->
                    MedicationCard(
                        medication   = med,
                        onEdit       = { editTarget = med },
                        onDelete     = { deleteTarget = med },
                        onDeactivate = { deactivateTarget = med }
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick  = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Icon(Icons.Filled.Add, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.medi_add), color = Color.White)
        }
    }

    if (showAddDialog) {
        MedicationDialog(
            medication = null,
            onDismiss  = { showAddDialog = false },
            onSave     = { vm.insertMedication(it); showAddDialog = false }
        )
    }

    editTarget?.let { med ->
        MedicationDialog(
            medication = med,
            onDismiss  = { editTarget = null },
            onSave     = { vm.updateMedication(it); editTarget = null }
        )
    }

    deleteTarget?.let { med ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text(stringResource(R.string.medi_del), fontWeight = FontWeight.Bold) },
            text  = { Text("\"${med.name}\" " + stringResource(R.string.medi_del_message)) },
            confirmButton = {
                Button(
                    onClick = { vm.deleteMedication(med); deleteTarget = null },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) { Text(stringResource(R.string.delete), color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deleteTarget = null },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text(stringResource(R.string.medi_cencel)) }
            }
        )
    }

    deactivateTarget?.let { med ->
        var note by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { deactivateTarget = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text(stringResource(R.string.medi_edit), fontWeight = FontWeight.Bold) },
            text  = {
                Column {
                    Text("\"${med.name}\"" + stringResource(R.string.medi_status_massege))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = note,
                        onValueChange = { note = it },
                        label         = { Text(stringResource(R.string.medi_desc)) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { vm.deactivateMedication(med, note); deactivateTarget = null },
                    shape   = RoundedCornerShape(10.dp),
                    colors  = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) { Text(stringResource(R.string.medi_status_false), color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deactivateTarget = null },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text(stringResource(R.string.medi_cencel)) }
            }
        )
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDeactivate: () -> Unit
) {
    val isActive = medication.isActive
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💊", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            medication.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            medication.dosage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row {
                    if (isActive) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Edit, null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDeactivate, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.PauseCircle, null,
                                modifier = Modifier.size(16.dp),
                                tint = ChartOrange)
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (medication.morning > 0) DozChip("🌅 ${medication.morning}", stringResource(R.string.medi_morning))
                if (medication.noon    > 0) DozChip("☀️ ${medication.noon}",    stringResource(R.string.medi_noon))
                if (medication.evening > 0) DozChip("🌆 ${medication.evening}", stringResource(R.string.medi_evening))
                if (medication.night   > 0) DozChip("🌙 ${medication.night}",   stringResource(R.string.medi_night))
            }

            if (medication.startDate.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.medi_start_date) + " ${medication.startDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (!isActive && medication.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text("⚠️ ", fontSize = 12.sp)
                    Text(medication.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }

            if (!isActive) {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.medi_status),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun DozChip(value: String, label: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NavyPrimary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold, color = NavyPrimary)
        Text(label, style = MaterialTheme.typography.bodySmall,
            fontSize = 9.sp, color = NavyPrimary.copy(alpha = 0.7f))
    }
}

@Composable
fun DozCounter(
    emoji: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(Modifier.height(4.dp))

        // + butonu üstte
        FilledIconButton(
            onClick = {
                val v = value.toIntOrNull() ?: 0
                onValueChange((v + 1).toString())
            },
            modifier = Modifier
                .size(36.dp)
                .fillMaxWidth(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = NavyPrimary
            )
        ) {
            Icon(Icons.Filled.Add, null,
                modifier = Modifier.size(18.dp),
                tint = Color.White)
        }

        // Sayı ortada
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // - butonu altta
        FilledIconButton(
            onClick = {
                val v = value.toIntOrNull() ?: 0
                if (v > 0) onValueChange((v - 1).toString())
            },
            modifier = Modifier
                .size(36.dp)
                .fillMaxWidth(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(Icons.Filled.Remove, null,
                modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDialog(
    medication: Medication?,
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit
) {
    var name      by remember { mutableStateOf(medication?.name      ?: "") }
    var dosage    by remember { mutableStateOf(medication?.dosage    ?: "") }
    var morning   by remember { mutableStateOf(medication?.morning?.toString()  ?: "0") }
    var noon      by remember { mutableStateOf(medication?.noon?.toString()     ?: "0") }
    var evening   by remember { mutableStateOf(medication?.evening?.toString()  ?: "0") }
    var night     by remember { mutableStateOf(medication?.night?.toString()    ?: "0") }
    var startDate by remember { mutableStateOf(medication?.startDate ?: "") }
    var notes     by remember { mutableStateOf(medication?.notes     ?: "") }
    //var reminderEnabled by remember {
    //  mutableStateOf((medication?.reminderHour ?: -1) >= 0)
    //}
    //var reminderHour   by remember {
    //    mutableStateOf(medication?.reminderHour?.takeIf { it >= 0 } ?: 8)
    //}
    //var reminderMinute by remember { mutableStateOf(medication?.reminderMinute ?: 0) }
    var morningReminderH  by remember { mutableStateOf(medication?.morningReminderHour.takeIf { (it ?: -1) >= 0 } ?: 7) }
    var morningReminderM  by remember { mutableStateOf(medication?.morningReminderMinute ?: 0) }
    var morningReminder   by remember { mutableStateOf((medication?.morningReminderHour ?: -1) >= 0) }

    var noonReminderH     by remember { mutableStateOf(medication?.noonReminderHour.takeIf { (it ?: -1) >= 0 } ?: 12) }
    var noonReminderM     by remember { mutableStateOf(medication?.noonReminderMinute ?: 0) }
    var noonReminder      by remember { mutableStateOf((medication?.noonReminderHour ?: -1) >= 0) }

    var eveningReminderH  by remember { mutableStateOf(medication?.eveningReminderHour.takeIf { (it ?: -1) >= 0 } ?: 20) }
    var eveningReminderM  by remember { mutableStateOf(medication?.eveningReminderMinute ?: 0) }
    var eveningReminder   by remember { mutableStateOf((medication?.eveningReminderHour ?: -1) >= 0) }

    var nightReminderH    by remember { mutableStateOf(medication?.nightReminderHour.takeIf { (it ?: -1) >= 0 } ?: 22) }
    var nightReminderM    by remember { mutableStateOf(medication?.nightReminderMinute ?: 0) }
    var nightReminder     by remember { mutableStateOf((medication?.nightReminderHour ?: -1) >= 0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = java.util.Calendar.getInstance().timeInMillis
    )

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    if (medication == null) stringResource(R.string.medi_add) else stringResource(R.string.medi_edit2),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                KalbimTextField(
                    name, { name   = it }, stringResource(R.string.medi_name),
                    icon = Icons.Filled.Medication,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                KalbimTextField(dosage, { dosage = it }, stringResource(R.string.medi_dose),colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                    unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                ))

                HorizontalDivider()

                Text(stringResource(R.string.medi_day_use),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DozCounter("🌅", stringResource(R.string.medi_morning),  morning, { morning = it }, Modifier.weight(1f))
                    DozCounter("☀️", stringResource(R.string.medi_noon),   noon,    { noon    = it }, Modifier.weight(1f))
                    DozCounter("🌆", stringResource(R.string.medi_evening),  evening, { evening = it }, Modifier.weight(1f))
                    DozCounter("🌙", stringResource(R.string.medi_night),   night,   { night   = it }, Modifier.weight(1f))
                }

                HorizontalDivider()

                OutlinedButton(
                    onClick  = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, null,
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (startDate.isBlank()) stringResource(R.string.medi_start_date_text)
                        else stringResource(R.string.medi_start_date) + " $startDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // ESKİ hatırlatıcı bölümünü tamamen kaldır, yerine:
                HorizontalDivider()

                Text(stringResource(R.string.medi_notifi),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)

// Sadece doz girilmiş vakitler için göster
                if (morning.toIntOrNull() ?: 0 > 0) {
                    ReminderTimeRow("🌅" + stringResource(R.string.medi_morning), morningReminder, morningReminderH, morningReminderM,
                        onToggle = { morningReminder = it },
                        onHourChange = { morningReminderH = it },
                        onMinuteChange = { morningReminderM = it })
                }
                if (noon.toIntOrNull() ?: 0 > 0) {
                    ReminderTimeRow("☀️" + stringResource(R.string.medi_noon), noonReminder, noonReminderH, noonReminderM,
                        onToggle = { noonReminder = it },
                        onHourChange = { noonReminderH = it },
                        onMinuteChange = { noonReminderM = it })
                }
                if (evening.toIntOrNull() ?: 0 > 0) {
                    ReminderTimeRow("🌆" + stringResource(R.string.medi_evening), eveningReminder, eveningReminderH, eveningReminderM,
                        onToggle = { eveningReminder = it },
                        onHourChange = { eveningReminderH = it },
                        onMinuteChange = { eveningReminderM = it })
                }
                if (night.toIntOrNull() ?: 0 > 0) {
                    ReminderTimeRow("🌙" + stringResource(R.string.medi_night), nightReminder, nightReminderH, nightReminderM,
                        onToggle = { nightReminder = it },
                        onHourChange = { nightReminderH = it },
                        onMinuteChange = { nightReminderM = it })
                }


                OutlinedTextField(
                    value         = notes,
                    onValueChange = { notes = it },
                    label         = { Text(stringResource(R.string.medi_note)) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    maxLines      = 3,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.medi_cencel)) }
                    Button(
                        onClick = {
                            onSave(Medication(
                                id       = medication?.id ?: 0,
                                name     = name.trim(),
                                dosage   = dosage.trim(),
                                morning  = morning.toIntOrNull() ?: 0,
                                noon     = noon.toIntOrNull()    ?: 0,
                                evening  = evening.toIntOrNull() ?: 0,
                                night    = night.toIntOrNull()   ?: 0,
                                morningReminderHour    = if (morningReminder)  morningReminderH  else -1,
                                morningReminderMinute  = morningReminderM,
                                noonReminderHour       = if (noonReminder)     noonReminderH     else -1,
                                noonReminderMinute     = noonReminderM,
                                eveningReminderHour    = if (eveningReminder)  eveningReminderH  else -1,
                                eveningReminderMinute  = eveningReminderM,
                                nightReminderHour      = if (nightReminder)    nightReminderH    else -1,
                                nightReminderMinute    = nightReminderM,
                                startDate = startDate,
                                isActive  = medication?.isActive ?: true,
                                notes     = notes.trim()
                            ))
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = name.isNotBlank() && dosage.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) { Text(stringResource(R.string.save), color = Color.White) }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val c = java.util.Calendar.getInstance()
                            .apply { timeInMillis = millis }
                        startDate = "%02d.%02d.%04d".format(
                            c.get(java.util.Calendar.DAY_OF_MONTH),
                            c.get(java.util.Calendar.MONTH) + 1,
                            c.get(java.util.Calendar.YEAR)
                        )
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.c_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.medi_cencel)) }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = true)
        }
    }
}

@Composable
fun ReminderTimeRow(
    label: String,
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onToggle: (Boolean) -> Unit,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f))
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NavyPrimary,
                    checkedTrackColor = NavyPrimary.copy(alpha = 0.5f))
            )
        }

        if (enabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.medi_hour), style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(36.dp))

                // Saat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(
                        onClick = { if (hour > 0) onHourChange(hour - 1) },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(Icons.Filled.Remove, null, Modifier.size(12.dp)) }
                    Text("%02d".format(hour),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center)
                    FilledIconButton(
                        onClick = { if (hour < 23) onHourChange(hour + 1) },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(Icons.Filled.Add, null, Modifier.size(12.dp)) }
                }

                Text(":", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)

                // Dakika
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(
                        onClick = { if (minute >= 5) onMinuteChange(minute - 5) },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(Icons.Filled.Remove, null, Modifier.size(12.dp)) }
                    Text("%02d".format(minute),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center)
                    FilledIconButton(
                        onClick = { if (minute < 55) onMinuteChange(minute + 5) },
                        modifier = Modifier.size(28.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(Icons.Filled.Add, null, Modifier.size(12.dp)) }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }
}