package com.kalbim.data.repository

import com.kalbim.data.db.KalbimDao
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import com.kalbim.data.model.Medication

class KalbimRepository(private val dao: KalbimDao) {

    fun getProfile(): Flow<UserProfile?> = dao.getProfile()
    suspend fun saveProfile(profile: UserProfile) = dao.saveProfile(profile)

    suspend fun addMeasurement(m: Measurement) = dao.insertMeasurement(m)
    suspend fun updateMeasurement(m: Measurement) = dao.updateMeasurement(m)
    suspend fun deleteMeasurement(m: Measurement) = dao.deleteMeasurement(m)
    fun getAllMeasurements(): Flow<List<Measurement>> = dao.getAllMeasurements()
    fun getLast30(): Flow<List<Measurement>> = dao.getLast30()
    fun getMeasurementsByRange(from: Long, to: Long) =
        dao.getMeasurementsByRange(from, to)
    // ── İlaçlar ──────────────────────────────────────────
    fun getAllMedications() = dao.getAllMedications()
    fun getActiveMedications() = dao.getActiveMedications()
    suspend fun insertMedication(m: Medication) = dao.insertMedication(m)
    suspend fun updateMedication(m: Medication) = dao.updateMedication(m)
    suspend fun deleteMedication(m: Medication) = dao.deleteMedication(m)
}