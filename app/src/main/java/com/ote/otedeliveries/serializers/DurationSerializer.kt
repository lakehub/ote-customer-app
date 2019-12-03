package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class DurationSerializer {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("value")
    var value: Long? = null
}