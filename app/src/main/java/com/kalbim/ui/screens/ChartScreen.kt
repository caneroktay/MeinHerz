package com.kalbim.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kalbim.R
import com.kalbim.data.model.Measurement
import com.kalbim.ui.theme.*
import com.kalbim.viewmodel.KalbimViewModel

enum class ChartType { TANSIYON, NABIZ, KILO, NTPROBNP, TROPONIN }
enum class ChartRange { SON30, TUMU }

@Composable
fun ChartScreen(vm: KalbimViewModel) {
    val measurements by vm.allMeasurements.collectAsState()
    var selectedChart by remember { mutableStateOf(ChartType.NABIZ) }
    var selectedRange by remember { mutableStateOf(ChartRange.SON30) }

    // Seçilen aralığa göre filtrele
    val filtered = when (selectedRange) {
        // ChartRange.SON7  -> measurements.takeLast(7)
        ChartRange.SON30 -> measurements.takeLast(30)
        ChartRange.TUMU  -> measurements
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
                .background(androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(NavyPrimary, NavyLight)))
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                stringResource(R.string.chart_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Metrik Seçici Dropdown ──────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartTypeDropdown(
                    selected  = selectedChart,
                    onSelect  = { selectedChart = it },
                    modifier  = Modifier.weight(1f)
                )
                ChartRangeDropdown(
                    selected  = selectedRange,
                    onSelect  = { selectedRange = it },
                    modifier  = Modifier.weight(1f)
                )
            }

            // ── Ana Grafik Kartı ────────────────────────────
            val chartData = when (selectedChart) {
                ChartType.TANSIYON -> filtered.filter { it.systolic != null }.reversed()
                ChartType.NABIZ    -> filtered.filter { it.pulse != null }.reversed()
                ChartType.KILO     -> filtered.filter { it.weightKg != null }.reversed()
                ChartType.NTPROBNP -> filtered.filter { it.ntProBnp != null }.reversed()
                ChartType.TROPONIN -> filtered.filter { it.troponin != null }.reversed()
            }

            if (chartData.size >= 2) {
                MainChartCard(
                    chartType    = selectedChart,
                    measurements = chartData
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.chart_exp_text),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Tüm Metrikler Özet Kartları ─────────────────
            //Text(
            //    "Tüm Metrikler",
            //    style = MaterialTheme.typography.titleMedium,
            //    fontWeight = FontWeight.Bold
            //)

            val bpData     = measurements.filter { it.systolic  != null }.reversed()
            val weightData = measurements.filter { it.weightKg  != null }.reversed()
            val bnpData    = measurements.filter { it.ntProBnp  != null }.reversed()

            if (bpData.size >= 2) {
                MiniMetricChart(
                    title  = stringResource(R.string.chart_pressure),
                    points = bpData.map { it.systolic!!.toFloat() },
                    color  = ChartRed,
                    unit   = "mmHg",
                    latest = "${bpData.last().systolic}/${bpData.last().diastolic}"
                )
            }
            if (weightData.size >= 2) {
                MiniMetricChart(
                    title  = stringResource(R.string.chart_weight),
                    points = weightData.map { it.weightKg!! },
                    color  = ChartGreen,
                    unit   = "kg",
                    latest = "%.1f".format(weightData.last().weightKg)
                )
            }
            if (bnpData.size >= 2) {
                MiniMetricChart(
                    title  = "🧪 NT-proBNP",
                    points = bnpData.map { it.ntProBnp!! },
                    color  = ChartOrange,
                    unit   = "pg/mL",
                    latest = "%.0f".format(bnpData.last().ntProBnp)
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Metrik Dropdown ────────────────────────────────────────
@Composable
fun ChartTypeDropdown(
    selected: ChartType,
    onSelect: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val labels = mapOf(
        ChartType.TANSIYON to stringResource(R.string.chart_pressure),
        ChartType.NABIZ    to stringResource(R.string.chart_pulse),
        ChartType.KILO     to stringResource(R.string.chart_weight),
        ChartType.NTPROBNP to "🧪 NT-proBNP",
        ChartType.TROPONIN to "🔬 Troponin"
    )
    Box(modifier = modifier) {
        Card(
            onClick = { expanded = true },
            shape   = RoundedCornerShape(12.dp),
            colors  = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    labels[selected] ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text("▾", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            labels.forEach { (type, label) ->
                DropdownMenuItem(
                    text    = { Text(label) },
                    onClick = { onSelect(type); expanded = false }
                )
            }
        }
    }
}

// ── Zaman Aralığı Dropdown ─────────────────────────────────
@Composable
fun ChartRangeDropdown(
    selected: ChartRange,
    onSelect: (ChartRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val labels = mapOf(
       // ChartRange.SON7  to "Son 7 Gün",
        ChartRange.SON30 to stringResource(R.string.chart_30),
        ChartRange.TUMU  to stringResource(R.string.chart_all)
    )
    Box(modifier = modifier) {
        Card(
            onClick = { expanded = true },
            shape   = RoundedCornerShape(12.dp),
            colors  = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    labels[selected] ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text("▾", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            labels.forEach { (range, label) ->
                DropdownMenuItem(
                    text    = { Text(label) },
                    onClick = { onSelect(range); expanded = false }
                )
            }
        }
    }
}

// ── Ana Grafik Kartı ───────────────────────────────────────
@Composable
fun MainChartCard(chartType: ChartType, measurements: List<Measurement>) {
    val datasets: List<Pair<List<Float>, Color>> = when (chartType) {
        ChartType.TANSIYON -> listOf(
            measurements.map { it.systolic!!.toFloat()  } to ChartRed,
            measurements.map { it.diastolic!!.toFloat() } to ChartBlue
        )
        ChartType.NABIZ    -> listOf(measurements.map { it.pulse!!.toFloat() } to ChartRed)
        ChartType.KILO     -> listOf(measurements.map { it.weightKg!!        } to ChartGreen)
        ChartType.NTPROBNP -> listOf(measurements.map { it.ntProBnp!!        } to ChartOrange)
        ChartType.TROPONIN -> listOf(measurements.map { it.troponin!!        } to ChartPurple)
    }

    val legendLabels = when (chartType) {
        ChartType.TANSIYON -> listOf(stringResource(R.string.systolic) to ChartRed, stringResource(R.string.diastolic) to ChartBlue)
        ChartType.NABIZ    -> listOf(stringResource(R.string.pulse)     to ChartRed)
        ChartType.KILO     -> listOf(stringResource(R.string.weight)       to ChartGreen)
        ChartType.NTPROBNP -> listOf("NT-proBNP (pg/mL)" to ChartOrange)
        ChartType.TROPONIN -> listOf("Troponin (ng/L)" to ChartPurple)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                legendLabels.forEach { (label, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(10.dp)) { drawCircle(color) }
                        Spacer(Modifier.width(5.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            val allPoints = datasets.flatMap { it.first }
            val minVal = (allPoints.minOrNull() ?: 0f) * 0.92f
            val maxVal = (allPoints.maxOrNull() ?: 1f) * 1.08f
            val range  = if (maxVal - minVal < 1f) 1f else maxVal - minVal

            // Y ekseni etiketleri
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Y ekseni değerleri
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .height(200.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(5) { i ->
                            val v = maxVal - range * i / 4
                            Text(
                                "%.0f".format(v),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Grafik canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(start = 36.dp)
                    ) {
                        val w      = size.width
                        val h      = size.height
                        val chartH = h - 16f

                        // Izgara çizgileri
                        repeat(5) { i ->
                            val y = chartH * i / 4
                            drawLine(
                                Color.Gray.copy(alpha = 0.15f),
                                Offset(0f, y), Offset(w, y), 1f
                            )
                        }

                        // Çizgiler ve noktalar
                        datasets.forEach { (points, color) ->
                            val path = Path()
                            points.forEachIndexed { i, v ->
                                val x = i.toFloat() / (points.size - 1) * w
                                val y = chartH - ((v - minVal) / range * chartH)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color, style = Stroke(width = 3f))
                            points.forEachIndexed { i, v ->
                                val x = i.toFloat() / (points.size - 1) * w
                                val y = chartH - ((v - minVal) / range * chartH)
                                drawCircle(color,        6f, Offset(x, y))
                                drawCircle(Color.White,  3f, Offset(x, y))
                            }
                        }
                    }
                }

                // X ekseni tarih etiketleri
                val labels = measurements.map { it.dateLabel.take(5) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val step = (labels.size / 4).coerceAtLeast(1)
                    labels.forEachIndexed { i, lbl ->
                        if (i % step == 0 || i == labels.size - 1) {
                            Text(
                                lbl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Mini Metrik Grafik ─────────────────────────────────────
@Composable
fun MiniMetricChart(
    title: String,
    points: List<Float>,
    color: Color,
    unit: String,
    latest: String
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    "$latest $unit",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Canvas(
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp)
            ) {
                val w    = size.width
                val h    = size.height
                val minV = (points.minOrNull() ?: 0f) * 0.95f
                val maxV = (points.maxOrNull() ?: 1f) * 1.05f
                val rng  = if (maxV - minV < 1f) 1f else maxV - minV
                val path = Path()
                points.forEachIndexed { i, v ->
                    val x = i.toFloat() / (points.size - 1) * w
                    val y = h - ((v - minV) / rng * h)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color, style = Stroke(width = 2.5f))
                val lastX = w
                val lastY = h - ((points.last() - minV) / rng * h)
                drawCircle(color, 5f, Offset(lastX, lastY))
            }
        }
    }
}