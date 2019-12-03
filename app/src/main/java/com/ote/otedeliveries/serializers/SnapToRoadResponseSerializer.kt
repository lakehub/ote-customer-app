package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class SnapToRoadResponseSerializer {
    @SerializedName("snappedPoints")
    var snappedPoints: List<SnappedPointSerializer>?  = null
}