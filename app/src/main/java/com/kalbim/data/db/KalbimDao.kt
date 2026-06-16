package com.kalbim.data.db

import androidx.room.*
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import com.kalbim.data.model.Medication

@Dao
interface KalbimDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(m: Measurement): Long

    @Update
    suspend fun updateMeasurement(m: Measurement)

    @Delete
    suspend fun deleteMeasurement(m: Measurement)

    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<Measurement>>

    @Query("""
        SELECT * FROM measurements 
        WHERE timestamp BETWEEN :from AND :to 
        ORDER BY timestamp DESC
    """)
    fun getMeasurementsByRange(from: Long, to: Long): Flow<List<Measurement>>

    @Query("SELECT * FROM measurements ORDER BY timestamp DESC LIMIT 30")
    fun getLast30(): Flow<List<Measurement>>

    // ── İlaçlar ──────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(m: Medication): Long

    @Update
    suspend fun updateMedication(m: Medication)

    @Delete
    suspend fun deleteMedication(m: Medication)

    @Query("SELECT * FROM medications ORDER BY isActive DESC, name ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveMedications(): Flow<List<Medication>>
}