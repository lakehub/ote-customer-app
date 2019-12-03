package com.ote.otedeliveries.activities.maps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ote.otedeliveries.R
import com.ote.otedeliveries.utils.SEARCH_LOCATION_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_pick_location.*
import kotlinx.android.synthetic.main.content_pick_location.*

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMapClickListener {
    private var mMap: GoogleMap? = null
    private var lat = 0.0
    private var lng = 0.0
    private lateinit var placesClient: PlacesClient
    private lateinit var token: AutocompleteSessionToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val mapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)

        placesClient = Places.createClient(this)
        token = AutocompleteSessionToken.newInstance()

        ivBack.setOnClickListener {
            finish()
        }

        if (!intent.getBooleanExtra("pickup", false)) {
           tvTitle.text = getString(R.string.pick_delivery_address)
        }

        ivSearch.setOnClickListener {
            val myIntent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(
                    myIntent,
                    SEARCH_LOCATION_REQUEST_CODE
            )
        }

        btnConfirm.setOnClickListener {
            val myIntent = Intent()
            myIntent.putExtra("lat", mMap?.cameraPosition?.target?.latitude)
            myIntent.putExtra("lng", mMap?.cameraPosition?.target?.longitude)
            setResult(Activity.RESULT_OK, myIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setOnCameraIdleListener(this)
        mMap?.setOnCameraMoveStartedListener(this)
        mMap?.setOnMapClickListener(this)
        val zoomLevel = 16f
        val place = LatLng(lat, lng)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoomLevel))

    }

    override fun onCameraIdle() {
        ivMarker.setColorFilter(
                ContextCompat.getColor(
                        this,
                        R.color.colorWarning
                ),
                android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    override fun onCameraMoveStarted(reason: Int) {
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                ivMarker.setColorFilter(
                        ContextCompat.getColor(
                                this,
                                R.color.colorSuccess
                        ),
                        android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            GoogleMap.OnCameraMoveStartedListener
                    .REASON_API_ANIMATION -> {
            }
            GoogleMap.OnCameraMoveStartedListener
                    .REASON_DEVELOPER_ANIMATION -> {
//                Log.e("TAG", "The app moved the camera.")
            }
        }

    }

    override fun onMapClick(place: LatLng?) {
        val zoomLevel = 16f
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(place, zoomLevel))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val placeId = data?.getStringExtra("placeId")
            val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
            val request = FetchPlaceRequest.builder(placeId!!, placeFields)
                    .build()
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place.latLng
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(place))
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                }
            }
        }
    }
}
