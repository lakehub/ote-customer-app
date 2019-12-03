package com.ote.otedeliveries.activities.maps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ote.otedeliveries.R
import com.ote.otedeliveries.adapters.AutoCompletePlaceAdapter
import com.ote.otedeliveries.callbacks.AutoCompletePlaceCallback
import com.ote.otedeliveries.utils.makeVisible
import com.ote.otedeliveries.utils.showWarning
import kotlinx.android.synthetic.main.activity_pick_address.*
import kotlinx.android.synthetic.main.connectivity_failure_dialog.view.*
import kotlinx.android.synthetic.main.content_pick_address.*
import java.util.*
import kotlin.collections.ArrayList

class PickAddressActivity : AppCompatActivity(), AutoCompletePlaceCallback {
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private lateinit var placesClient: PlacesClient
    private lateinit var token: AutocompleteSessionToken
    private lateinit var places: ArrayList<AutocompletePrediction>
    private lateinit var autoCompletePlaceAdapter: AutoCompletePlaceAdapter
    private val deliverFocused = 2
    private val pickupFocused = 1
    private var currentFocusView = 1
    private var deliveryAddress = ""
    private var deliveryPlaceId = ""
    private var pickupAddress = ""
    private var pickupPlaceId = ""
    private var pickUpPlace: LatLng? = null
    private var deliveryPlace: LatLng? = null
    private var imm: InputMethodManager? = null
    private var updateDelivery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_address)

        alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(true)
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.connectivity_failure_dialog, null)
        alertDialogBuilder.setView(dialogView)
        alertDialog = alertDialogBuilder.create()

        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled)
        )

        val colors = intArrayOf(
                ContextCompat.getColor(this, R.color.colorSuccess)
        )
        val colorList = ColorStateList(states, colors)

        btnAddStop.backgroundTintList = colorList

        placesClient = Places.createClient(this)
        token = AutocompleteSessionToken.newInstance()
        places = arrayListOf()
        autoCompletePlaceAdapter = AutoCompletePlaceAdapter(this)
        val myLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            adapter = autoCompletePlaceAdapter
            layoutManager = myLayoutManager
        }

        etDelivery.tag = null
        etPickup.tag = null


        val update = intent.getBooleanExtra("update", false)

        if (update) {
            pickupAddress = intent.getStringExtra("pickupAddress")!!
            deliveryAddress = intent.getStringExtra("deliveryAddress")!!
            pickupPlaceId = intent.getStringExtra("pickupPlaceId")!!
            deliveryPlaceId = intent.getStringExtra("deliveryPlaceId")!!
            val pickupLat = intent.getDoubleExtra("pickupLat", 0.0)
            val pickupLng = intent.getDoubleExtra("pickupLng", 0.0)
            val deliveryLng = intent.getDoubleExtra("deliveryLng", 0.0)
            val deliveryLat = intent.getDoubleExtra("deliveryLat", 0.0)
            updateDelivery = intent.getBooleanExtra("updateDelivery", false)
            pickUpPlace = LatLng(pickupLat, pickupLng)
            deliveryPlace = LatLng(deliveryLat, deliveryLng)

            etPickup.setText(pickupAddress)
            etDelivery.setText(deliveryAddress)
            deliveryContainer.makeVisible()
            btnAddStop.makeVisible()
            btnProceed.makeVisible()

        }

        etPickup.addTextChangedListener { text ->
            if (text.toString().isNotBlank()) {
                if (etPickup.tag == null)
                    geoCodeLocation(text.toString())
            } else {
                places.clear()
                autoCompletePlaceAdapter.swapData(places, "")
            }
        }

        etDelivery.addTextChangedListener { text ->
            if (text.toString().isNotBlank()) {
                if (etDelivery.tag == null)
                    geoCodeLocation(text.toString())
            } else {
                places.clear()
                autoCompletePlaceAdapter.swapData(places, "")
            }
        }

        if (updateDelivery) {
            etDelivery.requestFocus()
            currentFocusView = deliverFocused
        } else {
            etPickup.requestFocus()
        }
        imm = getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager
        imm?.showSoftInput(etPickup, InputMethodManager.SHOW_IMPLICIT)

        ivBack.setOnClickListener {
            finish()
        }

        etPickup.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoCodeLocation(etPickup.text.toString())
                val mView = this.currentFocus
                if (mView != null) {
                    imm?.hideSoftInputFromWindow(mView.windowToken, 0)
                }
            }
            true
        }

        etDelivery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoCodeLocation(etPickup.text.toString())
                val mView = this.currentFocus
                if (mView != null) {
                    imm?.hideSoftInputFromWindow(mView.windowToken, 0)
                }
            }
            true
        }

        dialogView.tvOk.setOnClickListener {
            alertDialog.dismiss()
        }

        etPickup.setOnFocusChangeListener { _, focused ->
            if (focused) {
                currentFocusView = pickupFocused
                etPickup.tag = null
            }
        }

        etDelivery.setOnFocusChangeListener { _, focused ->
            if (focused) {
                currentFocusView = deliverFocused
                etDelivery.tag = null
            }
        }

        btnProceed.setOnClickListener {
            val myIntent = Intent()
            myIntent.putExtra("pickupAddress", pickupAddress)
            myIntent.putExtra("deliveryAddress", deliveryAddress)
            myIntent.putExtra("pickupLat", pickUpPlace?.latitude)
            myIntent.putExtra("pickupLng", pickUpPlace?.longitude)
            myIntent.putExtra("deliveryLng", deliveryPlace?.longitude)
            myIntent.putExtra("deliveryLat", deliveryPlace?.latitude)
            myIntent.putExtra("deliveryPlaceId", deliveryPlaceId)
            myIntent.putExtra("pickupPlaceId", pickupPlaceId)
            setResult(Activity.RESULT_OK, myIntent)
            finish()
        }
    }

    private fun geoCodeLocation(address: String) {
        val request = FindAutocompletePredictionsRequest.builder()
                .setCountry("ke")
                .setSessionToken(token)
                .setQuery(address)
                .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            places.clear()
            places.addAll(response.autocompletePredictions)
            autoCompletePlaceAdapter.swapData(places, address)
        }.addOnFailureListener { exception ->
            places.clear()
            autoCompletePlaceAdapter.swapData(places, "")
            if (exception is ApiException) {
                alertDialog.show()
            }
        }
    }


    override fun onClickCallback(item: AutocompletePrediction) {
        if (currentFocusView == pickupFocused) {
            if (deliveryPlaceId != item.placeId) {
                etPickup.tag = "dummy"
                etPickup.setText(item.getPrimaryText(null))
                deliveryContainer.makeVisible()
                etDelivery.requestFocus()
                pickupAddress = item.getPrimaryText(null).toString()
                getCoordinates(item.placeId, pickupFocused)
                pickupPlaceId = item.placeId
                places.clear()
                autoCompletePlaceAdapter.swapData(places, "")
            } else {
                showWarning(getString(R.string.same_address_err))
            }
        } else {
            if (pickupPlaceId != item.placeId) {
                etDelivery.tag = "dummy"
                etDelivery.setText(item.getPrimaryText(null))
                deliveryAddress = item.getPrimaryText(null).toString()
                btnAddStop.makeVisible()
                btnProceed.makeVisible()
                val mView = this.currentFocus
                if (mView != null) {
                    imm?.hideSoftInputFromWindow(mView.windowToken, 0)
                }
                getCoordinates(item.placeId, deliverFocused)
                deliveryPlaceId = item.placeId
                places.clear()
                autoCompletePlaceAdapter.swapData(places, "")
            } else {
                showWarning(getString(R.string.same_address_err))
            }
        }
    }

    private fun getCoordinates(placeId: String, currentFocus: Int) {
        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        val request = FetchPlaceRequest.builder(placeId, placeFields)
                .build()
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            if (currentFocus == pickupFocused) {
                pickUpPlace = response.place.latLng
            } else {
                deliveryPlace = response.place.latLng
            }
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
            }
        }
    }
}
