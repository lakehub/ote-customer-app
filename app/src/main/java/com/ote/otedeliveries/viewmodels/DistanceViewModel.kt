package com.ote.otedeliveries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ote.otedeliveries.data.entities.Distance
import com.ote.otedeliveries.repos.DistanceRepo

class DistanceViewModel(application: Application): AndroidViewModel(application) {
    private val repo = DistanceRepo()

    fun getItem(): LiveData<Distance?> {
        return repo.getItem()
    }

}