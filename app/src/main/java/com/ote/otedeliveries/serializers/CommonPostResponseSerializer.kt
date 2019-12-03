package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class CommonPostResponseSerializer {
    @SerializedName("error")
    val error: Boolean? = null

    @SerializedName("message")
    val message: String? = null
}