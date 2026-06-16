package com.kalbim.notification

import android.content.Context
import androidx.work.*
import com.kalbim.data.model.Medication
import java.util.Calendar
import java.util.concurrent.TimeUnit

object MedicationReminderScheduler {

    fun scheduleAll(context: Context, medications: List<Medication>) {
        // Önce tüm ilaç bildirimlerini iptal et
        WorkManager.getInstance(context).cancelAllWorkByTag("medication_reminder")

        // Sadece aktif ilaçlar için planla
        medications.filter { it.isActive }.forEach { med ->
            scheduleMedication(context, med)
        }
    }

    fun scheduleMedication(context: Context, med: Medication) {
        // Her vakit için ayrı ayrı planla
        if (med.morning > 0 && med.morningReminderHour >= 0) {
            scheduleOne(context, med, "morning",
                med.morningReminderHour, med.morningReminderMinute)
        }
        if (med.noon > 0 && med.noonReminderHour >= 0) {
            scheduleOne(context, med, "noon",
                med.noonReminderHour, med.noonReminderMinute)
        }
        if (med.evening > 0 && med.eveningReminderHour >= 0) {
            scheduleOne(context, med, "evening",
                med.eveningReminderHour, med.eveningReminderMinute)
        }
        if (med.night > 0 && med.nightReminderHour >= 0) {
            scheduleOne(context, med, "night",
                med.nightReminderHour, med.nightReminderMinute)
        }
    }

    fun cancelMedication(context: Context, med: Medication) {
        listOf("morning", "noon", "evening", "night").forEach { vakit ->
            WorkManager.getInstance(context)
                .cancelUniqueWork("med_${med.id}_$vakit")
        }
    }

    private fun scheduleOne(
        context: Context,
        med: Medication,
        vakit: String,
        hour: Int,
        minute: Int
    ) {
        val now    = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)

        val delay = target.timeInMillis - now.timeInMillis

        val data = Data.Builder()
            .putString("type",    vakit)
            .putString("medName", med.name)
            .build()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("medication_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "med_${med.id}_$vakit",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}