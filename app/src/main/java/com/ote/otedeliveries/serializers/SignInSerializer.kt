package com.ote.otedeliveries.serializers

import com.google.gson.annotations.SerializedName

class SignInSerializer {
    @SerializedName("error")
    var error: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("token")
    val token: String? = null

    @SerializedName("details")
    val details: UserDetailsSerializer? = null
}