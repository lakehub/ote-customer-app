package com.ote.otedeliveries.services

import android.util.Log
import com.ote.otedeliveries.network_requests.sendToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ote.otedeliveries.app.AppPreferences

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        AppPreferences.firstTimeTokenSent = false
        AppPreferences.deviceToken = token
        Log.e("TAG", "newToken")
        if (!token.isNotBlank() && AppPreferences.loggedIn)
            sendToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (AppPreferences.loggedIn) {

        }
    }
}
