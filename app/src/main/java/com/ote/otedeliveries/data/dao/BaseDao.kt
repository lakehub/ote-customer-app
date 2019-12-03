package com.ote.otedeliveries.data.dao

import androidx.room.*

@Dao
interface BaseDao<in T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Delete
    fun delete(obj : T)

    @Update
    fun update(obj : T)
}