package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class AddressComponentsSerializer {
    @SerializedName("address_components")
    val addressComponents: List<AddressComponentSerializer>? = null

    @SerializedName("formatted_address")
    val formattedAddress: String? = null

    @SerializedName("geometry")
    val geometry: GeometrySerializer? = null
}