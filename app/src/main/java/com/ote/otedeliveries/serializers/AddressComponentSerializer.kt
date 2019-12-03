package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class AddressComponentSerializer {
    @SerializedName("long_name")
    val longName: String? = null

    @SerializedName("short_name")
    val shortName: String? = null

    @SerializedName("types")
    val types: List<String>? = null
}