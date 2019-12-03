package com.ote.otedeliveries.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ote.otedeliveries.data.entities.Load

@Dao
interface LoadDao: BaseDao<Load> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Load)

    @Update
    fun updateItem(item: Load)

    @Query("SELECT * FROM loads WHERE id = 0")
    fun getItem(): LiveData<Load?>
}