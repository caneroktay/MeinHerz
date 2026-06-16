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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kalbim.data.model.Measurement
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.stringResource
import com.kalbim.R

@Composable
fun HomeScreen(vm: KalbimViewModel) {
    val profile        by vm.profile.collectAsState()
    val measurements   by vm.last30.collectAsState()
    val latestBp       by vm.latestBp.collectAsState()
    val latestWeight   by vm.latestWeight.collectAsState()
    val latestLab      by vm.latestLab.collectAsState()

    var showBpDialog     by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showLabDialog    by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Üst Header ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(NavyPrimary, NavyLight))
                )
                .padding(horizontal = 25.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text =  stringResource(R.string.hello) + " " + "${profile?.firstName ?: ""}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold
                    )
//                    Text(
//                       text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
//                       style = MaterialTheme.typography.bodyLarge,
//                       color = Color.White,
//                    )
                    Text(
                        text = stringResource(R.string.home_hello),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // ── İçerik ─────────────────────────────────────────
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            // Özet başlığı
            /*Text(
                text = "Özet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )*/
            Text(
                text = stringResource(R.string.last_records),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.offset(x = (10).dp),
                fontWeight = FontWeight.Bold
            )

            // ── Tansiyon & Nabız Büyük Kartlar ─────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BigMetricCard(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Filled.Favorite,
                    iconColor   = AccentRed,
                    label       = stringResource(R.string.home_pressure),
                    value       = if (latestBp?.systolic != null)
                        "${latestBp!!.systolic}/${latestBp!!.diastolic}"
                    else "--/--",
                    unit        = "mmHg",
                    accentColor = AccentRed
                )
                BigMetricCard(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Filled.MonitorHeart,
                    iconColor   = AccentTeal,
                    label       = stringResource(R.string.pulse),
                    value       = latestBp?.pulse?.toString() ?: "--",
                    unit        = "bpm",
                    accentColor = AccentTeal
                )
            }

            // ── Kilo Büyük Kart ─────────────────────────────
            KiloCard(
                weightKg    = latestWeight?.weightKg,
                dateLabel   = latestWeight?.dateLabel ?: ""
            )

            // Son 7 Gün Mini Grafik
            if (measurements.size >= 2) {
                MiniChartCard(measurements = measurements)
            }

            // ── NT-proBNP & Troponin ────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SmallMetricCard(
                    modifier  = Modifier.weight(1f),
                    emoji     = "🧪",
                    label     = "NT-proBNP",
                    value     = latestLab?.ntProBnp?.let { "%.0f".format(it) } ?: "--",
                    unit      = "pg/mL"
                )
                SmallMetricCard(
                    modifier  = Modifier.weight(1f),
                    emoji     = "🔬",
                    label     = "Troponin",
                    value     = latestLab?.troponin?.let { "%.2f".format(it) } ?: "--",
                    unit      = "ng/L"
                )
            }

            // ── Yeni Kayıt Ekle Butonu ──────────────────────
            Button(
                onClick  = { showBpDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.new_record2),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            // Hızlı erişim butonları
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    emoji    = "⚖️",
                    label    = stringResource(R.string.home_weight_add),
                    onClick  = { showWeightDialog = true }
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    emoji    = "🧪",
                    label    = stringResource(R.string.home_blood_add),
                    onClick  = { showLabDialog = true }
                )
            }

            // Yasal uyarı
            Card(
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠️", fontSize = 16.sp)
                    Text(
                        stringResource(R.string.disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // ── Dialog'lar ──────────────────────────────────────────
    if (showBpDialog) {
        BpDialog(
            onDismiss = { showBpDialog = false },
            onSave    = { sys, dia, pulse ->
                vm.addMeasurement(systolic = sys, diastolic = dia, pulse = pulse)
                showBpDialog = false
            }
        )
    }
    if (showWeightDialog) {
        WeightDialog(
            onDismiss = { showWeightDialog = false },
            onSave    = { w ->
                vm.addMeasurement(weightKg = w)
                showWeightDialog = false
            }
        )
    }
    if (showLabDialog) {
        LabDialog(
            onDismiss = { showLabDialog = false },
            onSave    = { bnp, trop ->
                vm.addMeasurement(ntProBnp = bnp, troponin = trop)
                showLabDialog = false
            }
        )
    }
}

// ── Büyük Metrik Kart ──────────────────────────────────────
@Composable
fun BigMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    unit: String,
    accentColor: Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Text(
                unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Kilo Kart ──────────────────────────────────────────────
@Composable
fun KiloCard(weightKg: Float?, dateLabel: String) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    stringResource(R.string.home_weight),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        weightKg?.let { "%.1f".format(it) } ?: "--",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChartGreen
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "kg",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                if (dateLabel.isNotBlank()) {
                    Text(
                        dateLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ChartGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MonitorWeight,
                    null,
                    tint = ChartGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ── Mini Grafik Kart ───────────────────────────────────────
@Composable
fun MiniChartCard(measurements: List<Measurement>) {
    val bpData = measurements.filter { it.systolic != null }.reversed().takeLast(7)
    if (bpData.size < 2) return

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.home_chart),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val w         = size.width
                val h         = size.height
                val sysPoints = bpData.map { it.systolic!!.toFloat() }
                val diaPoints = bpData.map { it.diastolic!!.toFloat() }
                val allPts    = sysPoints + diaPoints
                val minV  = (allPts.minOrNull() ?: 0f) * 0.9f
                val maxV  = (allPts.maxOrNull() ?: 1f) * 1.1f
                val range = if (maxV - minV < 1f) 1f else maxV - minV

                listOf(
                    sysPoints to androidx.compose.ui.graphics.Color(0xFFE53935),
                    diaPoints to androidx.compose.ui.graphics.Color(0xFF1E88E5)
                ).forEach { (pts, color) ->
                    val path = androidx.compose.ui.graphics.Path()
                    pts.forEachIndexed { i, v ->
                        val x = i.toFloat() / (pts.size - 1) * w
                        val y = h - ((v - minV) / range * h)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path  = path,
                        color = color,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                    pts.forEachIndexed { i, v ->
                        val x = i.toFloat() / (pts.size - 1) * w
                        val y = h - ((v - minV) / range * h)
                        drawCircle(color = color, radius = 6f,
                            center = androidx.compose.ui.geometry.Offset(x, y))
                        drawCircle(
                            color  = androidx.compose.ui.graphics.Color.White,
                            radius = 3f,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }
        }
    }
}
// ── Küçük Metrik Kart ──────────────────────────────────────
@Composable
fun SmallMetricCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    value: String,
    unit: String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ChartOrange
            )
            Text(
                unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Hızlı Aksiyon Butonu ───────────────────────────────────
@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        border   = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.5.dp
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(emoji, fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

// ── Dialog'lar ─────────────────────────────────────────────
@Composable
fun BpDialog(onDismiss: () -> Unit, onSave: (Int, Int, Int) -> Unit) {
    var sys   by remember { mutableStateOf("") }
    var dia   by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, null,
                        tint = AccentRed, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.home_pressure_dialog_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                KalbimTextField(
                    sys,
                    { sys   = it },
                    stringResource(R.string.systolic),
                    keyboardType = KeyboardType.Number,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                KalbimTextField(
                    dia,
                    { dia   = it },
                    stringResource(R.string.diastolic),
                    keyboardType = KeyboardType.Number,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                KalbimTextField(
                    pulse,
                    { pulse = it },
                    stringResource(R.string.pulse),
                    keyboardType = KeyboardType.Number,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)) {
                        Text(stringResource(R.string.c_cencel))
                    }
                    Button(
                        onClick = {
                            onSave(
                                sys.toIntOrNull()   ?: return@Button,
                                dia.toIntOrNull()   ?: return@Button,
                                pulse.toIntOrNull() ?: return@Button
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = sys.isNotBlank() && dia.isNotBlank() && pulse.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(containerColor = NavyLight)
                    ) { Text(stringResource(R.string.save), color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun WeightDialog(onDismiss: () -> Unit, onSave: (Float) -> Unit) {
    var weight by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚖️", fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.home_weight_add), style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                KalbimTextField(
                    weight, { weight = it }, stringResource(R.string.weight),
                    keyboardType = KeyboardType.Decimal,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.c_cencel)) }
                    Button(
                        onClick  = { weight.toFloatOrNull()?.let { onSave(it) } },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = weight.isNotBlank(),

                    ) { Text(stringResource(R.string.save), color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun LabDialog(onDismiss: () -> Unit, onSave: (Float?, Float?) -> Unit) {
    var bnp  by remember { mutableStateOf("") }
    var trop by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧪", fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.home_blood_add), style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                KalbimTextField(
                    bnp, { bnp  = it }, "NT-proBNP / BNP (pg/mL)",
                    keyboardType = KeyboardType.Decimal,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                KalbimTextField(
                    trop, { trop = it }, "Troponin T/I (ng/L)",
                    keyboardType = KeyboardType.Decimal,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.c_cencel)) }
                    Button(
                        onClick  = { onSave(bnp.toFloatOrNull(), trop.toFloatOrNull()) },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = bnp.isNotBlank() || trop.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) { Text(stringResource(R.string.save), color = Color.White) }
                }
            }
        }
    }
}