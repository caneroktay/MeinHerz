package com.kalbim.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kalbim.data.model.UserProfile
import com.kalbim.ui.theme.NavyPrimary
import com.kalbim.viewmodel.KalbimViewModel
import com.kalbim.R
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.kalbim.ui.theme.NavyDark
import java.util.Calendar
import androidx.compose.ui.res.stringResource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(vm: KalbimViewModel) {
    val profile by vm.profile.collectAsState()

    var firstName by remember { mutableStateOf(profile?.firstName ?: "") }
    var lastName  by remember { mutableStateOf(profile?.lastName  ?: "") }
    var birthDate by remember { mutableStateOf(profile?.birthDate ?: "") }
    var weight    by remember { mutableStateOf(
        if ((profile?.weightKg ?: 0f) > 0f) profile!!.weightKg.toString() else ""
    )}
    var showDatePicker by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance().apply { set(1980, 0, 1) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = cal.timeInMillis,
        yearRange = 1920..2010
    )
    val isValid = firstName.isNotBlank() && lastName.isNotBlank() &&
            birthDate.length == 10 && weight.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Üst gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Brush.linearGradient(listOf(NavyDark, NavyDark))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.kalbim_icon),
                            contentDescription = "Kalbim icon",
                            modifier = Modifier
                                .size(150.dp)
                                // shadow yardımıyla dışa doğru beyaz ışıma veriyoruz
                                .shadow(
                                    elevation = 35.dp,
                                    // Işımanın yayılma/büyüklük mesafesi
                                    shape = RoundedCornerShape(30),     // İkonunuz yuvarlaksa CircleShape, kareyse RoundedCornerShape(dp) yapın
                                    clip = false,                               // Işımanın kesilmemesi için false olmalı
                                    ambientColor = Color.White,                 // Yumuşak dış ışıma rengi (Beyaz)
                                    spotColor = Color.White                 // Keskin dış ışıma rengi (Beyaz)
                                )
                        )
                Spacer(Modifier.height(0.dp))
                Text(
                    stringResource(R.string.welcome),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Text(
                    stringResource(R.string.enter_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }


        // Form alanları
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Ad Soyad tek satır
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KalbimTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = stringResource(R.string.onboard_firstname),
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Person,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
                KalbimTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = stringResource(R.string.onboard_lastname),
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Person,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                        unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (birthDate.isBlank())
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(Icons.Filled.CalendarMonth, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text  = if (birthDate.isBlank()) stringResource(R.string.onboard_birth) else birthDate,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            KalbimTextField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.onboard_weight),
                placeholder = stringResource(R.string.onboard_weight),
                icon = Icons.Filled.MonitorWeight,
                keyboardType = KeyboardType.Decimal,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary, // Tema rengini otomatik alır
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor     = MaterialTheme.colorScheme.onSurface, // Metin rengini sabitlemeyin
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
            )
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.saveProfile(
                        UserProfile(
                            firstName = firstName.trim(),
                            lastName  = lastName.trim(),
                            birthDate = birthDate.trim(),
                            weightKg  = weight.toFloatOrNull() ?: 0f,
                            onboardingDone = true
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text(stringResource(R.string.save_continue),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White)
            }
            // DatePicker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val c = Calendar.getInstance().apply { timeInMillis = millis }
                                birthDate = "%02d.%02d.%04d".format(
                                    c.get(Calendar.DAY_OF_MONTH),
                                    c.get(Calendar.MONTH) + 1,
                                    c.get(Calendar.YEAR)
                                )
                            }
                            showDatePicker = false
                        }) { Text(stringResource(R.string.c_ok)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.c_cencel))
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = true
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                )
            )
            {
                Text(
                    text = stringResource(R.string.onboard_privacy),
                    modifier = Modifier.padding(22.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

    }
}

@Composable
fun KalbimTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = icon?.let { { Icon(it, null,
            tint = MaterialTheme.colorScheme.primary) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NavyPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        )
    )
}