package com.ote.otedeliveries.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                "CREATE TABLE distance (id INTEGER NOT NULL, distance BIGINT NOT NULL, PRIMARY KEY(id))"
        )
    }
}