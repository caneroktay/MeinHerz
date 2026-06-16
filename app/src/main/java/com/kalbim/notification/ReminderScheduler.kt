package com.kalbim.notification

import android.content.Context
import androidx.work.*
import com.kalbim.data.model.UserProfile
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleAll(context: Context, profile: UserProfile? = null) {
        scheduleDaily(context, "morning",
            profile?.notifMorningHour ?: 7,
            profile?.notifMorningMin  ?: 0)
        scheduleDaily(context, "noon",
            profile?.notifNoonHour ?: 12,
            profile?.notifNoonMin  ?: 0)
        scheduleDaily(context, "evening",
            profile?.notifEveningHour ?: 20,
            profile?.notifEveningMin  ?: 0)
    }

    private fun scheduleDaily(
        context: Context,
        type: String,
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
        val data  = Data.Builder().putString("type", type).build()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("kalbim_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "kalbim_$type",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}