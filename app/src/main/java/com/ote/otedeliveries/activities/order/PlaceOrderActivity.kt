package com.ote.otedeliveries.activities.order

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ote.otedeliveries.R
import com.ote.otedeliveries.utils.*
import com.ote.otedeliveries.viewmodel_factories.DistanceViewModelFactory
import com.ote.otedeliveries.viewmodel_factories.LoadViewModelFactory
import com.ote.otedeliveries.viewmodels.DistanceViewModel
import com.ote.otedeliveries.viewmodels.LoadViewModel
import kotlinx.android.synthetic.main.activity_place_order.*
import kotlinx.android.synthetic.main.content_place_order.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

class PlaceOrderActivity : AppCompatActivity() {
    private lateinit var loadViewModel: LoadViewModel
    private lateinit var distanceViewModel: DistanceViewModel
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        loadViewModel = ViewModelProvider(
                this,
                LoadViewModelFactory(Application())
        ).get(LoadViewModel::class.java)

        distanceViewModel = ViewModelProvider(
                this,
                DistanceViewModelFactory(Application())
        ).get(DistanceViewModel::class.java)

        distanceViewModel.getItem().observe(this, Observer { distanceModel ->
            if (distanceModel != null) {
                val distance = distanceModel.distance
                loadViewModel.getItem().observe(this, Observer {
                    if (it != null) {
                        when {
                            it.size == LOAD_PICKUP -> {
                                tvPrice.text = currencyFormat(distance.div(100).times(PICKUP_PRICE).toInt())
                                tvLoad.text = getString(R.string.pickup)
                                icLoad.setImageResource(R.drawable.ic_pickup)
                            }
                            it.size == LOAD_TUK_TUK -> {
                                tvPrice.text = currencyFormat(distance.div(100).times(TUK_TUK_PRICE).toInt())
                                tvLoad.text = getString(R.string.tuktuk)
                                icLoad.setImageResource(R.drawable.ic_golf_cart)
                            }
                            it.size == LOAD_MOTOR_BIKE -> {
                                tvPrice.text = currencyFormat(distance.div(100).times(MOTOR_BIKE_PRICE).toInt())
                                tvLoad.text = getString(R.string.motorbike)
                                icLoad.setImageResource(R.drawable.ic_motocross)
                            }
                            else -> {
                                tvPrice.text = currencyFormat(distance.div(100).times(THREE_TONE_TRUCK_PRICE).toInt())
                                tvLoad.text = getString(R.string.three_tonne_truck)
                                icLoad.setImageResource(R.drawable.ic_truck)
                            }
                        }
                    }
                })
            }
        })


        icBack.setOnClickListener {
            finish()
        }

        tvPreferred.setOnClickListener {
            openOptionMenu(it)
        }

        val offset = TimeZone.getDefault().rawOffset
        val jodaTz = DateTimeZone.forOffsetMillis(offset)
        var fromDate = DateTime(jodaTz)

        clSchedule.setOnClickListener {
            datePickerDialog.show()
        }


        val displayFormat = "MMM dd hh:mm a"
        val displayFmt: DateTimeFormatter = DateTimeFormat.forPattern(displayFormat)
        val format = "yyyy MM dd HH:mm"
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern(format)

        datePickerDialog = DatePickerDialog(
                this, R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val timeFormat = "HH:mm"
                    val timeFmt: DateTimeFormatter = DateTimeFormat.forPattern(timeFormat)
                    val dateStr = "$year ${month + 1} $dayOfMonth ${timeFmt.print(fromDate)}"
                    fromDate = fmt.parseDateTime(dateStr)
                    tvTime.text = displayFmt.print(fromDate)
                    tvTime.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
                    timePickerDialog.show()
                }, fromDate.year, fromDate.monthOfYear - 1, fromDate.dayOfMonth
        )

        timePickerDialog = TimePickerDialog(
                this, R.style.TimePickerTheme,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val dateFormat = "yyyy MM dd"
                    val dateFmt: DateTimeFormatter = DateTimeFormat.forPattern(dateFormat)
                    var dateStr = "${dateFmt.print(fromDate)} $hourOfDay:$minute"
                    if (minute < 10) {
                        dateStr = "${dateFmt.print(fromDate)} $hourOfDay:0$minute"
                    }
                    fromDate = fmt.parseDateTime(dateStr)
                    tvTime.text = displayFmt.print(fromDate)
                }, fromDate.hourOfDay, fromDate.minuteOfHour, false
        )


    }

    private fun openOptionMenu(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.preferred_rider_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.yes -> {
                    clPreferred.makeVisible()
                    tvPreferred.text = getString(R.string.yes_lower_case)
                }
                R.id.no -> {
                    clPreferred.makeInVisible()
                    tvPreferred.text = getString(R.string.no_lower_case)
                }
                else -> {
                    clPreferred.makeInVisible()
                    tvPreferred.text = getString(R.string.select)
                }
            }
            true
        }
        popup.show()
    }
}
