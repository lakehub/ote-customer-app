package com.ote.otedeliveries.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by osimeon on 11/11/2016.
 */

class ConnectionDetector private constructor(private val _context: Context) {
    init {
        mInstance = this
    }

    /**
     * Checking for all possible internet providers
     */
    val isConnectingToInternet: Boolean
        get() {
            val connectivity = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val info = connectivity.allNetworkInfo
            for (i in info.indices)
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            return false
        }

    companion object {
        private var mInstance: ConnectionDetector? = null
        fun getInstance(): ConnectionDetector {
            return mInstance!!
        }
    }
}