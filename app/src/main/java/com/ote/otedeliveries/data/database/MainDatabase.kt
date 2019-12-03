package com.ote.otedeliveries.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ote.otedeliveries.app.MainApplication
import com.ote.otedeliveries.data.dao.DistanceDao
import com.ote.otedeliveries.data.dao.LoadDao
import com.ote.otedeliveries.data.entities.Distance
import com.ote.otedeliveries.data.entities.Load

@Database(
        entities = [Load::class, Distance::class],
        version = 2, exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract val loadDao: LoadDao
    abstract val distanceDao: DistanceDao

    companion object {
        private var INSTANCE: MainDatabase? = null

        fun get(): MainDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(
                                MainApplication.applicationContext(),
                                MainDatabase::class.java,
                                "main_db"
                        )
                        .addMigrations(MIGRATION_1_2)
                        .build()
            }

            return INSTANCE!!
        }
    }

}
