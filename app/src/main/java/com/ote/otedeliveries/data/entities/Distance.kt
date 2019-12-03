package com.ote.otedeliveries.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distance")
data class Distance(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        var id: Int = 0,
        @ColumnInfo(name = "distance")
        var distance: Long = 0L
)