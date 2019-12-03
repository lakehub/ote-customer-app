package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class LegSerializer {
    @SerializedName("start_location")
    var startLocation: LocationSerializer? = null

    @SerializedName("end_location")
    var endLocation: LocationSerializer? = null

    @SerializedName("duration")
    var duration: DurationSerializer? = null

    @SerializedName("distance")
    var distance: DistanceSerializer? = null

    @SerializedName("start_address")
    var startAddress: String? = null

    @SerializedName("end_address")
    var endAddress: String? = null

    @SerializedName("steps")
    var steps: List<StepSerializer>? = null
}