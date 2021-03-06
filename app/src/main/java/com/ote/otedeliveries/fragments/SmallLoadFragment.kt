package com.ote.otedeliveries.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ote.otedeliveries.R
import com.ote.otedeliveries.data.entities.Load
import com.ote.otedeliveries.repos.LoadRepo
import com.ote.otedeliveries.utils.*
import com.ote.otedeliveries.viewmodel_factories.DistanceViewModelFactory
import com.ote.otedeliveries.viewmodel_factories.LoadViewModelFactory
import com.ote.otedeliveries.viewmodels.DistanceViewModel
import com.ote.otedeliveries.viewmodels.LoadViewModel
import kotlinx.android.synthetic.main.fragment_small_load.view.*

class SmallLoadFragment : Fragment() {
    private lateinit var myView: View
    private lateinit var loadViewModel: LoadViewModel
    private lateinit var distanceViewModel: DistanceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_small_load, container, false)

        loadViewModel = ViewModelProvider(
                this,
                LoadViewModelFactory(Application())
        ).get(LoadViewModel::class.java)

        distanceViewModel = ViewModelProvider(
                this,
                DistanceViewModelFactory(Application())
        ).get(DistanceViewModel::class.java)

        val loadRepo = LoadRepo()

        myView.clMotor.setOnClickListener {
            val item = Load(size = LOAD_MOTOR_BIKE)
            loadRepo.addItem(item)
        }

        loadViewModel.getItem().observe(this, Observer {
            if (it != null) {
                if (it.size == LOAD_MOTOR_BIKE) {
                    myView.clMotor.setBackgroundResource(R.drawable.bg_active_load_item)
                } else {
                    myView.clMotor.background = null
                }
            }
        })

        distanceViewModel.getItem().observe(this, Observer {
            if (it != null) {
                val distance = it.distance
                myView.tvMotorPrice.text = currencyFormat(distance.div(100).times(MOTOR_BIKE_PRICE).toInt())
            } else {
                myView.tvMotorPrice.text = currencyFormat(0)
            }
        })



        return myView
    }
}