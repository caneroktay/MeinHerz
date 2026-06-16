package com.kalbim.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateLabel: String = "",
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val pulse: Int? = null,
    val weightKg: Float? = null,
    val ntProBnp: Float? = null,
    val troponin: Float? = null,
    val notes: String = ""
)