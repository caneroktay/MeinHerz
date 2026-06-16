package com.kalbim.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kalbim.R

class ReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val type    = inputData.getString("type")    ?: "morning"
        val medName = inputData.getString("medName") ?: ""

        val (title, body) = when {
            medName.isNotBlank() -> Pair(
                applicationContext.getString(R.string.notif_med_title),
                applicationContext.getString(R.string.notif_med_body, medName)
            )
            type == "morning" -> Pair(
                applicationContext.getString(R.string.notif_morning_title),
                applicationContext.getString(R.string.notif_morning_body)
            )
            type == "noon" -> Pair(
                applicationContext.getString(R.string.notif_noon_title),
                applicationContext.getString(R.string.notif_noon_body)
            )
            else -> Pair(
                applicationContext.getString(R.string.notif_evening_title),
                applicationContext.getString(R.string.notif_evening_body)
            )
        }

        NotificationHelper.sendNotification(
            applicationContext, title, body, "$type$medName".hashCode()
        )
        return Result.success()
    }
}