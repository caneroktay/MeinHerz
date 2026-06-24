@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.kalbim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kalbim.data.db.KalbimDatabase
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.UserProfile
import com.kalbim.data.repository.KalbimRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.kalbim.data.model.Medication
import com.kalbim.notification.MedicationReminderScheduler
import com.kalbim.notification.LanguageManager
import com.kalbim.data.backup.BackupManager

class KalbimViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = KalbimRepository(KalbimDatabase.getInstance(app).dao())

    val profile: StateFlow<UserProfile?> = repo.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMeasurements: StateFlow<List<Measurement>> = repo.getAllMeasurements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val last30: StateFlow<List<Measurement>> = repo.getLast30()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── İlaçlar ──────────────────────────────────────────
    val allMedications: StateFlow<List<Medication>> = repo.getAllMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMedications: StateFlow<List<Medication>> = repo.getActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertMedication(m: Medication) = viewModelScope.launch {
        repo.insertMedication(m)
        MedicationReminderScheduler.scheduleMedication(getApplication(), m)
    }

    fun updateMedication(m: Medication) = viewModelScope.launch {
        repo.updateMedication(m)
        // Önce iptal et, sonra yeniden planla
        MedicationReminderScheduler.cancelMedication(getApplication(), m)
        if (m.isActive) {
            MedicationReminderScheduler.scheduleMedication(getApplication(), m)
        }
    }

    fun deleteMedication(m: Medication) = viewModelScope.launch {
        repo.deleteMedication(m)
        MedicationReminderScheduler.cancelMedication(getApplication(), m)
    }

    fun deactivateMedication(m: Medication, note: String) = viewModelScope.launch {
        val updated = m.copy(isActive = false, notes = note)
        repo.updateMedication(updated)
        MedicationReminderScheduler.cancelMedication(getApplication(), m)
    }
    // ── Yedekleme ─────────────────────────────────────────────
    suspend fun getBackupJson(): String {
        val p    = repo.getProfile().first()
        val m    = repo.getAllMeasurements().first()
        val meds = repo.getAllMedications().first()
        return BackupManager.exportToJson(p, m, meds)
    }

    fun restoreFromJson(
        jsonString: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        val result = BackupManager.importFromJson(jsonString)
        if (result.error != null) {
            onError(result.error)
            return@launch
        }
        // Önce mevcut verileri temizle
        repo.getAllMeasurements().first().forEach { repo.deleteMeasurement(it) }
        repo.getAllMedications().first().forEach { repo.deleteMedication(it) }

        // Yeni verileri kaydet
        result.profile?.let { repo.saveProfile(it) }
        result.measurements.forEach { repo.addMeasurement(it) }
        result.medications.forEach { repo.insertMedication(it) }

        onSuccess()
    }


    // Her ölçüm tipi için en son değeri ayrı ayrı getir
    val latestBp: StateFlow<Measurement?> = repo.getAllMeasurements()
        .map { list -> list.firstOrNull { it.systolic != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestWeight: StateFlow<Measurement?> = repo.getAllMeasurements()
        .map { list -> list.firstOrNull { it.weightKg != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestLab: StateFlow<Measurement?> = repo.getAllMeasurements()
        .map { list -> list.firstOrNull { it.ntProBnp != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestTroponin: StateFlow<Measurement?> = repo.getAllMeasurements()
        .map { list -> list.firstOrNull { it.troponin != null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _fromDate = MutableStateFlow(0L)
    private val _toDate = MutableStateFlow(System.currentTimeMillis())

    val filteredMeasurements: StateFlow<List<Measurement>> =
        combine(_fromDate, _toDate) { from, to -> Pair(from, to) }
            .flatMapLatest { (from, to) -> repo.getMeasurementsByRange(from, to) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDateRange(from: Long, to: Long) {
        _fromDate.value = from
        _toDate.value = to
    }

    fun saveProfile(profile: UserProfile) = viewModelScope.launch {
        repo.saveProfile(profile)
        LanguageManager.applyLanguage(profile.language)
        _languageChanged.value = true
    }

    private val _languageChanged = MutableStateFlow(false)
    val languageChanged: StateFlow<Boolean> = _languageChanged.asStateFlow()

    fun onLanguageChangeHandled() {
        _languageChanged.value = false
    }

    fun addMeasurement(
        systolic: Int? = null,
        diastolic: Int? = null,
        pulse: Int? = null,
        weightKg: Float? = null,
        ntProBnp: Float? = null,
        troponin: Float? = null,
        notes: String = ""
    ) {
        val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        viewModelScope.launch {
            repo.addMeasurement(
                Measurement(
                    dateLabel = fmt.format(Date()),
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    weightKg = weightKg,
                    ntProBnp = ntProBnp,
                    troponin = troponin,
                    notes = notes
                )
            )
        }
    }
    fun updateMeasurement(m: Measurement) = viewModelScope.launch {
        repo.updateMeasurement(m)
    }

    fun deleteMeasurement(m: Measurement) = viewModelScope.launch {
        repo.deleteMeasurement(m)
    }
}