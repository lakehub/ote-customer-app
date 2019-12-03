package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class GeometrySerializer {
    @SerializedName("location")
    val location: LocationSerializer? = null

    @SerializedName("locationType")
    val locationType: String? = null
}