package com.ote.otedeliveries.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ote.otedeliveries.data.entities.Distance

@Dao
interface DistanceDao: BaseDao<Distance> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Distance)

    @Update
    fun updateItem(item: Distance)

    @Query("SELECT * FROM distance WHERE id = 0")
    fun getItem(): LiveData<Distance?>
}