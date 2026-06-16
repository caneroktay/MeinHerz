package com.kalbim.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kalbim.data.db.KalbimDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val dao     = KalbimDatabase.getInstance(context).dao()
            val profile = dao.getProfile().first()
            val meds    = dao.getActiveMedications().first()

            // Ölçüm hatırlatıcıları
            ReminderScheduler.scheduleAll(context, profile)

            // İlaç hatırlatıcıları
            MedicationReminderScheduler.scheduleAll(context, meds)
        }
    }
}