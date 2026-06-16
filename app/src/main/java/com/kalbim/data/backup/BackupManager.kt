package com.kalbim.data.backup

import android.content.Context
import android.net.Uri
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.Medication
import com.kalbim.data.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object BackupManager {

    // ── DIŞA AKTAR ─────────────────────────────────────────
    fun exportToJson(
        profile: UserProfile?,
        measurements: List<Measurement>,
        medications: List<Medication>
    ): String {
        val root = JSONObject()

        // Versiyon ve tarih
        root.put("version", 1)
        root.put("exportDate",
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()))

        // Profil
        profile?.let { p ->
            val profileJson = JSONObject().apply {
                put("firstName",        p.firstName)
                put("lastName",         p.lastName)
                put("birthDate",        p.birthDate)
                put("weightKg",         p.weightKg)
                put("medicalHistory",   p.medicalHistory)
                put("language",         p.language)
                put("themeMode",        p.themeMode)
                put("notifMorningHour", p.notifMorningHour)
                put("notifMorningMin",  p.notifMorningMin)
                put("notifNoonHour",    p.notifNoonHour)
                put("notifNoonMin",     p.notifNoonMin)
                put("notifEveningHour", p.notifEveningHour)
                put("notifEveningMin",  p.notifEveningMin)
            }
            root.put("profile", profileJson)
        }

        // Ölçümler
        val measurementsArray = JSONArray()
        measurements.forEach { m ->
            val obj = JSONObject().apply {
                put("timestamp",  m.timestamp)
                put("dateLabel",  m.dateLabel)
                put("systolic",   m.systolic  ?: JSONObject.NULL)
                put("diastolic",  m.diastolic ?: JSONObject.NULL)
                put("pulse",      m.pulse     ?: JSONObject.NULL)
                put("weightKg",   m.weightKg  ?: JSONObject.NULL)
                put("ntProBnp",   m.ntProBnp  ?: JSONObject.NULL)
                put("troponin",   m.troponin  ?: JSONObject.NULL)
                put("notes",      m.notes)
            }
            measurementsArray.put(obj)
        }
        root.put("measurements", measurementsArray)

        // İlaçlar
        val medicationsArray = JSONArray()
        medications.forEach { med ->
            val obj = JSONObject().apply {
                put("name",                   med.name)
                put("dosage",                 med.dosage)
                put("morning",                med.morning)
                put("noon",                   med.noon)
                put("evening",                med.evening)
                put("night",                  med.night)
                put("morningReminderHour",    med.morningReminderHour)
                put("morningReminderMinute",  med.morningReminderMinute)
                put("noonReminderHour",       med.noonReminderHour)
                put("noonReminderMinute",     med.noonReminderMinute)
                put("eveningReminderHour",    med.eveningReminderHour)
                put("eveningReminderMinute",  med.eveningReminderMinute)
                put("nightReminderHour",      med.nightReminderHour)
                put("nightReminderMinute",    med.nightReminderMinute)
                put("startDate",              med.startDate)
                put("isActive",               med.isActive)
                put("notes",                  med.notes)
            }
            medicationsArray.put(obj)
        }
        root.put("medications", medicationsArray)

        return root.toString(2)
    }

    // ── İÇE AKTAR ──────────────────────────────────────────
    data class ImportResult(
        val profile: UserProfile?,
        val measurements: List<Measurement>,
        val medications: List<Medication>,
        val error: String? = null
    )

    fun importFromJson(jsonString: String): ImportResult {
        return try {
            val root = JSONObject(jsonString)

            // Profil
            val profile = if (root.has("profile")) {
                val p = root.getJSONObject("profile")
                UserProfile(
                    id            = 1,
                    firstName     = p.optString("firstName"),
                    lastName      = p.optString("lastName"),
                    birthDate     = p.optString("birthDate"),
                    weightKg      = p.optDouble("weightKg", 0.0).toFloat(),
                    medicalHistory = p.optString("medicalHistory"),
                    language      = p.optString("language", "tr"),
                    themeMode     = p.optString("themeMode", "auto"),
                    notifMorningHour = p.optInt("notifMorningHour", 7),
                    notifMorningMin  = p.optInt("notifMorningMin",  0),
                    notifNoonHour    = p.optInt("notifNoonHour",   12),
                    notifNoonMin     = p.optInt("notifNoonMin",     0),
                    notifEveningHour = p.optInt("notifEveningHour",20),
                    notifEveningMin  = p.optInt("notifEveningMin",  0),
                    onboardingDone   = true
                )
            } else null

            // Ölçümler
            val measurements = mutableListOf<Measurement>()
            if (root.has("measurements")) {
                val arr = root.getJSONArray("measurements")
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    measurements.add(Measurement(
                        timestamp  = m.optLong("timestamp", System.currentTimeMillis()),
                        dateLabel  = m.optString("dateLabel"),
                        systolic   = if (m.isNull("systolic"))  null else m.optInt("systolic"),
                        diastolic  = if (m.isNull("diastolic")) null else m.optInt("diastolic"),
                        pulse      = if (m.isNull("pulse"))     null else m.optInt("pulse"),
                        weightKg   = if (m.isNull("weightKg"))  null else m.optDouble("weightKg").toFloat(),
                        ntProBnp   = if (m.isNull("ntProBnp"))  null else m.optDouble("ntProBnp").toFloat(),
                        troponin   = if (m.isNull("troponin"))  null else m.optDouble("troponin").toFloat(),
                        notes      = m.optString("notes")
                    ))
                }
            }

            // İlaçlar
            val medications = mutableListOf<Medication>()
            if (root.has("medications")) {
                val arr = root.getJSONArray("medications")
                for (i in 0 until arr.length()) {
                    val med = arr.getJSONObject(i)
                    medications.add(Medication(
                        name                  = med.optString("name"),
                        dosage                = med.optString("dosage"),
                        morning               = med.optInt("morning"),
                        noon                  = med.optInt("noon"),
                        evening               = med.optInt("evening"),
                        night                 = med.optInt("night"),
                        morningReminderHour   = med.optInt("morningReminderHour",   -1),
                        morningReminderMinute = med.optInt("morningReminderMinute",  0),
                        noonReminderHour      = med.optInt("noonReminderHour",      -1),
                        noonReminderMinute    = med.optInt("noonReminderMinute",     0),
                        eveningReminderHour   = med.optInt("eveningReminderHour",   -1),
                        eveningReminderMinute = med.optInt("eveningReminderMinute",  0),
                        nightReminderHour     = med.optInt("nightReminderHour",     -1),
                        nightReminderMinute   = med.optInt("nightReminderMinute",    0),
                        startDate             = med.optString("startDate"),
                        isActive              = med.optBoolean("isActive", true),
                        notes                 = med.optString("notes")
                    ))
                }
            }

            ImportResult(profile, measurements, medications)
        } catch (e: Exception) {
            ImportResult(null, emptyList(), emptyList(), e.message)
        }
    }
}