package com.ote.otedeliveries.activities.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ote.otedeliveries.R
import com.ote.otedeliveries.activities.maps.PermissionActivity
import com.ote.otedeliveries.app.AppPreferences

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        if (AppPreferences.loggedIn) {
            finish()
            startActivity(Intent(this, PermissionActivity::class.java))
        } else {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
