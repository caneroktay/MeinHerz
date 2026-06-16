package com.kalbim.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val dosage: String = "",
    val morning: Int = 0,
    val noon: Int = 0,
    val evening: Int = 0,
    val night: Int = 0,
    // Her vakit için ayrı hatırlatma saati (-1 = kapalı)
    val morningReminderHour: Int = -1,
    val morningReminderMinute: Int = 0,
    val noonReminderHour: Int = -1,
    val noonReminderMinute: Int = 0,
    val eveningReminderHour: Int = -1,
    val eveningReminderMinute: Int = 0,
    val nightReminderHour: Int = -1,
    val nightReminderMinute: Int = 0,
    val startDate: String = "",
    val isActive: Boolean = true,
    val notes: String = ""
)