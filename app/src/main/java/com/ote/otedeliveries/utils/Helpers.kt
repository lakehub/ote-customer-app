package com.ote.otedeliveries.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.ote.otedeliveries.R
import com.ote.otedeliveries.app.AppPreferences
import com.ote.otedeliveries.app.MainApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.PeriodFormatterBuilder
import org.json.JSONObject
import java.io.*
import java.text.NumberFormat
import java.util.*

fun limitStringLength(value: String, length: Int): String {

    val buf = StringBuilder(value)
    if (buf.length > length) {
        buf.setLength(length)
        buf.append("...")
    }

    return buf.toString()
}

fun formatDate(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    val desiredFormat = "dd MMM yyyy"

    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}

fun dateTime(dateString: String): DateTime {
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    return fmt.parseDateTime(dateString)

}

fun formatDateShort(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    val desiredFormat = "HH:mm, dd/MMM/yyyy"

    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}

fun formatDateLong(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    val desiredFormat = "EEEE, dd MMM yyyy"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}

fun formatTime(dateString: String): String {
    val format = "yyyy-MM-dd HH:mm:ss"
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    val desiredFormat = "hh:mm a"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}

fun formatTimeAlt(dateString: String): String {
    val format = "yyyy-MM-dd HH:mm:ss"
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    val desiredFormat = "HH:mm"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}

fun dateMillis(dateString: String): Long {
    val format = "yyyy-MM-dd HH:mm:ss"
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
    return mDate.millis

}

fun saveImgToInternalStorage(bitmapImg: Bitmap, imageName: String?, directoryName: String) {
    val contextWrapper =
            ContextWrapper(MainApplication.applicationContext())
    val directory: File = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE)
    val path = File(directory, imageName!!)
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(path)
        bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fileOutputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//            return directory.absolutePath
}

fun emptyDirectory(directoryName: String) {
    val contextWrapper =
            ContextWrapper(MainApplication.applicationContext())
    val directory: File = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE)
    val children: Array<String> = directory.list()!!

    children.forEach {
        File(directory, it).delete()
    }
}

fun deleteFile(directoryName: String, fileName: String) {
    val contextWrapper =
            ContextWrapper(MainApplication.applicationContext())
    val directory: File = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE)

    try {
        File(directory, fileName).delete()
    } catch (e: FileNotFoundException) {
    }
}

fun loadImgFromInternalStorage(path: String, name: String): Bitmap? {
    try {
        val file = File(path, name)
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        return BitmapFactory.decodeStream(FileInputStream(file))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: OutOfMemoryError) {
    }

    return null
}

fun clearData() {
    AppPreferences.loggedIn = false
    AppPreferences.token = null
    AppPreferences.fullName = null
    AppPreferences.phoneNo = null
    AppPreferences.imgUri = null
    AppPreferences.email = null
}

fun titleCase(str: String): String {
    val strArr = str.split(" ").toTypedArray()
    for (i in strArr.indices) {
        strArr[i] = strArr[i].capitalize()
    }
    return strArr.joinToString(" ")
}

fun createJsonRequestBody(vararg params: Pair<String, Any>): RequestBody =
        JSONObject(mapOf(*params)).toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

/*fun formatDateTimeAgo(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val now = DateTime.now()
    val offset = jodaTz.getOffset(instant)
//            val defTz = DateTimeZone.getDefault()
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    val desTimeFormat = "HH:mm"
    val desTimeFormatter = DateTimeFormat.forPattern(desTimeFormat)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
//            val endOfTheDayToday = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
    val startOfTheDayToday = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
    val startOfTheDayTomorrow = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(1)
    val diffHr = (mDate.millis - startOfTheDayToday.millis) / (1000 * 60 * 60)
    val diffHrTomorrow = (startOfTheDayTomorrow.millis - mDate.millis) / (1000 * 60 * 60)

    if (diffHr in 1..23) {
        return desTimeFormatter.print(mDate)
    } else if (diffHrTomorrow in 24..47) {
        return "${MainApplication.applicationContext().getString(R.string.yesterday)}, ${desTimeFormatter.print(
                mDate
        )}"
    }

    val desiredFormat = "HH:mm, dd/MMM/yyyy"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}*/

/*fun formatDateAgo(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val now = DateTime.now()
    val offset = jodaTz.getOffset(instant)
//            val defTz = DateTimeZone.getDefault()
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
//            val endOfTheDayToday = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
    val startOfTheDayToday = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
    val startOfTheDayTomorrow = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(1)
    val diffHr = (mDate.millis - startOfTheDayToday.millis) / (1000 * 60 * 60)
    val diffHrTomorrow = (startOfTheDayTomorrow.millis - mDate.millis) / (1000 * 60 * 60)

    if (diffHr in 1..23) {
        return MainApplication.applicationContext().getString(R.string.today)
    } else if (diffHrTomorrow in 24..47) {
        return MainApplication.applicationContext().getString(R.string.yesterday)
    }

    val desiredFormat = "dd MMM yyyy"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}*/

