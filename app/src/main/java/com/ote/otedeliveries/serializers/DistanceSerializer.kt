package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class DistanceSerializer {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("value")
    var value: Long? = null
}