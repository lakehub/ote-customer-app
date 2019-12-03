package com.ote.otedeliveries.viewmodel_factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ote.otedeliveries.viewmodels.DistanceViewModel

class DistanceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistanceViewModel::class.java)) {
            return DistanceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}