package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class DirectionsResponseSerializer {
    @SerializedName("routes")
    var routes: List<RouteSerializer>? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("error_message")
    val errorMessage: String? = null
}