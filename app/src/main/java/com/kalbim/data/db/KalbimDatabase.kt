package com.kalbim.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.UserProfile
import com.kalbim.data.model.Medication

@Database(
    entities = [UserProfile::class, Measurement::class, Medication::class],
    version = 4,
    exportSchema = false
)
abstract class KalbimDatabase : RoomDatabase() {

    abstract fun dao(): KalbimDao

    companion object {
        @Volatile private var INSTANCE: KalbimDatabase? = null

        fun getInstance(context: Context): KalbimDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    KalbimDatabase::class.java,
                    "kalbim_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}