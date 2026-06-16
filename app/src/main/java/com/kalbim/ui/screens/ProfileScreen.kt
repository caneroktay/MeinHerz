package com.kalbim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kalbim.data.model.UserProfile
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel
import com.kalbim.notification.LanguageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.kalbim.R


@Composable
fun ProfileScreen(vm: KalbimViewModel) {
    val profile by vm.profile.collectAsState()
    var editMode by remember { mutableStateOf(false) }

    var firstName   by remember(profile) { mutableStateOf(profile?.firstName ?: "") }
    var lastName    by remember(profile) { mutableStateOf(profile?.lastName  ?: "") }
    var birthDate   by remember(profile) { mutableStateOf(profile?.birthDate ?: "") }
    var weight      by remember(profile) { mutableStateOf(profile?.weightKg?.toString() ?: "") }
    var medHistory  by remember(profile) { mutableStateOf(profile?.medicalHistory ?: "") }
    var medications by remember(profile) { mutableStateOf(profile?.medications ?: "") }
    var themeMode   by remember(profile) { mutableStateOf(profile?.themeMode ?: "auto") }
    //var language    by remember(profile) { mutableStateOf(profile?.language  ?: "tr") }
    var language by remember(profile) {
        mutableStateOf(profile?.language ?: LanguageManager.getDeviceLanguage())
    }
    var morningH    by remember(profile) { mutableStateOf(profile?.notifMorningHour ?: 7) }
    var noonH       by remember(profile) { mutableStateOf(profile?.notifNoonHour    ?: 12) }
    var eveningH    by remember(profile) { mutableStateOf(profile?.notifEveningHour ?: 20) }

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
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null,
                        tint = Color.White, modifier = Modifier.size(50.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    profile?.birthDate ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(16.dp))
                // Düzenle / Kaydet butonu
                Button(
                    onClick = {
                        if (editMode) {
                            vm.saveProfile(
                                (profile ?: UserProfile()).copy(
                                    firstName        = firstName.trim(),
                                    lastName         = lastName.trim(),
                                    birthDate        = birthDate.trim(),
                                    weightKg         = weight.toFloatOrNull() ?: 0f,
                                    medicalHistory   = medHistory.trim(),
                                    medications      = medications.trim(),
                                    notifMorningHour = morningH,
                                    notifNoonHour    = noonH,
                                    notifEveningHour = eveningH,
                                    themeMode        = themeMode,
                                    language         = language,
                                    onboardingDone   = true
                                )
                            )
                        }
                        editMode = !editMode
                    },
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (editMode) Color.White else Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Icon(
                        if (editMode) Icons.Filled.Save else Icons.Filled.Edit,
                        null,
                        tint = if (editMode) NavyPrimary else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (editMode) stringResource(R.string.save) else stringResource(R.string.edit),
                        color = if (editMode) NavyPrimary else Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── Bölümler ───────────────────────────────────────
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Kişisel Bilgiler
            ProfileSectionCard(title = stringResource(R.string.personal_info)) {
                if (editMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        KalbimTextField(
                            firstName,
                            { firstName = it },
                            stringResource(R.string.profile_firstname),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        KalbimTextField(
                            lastName, { lastName = it }, stringResource(R.string.profile_lastname),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    KalbimTextField(
                        birthDate,
                        { birthDate = it },
                        stringResource(R.string.profile_birth),
                        placeholder = stringResource(R.string.profile_birth),
                        icon = Icons.Filled.CalendarMonth,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                            unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    //KalbimTextField(weight, { weight = it },
                    //    "Güncel Kilo (kg)", icon = Icons.Filled.MonitorWeight)
                } else {
                    ProfileInfoRow(stringResource(R.string.profile_fullname),
                        "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}")
                    ProfileInfoRow(stringResource(R.string.profile_birth), profile?.birthDate ?: "--")
                    //ProfileInfoRow("Güncel Kilo",
                    //    if ((profile?.weightKg ?: 0f) > 0f)
                    //        "${"%.1f".format(profile!!.weightKg)} kg" else "--")
                }
            }

            // Tıbbi Geçmiş
            ProfileSectionCard(title = stringResource(R.string.medical_history_title)) {
                if (editMode) {
                    OutlinedTextField(
                        value = medHistory,
                        onValueChange = { medHistory = it },
                        label = { Text(stringResource(R.string.medical_history_label)) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary)
                    )
                } else {
                    Text(
                        medHistory.ifBlank { stringResource(R.string.no_add) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (medHistory.isBlank())
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Kullanılan İlaçlar
            MedicationsSection(vm = vm)

            // Hatırlatıcı Saatleri
            ProfileSectionCard(title = stringResource(R.string.no_add)) {
                listOf(
                    Triple(stringResource(R.string.reminder_morning),     morningH) { h: Int -> morningH = h },
                    Triple(stringResource(R.string.reminder_noon),  noonH)    { h: Int -> noonH    = h },
                    Triple(stringResource(R.string.reminder_evening), eveningH) { h: Int -> eveningH = h }
                ).forEach { (label, hour, setter) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f))
                        if (editMode) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { if (hour > 0) setter(hour - 1) },
                                    modifier = Modifier.size(32.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Filled.Remove, null,
                                        modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    "%02d:00".format(hour),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(52.dp),
                                    textAlign = TextAlign.Center
                                )
                                FilledIconButton(
                                    onClick = { if (hour < 23) setter(hour + 1) },
                                    modifier = Modifier.size(32.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Filled.Add, null,
                                        modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Text(
                                "%02d:00".format(hour),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }

            // Görünüm Ayarları
            ProfileSectionCard(title = stringResource(R.string.appearance_title)) {
                Text(stringResource(R.string.appearance_theme), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("auto" to stringResource(R.string.theme_auto), "light" to stringResource(R.string.theme_light), "dark" to stringResource(R.string.theme_dark))
                        .forEach { (mode, label) ->
                            FilterChip(
                                selected  = themeMode == mode,
                                onClick   = { if (editMode) themeMode = mode },
                                label     = { Text(label,
                                    style = MaterialTheme.typography.labelSmall) },
                                modifier  = Modifier.weight(1f),
                                colors    = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor     = Color.White
                                )
                            )
                        }
                }

                Spacer(Modifier.height(14.dp))

                Text(stringResource(R.string.lang_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))


                if (!editMode) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.lang_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            // Veri Yönetimi
            // Bölümün içi:
            ProfileSectionCard(title = stringResource(R.string.data_management)) {
                val context = LocalContext.current
                val scope   = rememberCoroutineScope()
                val successMessageExport = stringResource(R.string.data_export)
                val successMessageImport = stringResource(R.string.data_import)
                val successMessageNotRead = stringResource(R.string.data_not_read)

                // Dışa aktar launcher
                val exportLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json")
                ) { uri ->
                    uri?.let {
                        scope.launch {
                            try {
                                val json = vm.getBackupJson()
                                context.contentResolver.openOutputStream(uri)?.use { stream ->
                                    stream.write(json.toByteArray())
                                }
                                Toast.makeText(context,
                                    successMessageExport, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context,
                                    "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // İçe aktar launcher
                val importLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        scope.launch {
                            try {
                                val json = context.contentResolver
                                    .openInputStream(uri)?.bufferedReader()?.readText() ?: ""
                                vm.restoreFromJson(
                                    jsonString = json,
                                    onSuccess  = {
                                        Toast.makeText(context,
                                            successMessageImport,
                                            Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context,
                                            "❌ Error: $err", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } catch (e: Exception) {
                                Toast.makeText(context,
                                    successMessageNotRead + "${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // Dışa Aktar butonu
                Button(
                    onClick = {
                        val fileName = "Kalbim_Backup_${
                            SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                        }.json"
                        exportLauncher.launch(fileName)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Filled.Upload, null,
                        tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.backup_export), color = Color.White)
                }

                Spacer(Modifier.height(10.dp))

                // İçe Aktar butonu
                OutlinedButton(
                    onClick  = { importLauncher.launch("application/json") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                ) {
                    Icon(Icons.Filled.Download, null,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.backup_import))
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    stringResource(R.string.backup_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }



            // kaydet /Düzenle
            // ProfileSectionCard(title = "") {
                Button(
                    onClick = {
                        if (editMode) {
                            vm.saveProfile(
                                (profile ?: UserProfile()).copy(
                                    firstName        = firstName.trim(),
                                    lastName         = lastName.trim(),
                                    birthDate        = birthDate.trim(),
                                    weightKg         = weight.toFloatOrNull() ?: 0f,
                                    medicalHistory   = medHistory.trim(),
                                    medications      = medications.trim(),
                                    notifMorningHour = morningH,
                                    notifNoonHour    = noonH,
                                    notifEveningHour = eveningH,
                                    themeMode        = themeMode,
                                    language         = language,
                                    onboardingDone   = true
                                )
                            )
                        }
                        editMode = !editMode
                    },
                    border   = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                    shape  = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (editMode) Color.Red else Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Icon(
                        if (editMode) Icons.Filled.Save else Icons.Filled.Edit,
                        null,
                        tint = if (editMode) Color.White else NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (editMode) stringResource(R.string.save) else stringResource(R.string.edit),
                        color = if (editMode) Color.White else NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            //}

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Yardımcı Composable'lar ────────────────────────────────

@Composable
fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
}