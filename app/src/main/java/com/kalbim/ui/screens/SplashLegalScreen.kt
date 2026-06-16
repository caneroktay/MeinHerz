package com.kalbim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kalbim.data.model.UserProfile
import com.kalbim.ui.theme.NavyPrimary
import com.kalbim.ui.theme.NavyLight
import com.kalbim.viewmodel.KalbimViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.kalbim.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kalbim.notification.LanguageManager

@Composable
fun SplashLegalScreen(vm: KalbimViewModel) {
    var showLegal   by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var legalOk     by remember { mutableStateOf(false) }
    var privacyOk   by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(48.dp))

            // Logo bölümü
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.kalbim_icon),
                    contentDescription = "Kalbim icon",
                    modifier = Modifier.size(250.dp)
                )
                Spacer(Modifier.height(0.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(0.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.app_slogan),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.app_slogan2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Bildirim kartları
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LegalCard(
                    icon       = Icons.Filled.Policy,
                    title      = stringResource(R.string.legal_title),
                    subtitle   = stringResource(R.string.legal_subtitle),
                    isAccepted = legalOk,
                    onClick    = { showLegal = true }
                )
                LegalCard(
                    icon       = Icons.Filled.GppGood,
                    title      = stringResource(R.string.data_privacy_title),
                    subtitle   = stringResource(R.string.data_privacy_subtitle),
                    isAccepted = privacyOk,
                    onClick    = { showPrivacy = true }
                )
            }

            // Devam Et butonu
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { vm.saveProfile(UserProfile(
                        onboardingDone = false,
                        language = LanguageManager.getDeviceLanguage()
                    )) },
                    enabled = legalOk && privacyOk,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        stringResource(R.string.c_continue),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
                if (!legalOk || !privacyOk) {
                    Text(
                        stringResource(R.string.c_sub_continue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showLegal) {
        InfoDialog(
            title = stringResource(R.string.legal_text_title),
            text = stringResource(R.string.legal_text),
            onDismiss = { showLegal = false; legalOk = true }
        )
    }
    if (showPrivacy) {
        InfoDialog(
            title = stringResource(R.string.data_privacy_title),
            text = stringResource(R.string.data_privacy_text),
            onDismiss = { showPrivacy = false; privacyOk = true }
        )
    }
}

@Composable
fun LegalCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isAccepted: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccepted)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAccepted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isAccepted) Color.White
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isAccepted) {
                Text("✓", color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Filled.Policy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun InfoDialog(title: String, text: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text, style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp)
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(stringResource(R.string.btn_accept),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis, // Sığmazsa "..." koyar
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}