package com.kalbim.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.kalbim.pdf.PdfGenerator
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.stringResource
import com.kalbim.R

@Composable
fun ReportScreen(vm: KalbimViewModel) {
    val context      = LocalContext.current
    val profile      by vm.profile.collectAsState()
    val measurements by vm.allMeasurements.collectAsState()
    val medications by vm.allMedications.collectAsState()
    var isGenerating by remember { mutableStateOf(false) }
    var lastFilePath by remember { mutableStateOf("") }
    val sharetext = stringResource(R.string.report_pdf_share)
    val pdfexep = stringResource(R.string.report_pdf_exep)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.report_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.report_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Özet bilgi kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.report_box_title), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                InfoRow( stringResource(R.string.report_box_person), "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}")
                InfoRow(stringResource(R.string.report_box_measurement), "${measurements.size}" + " " + stringResource(R.string.report_box_measurement_text))
                InfoRow(stringResource(R.string.report_box_medical_history),
                    if (profile?.medicalHistory.isNullOrBlank()) stringResource(R.string.report_box_medical_history_text) else stringResource(R.string.report_box_medical_history_text2))
                InfoRow(stringResource(R.string.report_box_medicinelist),
                    if (medications.isEmpty()) stringResource(R.string.report_box_medicinelist_null)
                    else "${medications.size}" + " " + stringResource(R.string.report_box_medicinelist_text) + " " + "(${medications.count { it.isActive }}" + " " + stringResource(R.string.report_box_medicinelist_text2) + ")")
            }
        }

        if (measurements.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = stringResource(R.string.report_min_ex),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // PDF Oluştur butonu
        Button(
            onClick = {
                isGenerating = true
                val file = PdfGenerator.generate(
                    context      = context,
                    profile      = profile ?: return@Button,
                    measurements = measurements,
                    medications  = medications
                )
                //
                isGenerating = false
                if (file != null) {
                    lastFilePath = file.absolutePath
                    // Dosyayı paylaş
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
                        // PDF viewer yok, paylaş
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(shareIntent, sharetext ))
                    }
                } else {
                    Toast.makeText(context, pdfexep, Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isGenerating && measurements.isNotEmpty() && profile != null,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.report_pdf_creat), fontSize = 16.sp)
            }
        }

        if (lastFilePath.isNotBlank()) {
            Text(
                text = stringResource(R.string.report_pdf_save) + " " + "${lastFilePath.substringAfterLast("/")}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Yasal uyarı
        Text(
            text = stringResource(R.string.report_pdf_info),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}