fun currentUtcDateTime(): String {
    val timeZone = DateTimeZone.UTC
    val date = DateTime.now(timeZone)
    val strFormat = "yyyy-MM-dd HH:mm:ss"
    val format: DateTimeFormatter = DateTimeFormat.forPattern(strFormat)
    return format.print(date)
}

fun utcMillisToTimeString(millis: Long): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val offset = jodaTz.getOffset(instant)
    var date = DateTime(millis)
    date = date.plusMillis(offset)
    val desiredFormat = "hh:mm a"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(date)
}

/*fun formatLatestChatsDate(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val now = DateTime.now()
    val offset = jodaTz.getOffset(instant)
//            val defTz = DateTimeZone.getDefault()
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
//            val endOfTheDayToday = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
    val startOfTheDayToday = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
    val startOfTheDayTomorrow = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(1)
    val diffHr = (mDate.millis - startOfTheDayToday.millis) / (1000 * 60 * 60)
    val diffHrTomorrow = (startOfTheDayTomorrow.millis - mDate.millis) / (1000 * 60 * 60)
    val desiredTimeFormat = "HH:mm"
    val desTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(desiredTimeFormat)

    if (diffHr in 1..23) {
        return desTimeFormatter.print(mDate)
    } else if (diffHrTomorrow in 24..47) {
        return MainApplication.applicationContext().getString(R.string.yesterday)
    }

    val desiredFormat = "dd/MM/yyyy"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}*/

fun getCircleBitmap(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)

    val color = Color.RED
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawOval(rectF, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    bitmap.recycle()

    return output
}

/*fun formatPostDate(dateString: String): String {
    val tz = TimeZone.getDefault()
    val tzId = tz.id
    val jodaTz = DateTimeZone.forID(tzId)
    val instant = DateTime.now().millis
    val now = DateTime.now()
    val offset = jodaTz.getOffset(instant)
//            val defTz = DateTimeZone.getDefault()
    val format = "yyyy-MM-dd HH:mm:ss"
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)
    var mDate: DateTime = fmt.parseDateTime(dateString)
    mDate = mDate.plusMillis(offset)
//            val endOfTheDayToday = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
    val startOfTheDayToday = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
    val startOfTheDayTomorrow = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(1)
    val diffHr = (mDate.millis - startOfTheDayToday.millis) / (1000 * 60 * 60)
    val diffHrTomorrow = (startOfTheDayTomorrow.millis - mDate.millis) / (1000 * 60 * 60)
    val desiredTimeFormat = "HH:mm"
    val desTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(desiredTimeFormat)

    if (diffHr in 1..23) {
//                return desTimeFormatter.print(mDate)
        val period = Period(mDate, now)
        val builder = PeriodFormatterBuilder()
        when {
            period.hours > 1 -> builder.appendHours()
                    .appendSuffix(
                            " ${MainApplication.applicationContext()
                                    .getString(R.string.hr_ago)}"
                    )
            period.minutes > 1 -> builder.appendMinutes()
                    .appendSuffix(
                            " ${MainApplication.applicationContext()
                                    .getString(R.string.min_ago)}"
                    )
            period.seconds in 0..59 ->
                return MainApplication.applicationContext()
                        .getString(R.string.now)
        }

        val periodFormatter = builder.printZeroNever().toFormatter()
        return periodFormatter.print(period)
    } else if (diffHrTomorrow in 24..47) {
        return MainApplication.applicationContext().getString(
                R.string.yesterday_at,
                desTimeFormatter.print(mDate)
        )
    }

    val desiredFormat = "dd/MM/yyyy"
    val desFmt: DateTimeFormatter = DateTimeFormat.forPattern(desiredFormat)
    return desFmt.print(mDate)

}*/

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

open class NoPaddingArrayAdapter<T>(context: Context, layoutId: Int, items: List<T>) : ArrayAdapter<T>(context, layoutId, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val tv = view as TextView
        tv.setPadding(0, view.paddingTop, 0, view.paddingBottom)
        tv.isSingleLine = false
        return tv
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun getBitmapFromView(view: View, activity: AppCompatActivity, callback: (Bitmap) -> Unit) {
    activity.window?.let { window ->
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val locationOfViewInWindow = IntArray(2)
        view.getLocationInWindow(locationOfViewInWindow)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopy.request(window, Rect(locationOfViewInWindow[0], locationOfViewInWindow[1], locationOfViewInWindow[0] + view.width, locationOfViewInWindow[1] + view.height), bitmap, { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback(bitmap)
                    }
                    // possible to handle other result codes ...
                }, Handler())
            }
        } catch (e: IllegalArgumentException) {
            // PixelCopy may throw IllegalArgumentException, make sure to handle it
            e.printStackTrace()
        }
    }
}

fun getBitmapFromView( view: View): Bitmap {
    //Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap (view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable = view.getBackground ()
    if (bgDrawable != null)
    //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
    else
    //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    // draw the view on the canvas
    view.draw(canvas);
    //return the bitmap
    return returnedBitmap
}

fun decodePoly(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
        poly.add(p)
    }

    return poly
}

fun currencyFormat(value: Int): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "ke")).format(value)
}