package com.ote.otedeliveries.activities.startup

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.ote.otedeliveries.R
import com.ote.otedeliveries.activities.maps.PermissionActivity
import com.ote.otedeliveries.app.AppPreferences
import com.ote.otedeliveries.retrofit.RetrofitFactory
import com.ote.otedeliveries.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled)
        )

        val colors = intArrayOf(
                ContextCompat.getColor(this, R.color.colorFb)
        )
        val colorList = ColorStateList(states, colors)

        btnFb.backgroundTintList = colorList

        etUsername.addTextChangedListener { text ->
            if (text?.isBlank() == true) {
                inputLayoutUsername.showRequiredError()
            } else {
                inputLayoutUsername.hideError()
            }
        }

        etPassword.addTextChangedListener { text ->
            if (text?.isBlank() == true) {
                inputLayoutPassword.showRequiredError()
            } else {
                inputLayoutPassword.hideError()
            }
        }

        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString()
            val pwd = etPassword.text.toString()
            if (username.isBlank() || pwd.isBlank()) {
                if (username.isBlank())
                    inputLayoutUsername.showRequiredError()

                if (pwd.isBlank())
                    inputLayoutPassword.showRequiredError()
            } else {
                login(username, pwd)
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun login(username: String, password: String) {
        showProgress()
        val credentials = "$username:$password"
        val auth = "Basic ${Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)}"
        val service = RetrofitFactory.makeRetrofitService()
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.signInAsync(auth)
            try {
                val body = request.await().body()
                val resCode = request.await().code()

                val errorBody = request.await().errorBody()
                if (resCode == 200) {
                    hideProgress()

                    if (body?.error != true) {
                        AppPreferences.loggedIn = true
                        AppPreferences.token = body!!.token
                        val details = body.details!!

                        AppPreferences.fullName = details.name
                        AppPreferences.email = details.email
                        AppPreferences.phoneNo = details.phoneNo
                        AppPreferences.imgUri = details.imageUri
                        finish()
                        startActivity(Intent(this@LoginActivity, PermissionActivity::class.java))
                    } else {
                        showWarning(body.message!!)
                    }

                    /*val teacherImgDownloadInfo = FileDownloadInfo(
                        "${Constants.PROFILE_IMAGE_URL}/${details.imageUri}",
                        details.imageUri,
                        Constants.DETAILS_DIRECTORY_NAME
                    )
                    DownloadImgAsyncTask().execute(teacherImgDownloadInfo)*/

                    /*if (body.role == ROLE_AGENT) {
                        startActivity(Intent(applicationContext, AgentDashboardActivity::class.java))
                    } else {

                    }*/
//                    finish()
                } else {
                    hideProgress()
                    if (errorBody != null) {
                        val errorData = JSONObject(errorBody.string())
                        showWarning(errorData.getString("message"))
                    }
                }

            } catch (e: Throwable) {
                Log.d("TAG", "error: ${e.message}")
                if (e is IOException) {
                    hideProgress()
                    showNetworkError()
                }
            }
        }
    }

    private fun showProgress() {
        progress.makeVisible()
        btnSignIn.makeGone()
        btnFb.makeGone()
    }

    private fun hideProgress() {
        progress.makeGone()
        btnSignIn.makeVisible()
        btnFb.makeVisible()
    }

}
