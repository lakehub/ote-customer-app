package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class SnappedPointSerializer {
    @SerializedName("location")
    var location: LocationAltSerializer? = null

    @SerializedName("placeId")
    var placeId: String? = null

    @SerializedName("originalIndex")
    var originalIndex: Int? = null
}