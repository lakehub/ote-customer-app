package com.ote.otedeliveries.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loads")
data class Load(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @ColumnInfo(name = "size")
        var size: Int = 1
)