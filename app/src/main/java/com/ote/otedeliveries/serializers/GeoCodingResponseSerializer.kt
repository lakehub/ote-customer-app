package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class GeoCodingResponseSerializer {
    @SerializedName("results")
    val results: List<AddressComponentsSerializer>? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("error_message")
    val errorMessage: String? = null
}