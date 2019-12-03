package com.ote.otedeliveries.callbacks

import com.google.android.libraries.places.api.model.AutocompletePrediction

interface AutoCompletePlaceCallback {
    fun onClickCallback(item: AutocompletePrediction)
}