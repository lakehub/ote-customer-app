package com.ote.otedeliveries.repos

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ote.otedeliveries.data.dao.LoadDao
import com.ote.otedeliveries.data.database.MainDatabase
import com.ote.otedeliveries.data.entities.Load

class LoadRepo {
    private val db = MainDatabase.get()
    private val loadDao = db.loadDao

    fun getItem(): LiveData<Load?> {
        return loadDao.getItem()
    }

    fun addItem(item: Load) {
        InsertAsync(loadDao).execute(item)
    }

    fun deleteItem(item: Load) {
        DeleteAsyncTask(loadDao).execute(item)
    }

    companion object {
        private class InsertAsync internal constructor(private val mDao: LoadDao) :
                AsyncTask<Load, Void, Void>() {
            override fun doInBackground(vararg params: Load): Void? {
                mDao.addItem(params[0])
                return null
            }

        }

        private class DeleteAsyncTask internal constructor(private val mDao: LoadDao) :
                AsyncTask<Load, Void, Void>() {

            override fun doInBackground(vararg params: Load): Void? {
                mDao.delete(params[0])
                return null
            }
        }
    }
}