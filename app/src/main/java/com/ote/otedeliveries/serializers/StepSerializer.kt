package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class StepSerializer {
    @SerializedName("start_location")
    var startLocation: LocationSerializer? = null

    @SerializedName("end_location")
    var endLocation: LocationSerializer? = null

    @SerializedName("duration")
    var duration: DurationSerializer? = null

    @SerializedName("distance")
    var distance: DistanceSerializer? = null

    @SerializedName("polyline")
    var polyline: PolylineSerializer? = null
}