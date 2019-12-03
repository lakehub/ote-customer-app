package com.ote.otedeliveries.app

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "OTE_DELIVERIES"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", true)
    private val IS_LOGGED_IN = Pair("is_logged_in", false)
    private val TOKEN = Pair("token", null)
    private val FULL_NAME = Pair("full_name", null)
    private val IMG_URI = Pair("imgUri", null)
    private val PHONE_NO = Pair("phone_no", null)
    private val EMAIL = Pair("email", null)
    private val DEVICE_TOKEN = Pair("device_token", null)
    private val FIRST_TIME_TOKEN_SENT = Pair("first_time_token_sent", false)
    private val APP_IN_FOREGROUND = Pair("app_in_foreground", false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

    var loggedIn: Boolean
        get() = preferences.getBoolean(IS_LOGGED_IN.first, IS_LOGGED_IN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGGED_IN.first, value)
        }

    var token: String?
        get() = preferences.getString(TOKEN.first, TOKEN.second)
        set(value) = preferences.edit {
            it.putString(TOKEN.first, value)
        }

    var fullName: String?
        get() = preferences.getString(FULL_NAME.first, FULL_NAME.second)
        set(value) = preferences.edit {
            it.putString(FULL_NAME.first, value)
        }

    var imgUri: String?
        get() = preferences.getString(IMG_URI.first, IMG_URI.second)
        set(value) = preferences.edit {
            it.putString(IMG_URI.first, value)
        }

    var phoneNo: String?
        get() = preferences.getString(PHONE_NO.first, PHONE_NO.second)
        set(value) = preferences.edit {
            it.putString(PHONE_NO.first, value)
        }

    var deviceToken: String?
        get() = preferences.getString(DEVICE_TOKEN.first, DEVICE_TOKEN.second)
        set(value) = preferences.edit {
            it.putString(DEVICE_TOKEN.first, value)
        }
    var firstTimeTokenSent: Boolean
        get() = preferences.getBoolean(FIRST_TIME_TOKEN_SENT.first, FIRST_TIME_TOKEN_SENT.second)
        set(value) = preferences.edit {
            it.putBoolean(FIRST_TIME_TOKEN_SENT.first, value)
        }

    var appInForeground: Boolean
        get() = preferences.getBoolean(APP_IN_FOREGROUND.first, APP_IN_FOREGROUND.second)
        set(value) = preferences.edit {
            it.putBoolean(APP_IN_FOREGROUND.first, value)
        }

    var email: String?
        get() = preferences.getString(EMAIL.first, EMAIL.second)
        set(value) = preferences.edit {
            it.putString(EMAIL.first, value)
        }
}