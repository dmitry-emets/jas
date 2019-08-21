package com.demets.jas.db.room

import android.content.Context
import androidx.room.*
import com.demets.jas.db.Converters

@Database(entities = [TrackEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {

        private const val databaseName: String = "mbdb.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context)
                }
                INSTANCE!!
            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}