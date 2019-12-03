package com.ote.otedeliveries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ote.otedeliveries.data.entities.Load
import com.ote.otedeliveries.repos.LoadRepo

class LoadViewModel(application: Application): AndroidViewModel(application) {
    private val repo = LoadRepo()

    fun getItem(): LiveData<Load?> {
        return repo.getItem()
    }

}