package com.ote.otedeliveries.network_requests

import com.ote.otedeliveries.app.AppPreferences
import com.ote.otedeliveries.retrofit.RetrofitFactory
import com.ote.otedeliveries.utils.createJsonRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun sendToken(token: String?) {
    val auth = "Bearer ${AppPreferences.token}"
    val service = RetrofitFactory.makeRetrofitService()
    val params = createJsonRequestBody(Pair("deviceId", token!!))
    GlobalScope.launch(Dispatchers.Main) {
        val request = service.sendDeviceIdAsync(auth, params)
        try {
            val response = request.await()
            val body = response.body()
            val resCode = response.code()
            if (resCode == 200) {
                if (!body?.error!!)
                    AppPreferences.firstTimeTokenSent = true
            } else {
                AppPreferences.firstTimeTokenSent = false
            }

        } catch (e: Throwable) {
            AppPreferences.firstTimeTokenSent = false
        }
    }
}