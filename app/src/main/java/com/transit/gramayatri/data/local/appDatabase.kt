package com.transit.gramayatri.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [cachedPingEntity::class], version = 1, exportSchema = false)
abstract class appDatabase : RoomDatabase() {

    abstract fun pingDao(): pingDao

    companion object {
        @Volatile
        private var INSTANCE: appDatabase? = null

        fun getDatabase(context: Context): appDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    appDatabase::class.java,
                    "gramayatri_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}