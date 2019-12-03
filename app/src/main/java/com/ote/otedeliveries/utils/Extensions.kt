package com.ote.otedeliveries.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.graphics.drawable.ColorDrawable
import android.view.ViewTreeObserver
import com.ote.otedeliveries.R
import com.ote.otedeliveries.app.MainApplication


fun AppCompatActivity.showToast(view: View) {
    val toast = Toast(this)
    toast.view = view
    toast.setGravity(Gravity.BOTTOM, 0, 30)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
}

fun AppCompatActivity.showWarning(message: String) {
    val view = layoutInflater.inflate(R.layout.warning_toast, null)
    val textView: TextView = view.findViewById(R.id.message)
    textView.text = message
    this.showToast(view)
}

fun AppCompatActivity.showNetworkError() {
    val view = layoutInflater.inflate(R.layout.network_error_toast, null)
    showToast(view)
}

fun AppCompatActivity.showSuccess(message: String) {
    val view = layoutInflater.inflate(R.layout.normal_toast, null)
    val textView: TextView = view.findViewById(R.id.message)
    textView.text = message
    this.showToast(view)
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun dpToPx(dp: Int, context: Context): Int =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                context.resources.displayMetrics
        ).toInt()

fun spToPx(sp: Int, context: Context): Float =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp.toFloat(),
                context.resources.displayMetrics
        )

fun SwipeRefreshLayout.colorSchemePrimary() {
    setColorSchemeColors(ContextCompat.getColor(MainApplication.applicationContext(), R.color.colorPrimary))
}

fun TextInputLayout.markRequired() {
    hint = "$hint *"
}

fun TextInputEditText.markRequired() {
    hint = "$hint *"
}

fun TextView.markRequired() {
    text = context.getString(R.string.required)
}

fun TextInputLayout.showError(text: String) {
    error = text
}

fun TextInputLayout.hideError() {
    error = null
}

fun TextInputLayout.showRequiredError() {
    error = context.getString(R.string.required)
}

fun TextView.setSize(size: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

fun AlertDialog.setGravity(gravity: Int) {
    val wlp = window?.attributes

    wlp?.gravity = gravity
    wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
    window?.attributes = wlp
}

fun AlertDialog.fullWidth() {
    val lp = WindowManager.LayoutParams()
    window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    lp.copyFrom(window?.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    window?.attributes = lp
}

fun AlertDialog.fillParent() {
    val lp = WindowManager.LayoutParams()
    window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    lp.copyFrom(window?.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = context.resources.displayMetrics.heightPixels
    window?.attributes = lp
}

fun <T : View> T.viewHeight(function: (Int) -> Unit) {
    if (height == 0)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                function(height)
            }
        })
    else function(height)
}

fun View.slideDown(height: Int) {
    animate().translationY(height.toFloat())
            .setDuration(500)
            .setListener(null)
}

fun View.slideUp() {
    animate().translationY(0f)
            .setDuration(500)
            .setListener(null)
}
