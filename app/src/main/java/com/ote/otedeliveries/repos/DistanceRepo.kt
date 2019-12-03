package com.ote.otedeliveries.repos

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ote.otedeliveries.data.dao.DistanceDao
import com.ote.otedeliveries.data.dao.LoadDao
import com.ote.otedeliveries.data.database.MainDatabase
import com.ote.otedeliveries.data.entities.Distance
import com.ote.otedeliveries.data.entities.Load

class DistanceRepo {
    private val db = MainDatabase.get()
    private val distanceDao = db.distanceDao

    fun getItem(): LiveData<Distance?> {
        return distanceDao.getItem()
    }

    fun addItem(distance: Distance) {
        InsertAsync(distanceDao).execute(distance)
    }

    fun deleteItem(distance: Distance) {
        DeleteAsyncTask(distanceDao).execute(distance)
    }

    companion object {
        private class InsertAsync internal constructor(private val mDao: DistanceDao) :
                AsyncTask<Distance, Void, Void>() {
            override fun doInBackground(vararg params: Distance): Void? {
                mDao.addItem(params[0])
                return null
            }

        }

        private class DeleteAsyncTask internal constructor(private val mDao: DistanceDao) :
                AsyncTask<Distance, Void, Void>() {

            override fun doInBackground(vararg params: Distance): Void? {
                mDao.delete(params[0])
                return null
            }
        }
    }
}