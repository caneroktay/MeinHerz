package com.kalbim.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val type    = inputData.getString("type")    ?: "morning"
        val medName = inputData.getString("medName") ?: ""

        val (title, body) = when {
            medName.isNotBlank() -> Pair(
                "💊 İlaç Hatırlatıcısı",
                "$medName almayı unutmayın!"
            )
            type == "morning" -> Pair(
                "🌅 Sabah Ölçümü",
                "Günaydın! Kilonuzu girmeyi unutmayın."
            )
            type == "noon" -> Pair(
                "☀️ Öğle Ölçümü",
                "Tansiyon ve nabzınızı girdiniz mi?"
            )
            else -> Pair(
                "🌙 Akşam Ölçümü",
                "Akşam tansiyon ölçümünüzü girmeyi unutmayın."
            )
        }

        NotificationHelper.sendNotification(
            applicationContext, title, body, "$type$medName".hashCode()
        )
        return Result.success()
    }
}