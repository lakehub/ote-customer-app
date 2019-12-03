package com.ote.otedeliveries.activities.startup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ote.otedeliveries.R
import com.ote.otedeliveries.app.AppPreferences
import com.ote.otedeliveries.retrofit.RetrofitFactory
import com.ote.otedeliveries.utils.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        etName.addTextChangedListener { text ->
            if (text.isNullOrBlank()) {
                inputLayoutName.showRequiredError()
            } else {
                inputLayoutName.hideError()
            }
        }

        etEmail.addTextChangedListener { text ->
            when {
                text.isNullOrBlank() -> inputLayoutEmail.showRequiredError()
                !isEmailValid(text.toString()) -> inputLayoutEmail.showError(getString(R.string.invalid_email))
                else -> inputLayoutEmail.hideError()
            }
        }

        etPassword.addTextChangedListener { text ->
            when {
                text.isNullOrBlank() -> inputLayoutPassword.showRequiredError()
                text.length < 8 -> inputLayoutPassword.showError(getString(R.string.password_short))
                else -> inputLayoutPassword.hideError()
            }
        }

        etPhone.addTextChangedListener { text ->
            if (text.toString() == "0") {
                etPhone.text = null
            }
        }

        ivBack.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phoneNo = etPhone.text.toString()
            val password = etPassword.text.toString()
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                if (name.isBlank())
                    inputLayoutName.showRequiredError()

                if (email.isBlank())
                    inputLayoutEmail.showRequiredError()

                if (password.isBlank())
                    inputLayoutPassword.showRequiredError()
            } else if (!isEmailValid(email)){
                inputLayoutEmail.showError(getString(R.string.invalid_email))
            } else if (password.length < 8) {
                inputLayoutPassword.showError(getString(R.string.password_short))
            } else if (phoneNo.isBlank()) {
                showWarning(getString(R.string.phone_no_required))
            } else if (phoneNo.length != 9) {
                showWarning(getString(R.string.invalid_phone))
            } else {
                createAccount(name, phoneNo, email, password)
            }
        }
    }

    private fun createAccount(name: String, phoneNo: String, email: String, password: String) {
        showProgress()
        val service = RetrofitFactory.makeRetrofitService()
        val param = createJsonRequestBody(
                Pair("name", name), Pair("email", email),
                Pair("phoneNo", phoneNo), Pair("password", password)
        )
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.signUpAsync(param)
            try {
                val body = request.await().body()
                val resCode = request.await().code()

                val errorBody = request.await().errorBody()
                if (resCode == 201) {
                    hideProgress()

                    if (body?.error != true) {
                        showSuccess(body!!.message!!)
                    } else {
                        showWarning(body.message!!)
                    }
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
        btnSubmit.makeGone()
    }

    private fun hideProgress() {
        progress.makeGone()
        btnSubmit.makeVisible()
    }
}
