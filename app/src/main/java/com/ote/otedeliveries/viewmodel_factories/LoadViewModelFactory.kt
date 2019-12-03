package com.ote.otedeliveries.viewmodel_factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ote.otedeliveries.viewmodels.LoadViewModel

class LoadViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadViewModel::class.java)) {
            return LoadViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}