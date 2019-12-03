package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class RouteSerializer {
    @SerializedName("legs")
    var legs: List<LegSerializer>? = null

    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolylineSerializer? = null
}