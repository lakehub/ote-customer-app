package com.ote.otedeliveries.activities.maps

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.arsy.maps_library.MapRipple
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ote.otedeliveries.R
import com.ote.otedeliveries.activities.order.PlaceOrderActivity
import com.ote.otedeliveries.adapters.LoadQuantityPagerAdapter
import com.ote.otedeliveries.app.AppPreferences
import com.ote.otedeliveries.data.entities.Distance
import com.ote.otedeliveries.network_requests.sendToken
import com.ote.otedeliveries.repos.DistanceRepo
import com.ote.otedeliveries.retrofit.RetrofitFactory
import com.ote.otedeliveries.utils.*
import com.ote.otedeliveries.viewmodel_factories.DistanceViewModelFactory
import com.ote.otedeliveries.viewmodels.DistanceViewModel
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.content_map.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var mMap: GoogleMap? = null
    private var lat = 0.0
    private var lng = 0.0
    private var pickupLat = 0.0
    private var pickupLng = 0.0
    private var delLat = 0.0
    private var delLng = 0.0
    private lateinit var placesClient: PlacesClient
    private lateinit var token: AutocompleteSessionToken
    private var pickupMarker: Marker? = null
    private var deliveryMarker: Marker? = null
    private lateinit var mapRipple: MapRipple
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var polyline: Polyline
    private var deliveryAddress = ""
    private var pickupAddress = ""
    private var deliveryPlaceId = ""
    private var pickupPlaceId = ""
    private lateinit var loadQuantityPagerAdapter: LoadQuantityPagerAdapter
    private var viewUp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val mapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!AppPreferences.firstTimeTokenSent)
            sendToken(AppPreferences.deviceToken)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                mapRipple.withLatLng(
                        LatLng(locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude)
                )
                if (!mapRipple.isAnimationRunning)
                    mapRipple.startRippleMapAnimation()
            }
        }

        placesClient = Places.createClient(this)
        token = AutocompleteSessionToken.newInstance()

        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)

        ivDrawerToggle.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(GravityCompat.START, true)
            } else {
                drawer_layout.closeDrawer(GravityCompat.START, true)
            }
        }

        tvUsername.text = titleCase(AppPreferences.fullName!!)

        clPickUpAddress.setOnClickListener {
            val myIntent = Intent(this, PickAddressActivity::class.java)
            myIntent.putExtra("pickup", true)
            startActivityForResult(myIntent, PICK_ADDRESS_REQUEST_CODE)
        }

        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }

        val myLayoutParams = paddingView.layoutParams as ConstraintLayout.LayoutParams
        myLayoutParams.height = statusBarHeight.times(2)
        paddingView.layoutParams = myLayoutParams
        loadQuantityPagerAdapter = LoadQuantityPagerAdapter(supportFragmentManager)
        viewPager.adapter = loadQuantityPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        icExpand.setOnClickListener {
            viewUp = if (viewUp) {
                loadSizeViewContainer.slideDown(loadSizeView.height)
                false
            } else {
                loadSizeViewContainer.slideUp()
                true
            }
        }

        btnContinue.setOnClickListener {
            startActivity(Intent(this, PlaceOrderActivity::class.java))
        }

    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (mapRipple.isAnimationRunning) {
            mapRipple.stopRippleMapAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val zoomLevel = 16f
        val place = LatLng(lat, lng)
        mMap = googleMap
        val uiSettings = mMap?.uiSettings
//        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        mMap?.setOnMarkerClickListener(this)
        mMap?.clear()
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoomLevel))
        mapRipple = MapRipple(mMap, place, this@MapActivity)
        mapRipple.withNumberOfRipples(1)
        mapRipple.withFillColor(ContextCompat.getColor(this@MapActivity, R.color.colorPrimaryLight))
        mapRipple.withStrokeColor(ContextCompat.getColor(this@MapActivity, R.color.colorPrimaryLight2))
        mapRipple.withStrokewidth(15)      // 10dp
        mapRipple.withDistance(500.0)     // 2000 metres radius
        mapRipple.withRippleDuration(12000)    //12000ms
        mapRipple.withTransparency(0.1f)
        mapRipple.startRippleMapAnimation()

        uiSettings?.isCompassEnabled = false
        uiSettings?.isRotateGesturesEnabled = false
        uiSettings?.isTiltGesturesEnabled = true
        uiSettings?.isMyLocationButtonEnabled = true
        uiSettings?.isZoomControlsEnabled = false
        uiSettings?.isMapToolbarEnabled = false

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val myIntent = Intent(this, PickAddressActivity::class.java)
        myIntent.putExtra("update", true)
        myIntent.putExtra("pickupAddress", pickupAddress)
        myIntent.putExtra("deliveryAddress", deliveryAddress)
        myIntent.putExtra("pickupLat", pickupLat)
        myIntent.putExtra("pickupLng", pickupLng)
        myIntent.putExtra("deliveryLat", delLat)
        myIntent.putExtra("deliveryLng", delLng)
        myIntent.putExtra("pickupPlaceId", pickupPlaceId)
        myIntent.putExtra("deliveryPlaceId", deliveryPlaceId)

        if (marker?.position?.latitude == pickupLat) {
            myIntent.putExtra("updateDelivery", false)
        } else {
            myIntent.putExtra("updateDelivery", true)
        }
        startActivityForResult(myIntent, PICK_ADDRESS_REQUEST_CODE)
        return false
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START, true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mMap?.clear()
            clPickUpAddressContainer.makeGone()
            clProgress.makeVisible()
            pickupLat = data!!.getDoubleExtra("pickupLat", 0.0)
            pickupLng = data.getDoubleExtra("pickupLng", 0.0)
            delLat = data.getDoubleExtra("deliveryLat", 0.0)
            delLng = data.getDoubleExtra("deliveryLng", 0.0)
            deliveryAddress = data.getStringExtra("deliveryAddress")!!
            pickupAddress = data.getStringExtra("pickupAddress")!!
            deliveryPlaceId = data.getStringExtra("deliveryPlaceId")!!
            pickupPlaceId = data.getStringExtra("pickupPlaceId")!!

            val pickupPlace = LatLng(pickupLat, pickupLng)
            val markerOptions = MarkerOptions().position(pickupPlace)
                    .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                    createPickupMarker(
                                            limitStringLength(pickupAddress, 15)
                                    )
                            )
                    )

            pickupMarker?.remove()
            pickupMarker = mMap?.addMarker(markerOptions)

            val deliveryPlace = LatLng(delLat, delLng)
            val markerOptionsDelivery = MarkerOptions().position(deliveryPlace)
                    .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                    createDeliveryMarker(
                                            limitStringLength(deliveryAddress, 15)
                                    )
                            )
                    )

            deliveryMarker?.remove()
            deliveryMarker = mMap?.addMarker(markerOptionsDelivery)

            val builder = LatLngBounds.Builder()
            builder.include(pickupPlace)
            builder.include(deliveryPlace)
            val bounds = builder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200)
            mMap?.animateCamera(cameraUpdate)

            val origin = "$pickupLat,$pickupLng"
            val destination = "$delLat,$delLng"

            getDirections(origin, destination, "")
        }
    }

    private fun createPickupMarker(text: String): Bitmap {
        val markerLayout = layoutInflater.inflate(R.layout.pick_up_address_marker, null)

        val textView = markerLayout.findViewById(R.id.textView) as TextView
        textView.text = text

        markerLayout.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        markerLayout.layout(0, 0, markerLayout.measuredWidth, markerLayout.measuredHeight)

        val bitmap = Bitmap.createBitmap(markerLayout.measuredWidth, markerLayout.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerLayout.draw(canvas)
        return bitmap
    }

    private fun createDeliveryMarker(text: String): Bitmap {
        val markerLayout = layoutInflater.inflate(R.layout.delivery_marker, null)

        val textView = markerLayout.findViewById(R.id.textView) as TextView
        textView.text = text

        markerLayout.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        markerLayout.layout(0, 0, markerLayout.measuredWidth, markerLayout.measuredHeight)

        val bitmap = Bitmap.createBitmap(markerLayout.measuredWidth, markerLayout.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerLayout.draw(canvas)
        return bitmap
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report?.areAllPermissionsGranted() == true) {
                                requestLocationUpdates()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }

                    }).check()
        } else {
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            requestPermission()
        }
    }

    private fun getAddress(lat: Double, lng: Double, addressType: Int) {
        val geocoder = Geocoder(this, Locale("en", "ke"))
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 5)
            val address = addresses[1]

            Log.e("TAG", "addressed: $addresses")

            when (addressType) {
                PICKUP_ADDRESS -> {
                    val place = LatLng(pickupLat, pickupLng)
                    val markerOptions = MarkerOptions().position(place)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            createPickupMarker(
                                                    limitStringLength(address.featureName, 15)
                                            )
                                    )
                            )

                    pickupMarker?.remove()
                    pickupMarker = mMap?.addMarker(markerOptions)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(place))
                }
                DELIVERY_ADDRESS -> {
                    val place = LatLng(delLat, delLng)
                    val markerOptions = MarkerOptions().position(place)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            createDeliveryMarker(
                                                    limitStringLength(address.featureName, 15)
                                            )
                                    )
                            )

                    deliveryMarker?.remove()
                    deliveryMarker = mMap?.addMarker(markerOptions)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12f))

                    val origin = "$pickupLat,$pickupLng"
                    val destination = "$delLat,$delLng"

                    getDirections(origin, destination, "")
                }
                STOP_ADDRESS -> {

                }
            }
        } catch (e: IOException) {

        } catch (illegalArgumentException: IllegalArgumentException) {

        }
    }

    private fun getDirections(origin: String, destination: String, waypoints: String) {
//        showProgress()
        val service = RetrofitFactory.makeRetrofitServiceMaps()
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getDirectionsAsync(origin, destination, waypoints, API_KEY, REGION_KENYA)
            try {
                val body = request.await().body()
                val resCode = request.await().code()

                val errorBody = request.await().errorBody()
                if (resCode == 200) {
                    clProgress.makeGone()
                    val points = arrayListOf<LatLng>()
                    var distance = 0L

                    if (body?.status == STATUS_OK) {
                        loadSizeViewContainer.makeVisible()
                        val legs = body.routes?.get(0)?.legs
                        if (legs?.isNotEmpty() == true) {
                            val polylineOptions = PolylineOptions()

                            for (leg in legs) {
                                val startPlaceLeg = LatLng(leg.startLocation!!.lat!!, leg.startLocation!!.lng!!)
                                val endPlaceLeg = LatLng(leg.endLocation!!.lat!!, leg.endLocation!!.lng!!)
//                                points.add(startPlaceLeg)
                                val steps = leg.steps
                                if (steps?.isNotEmpty() == true) {
                                    for (step in steps) {
                                        val startPlace = LatLng(step.startLocation!!.lat!!, step.startLocation!!.lng!!)
                                        val endPlace = LatLng(step.endLocation!!.lat!!, step.endLocation!!.lng!!)
                                        val polyPoints = step.polyline!!.points
//                                        polylineOptions.addAll(polyPoints)
                                    }
                                }
//                                points.add(endPlaceLeg)

                                distance += leg.distance!!.value!!
                            }

                            val distanceItem = Distance(distance = distance)
                            DistanceRepo().addItem(distanceItem)

                            val startPlace = LatLng(pickupLat, pickupLng)
                            val endPlace = LatLng(delLat, delLng)
                            polylineOptions.add(startPlace)
                            val routes = body.routes!!
                            val overviewPolyline = routes[0].overviewPolyline
                            polylineOptions.addAll(decodePoly(overviewPolyline?.points!!))
                            polylineOptions.add(endPlace)

                            polyline = mMap?.addPolyline(polylineOptions)!!
                            polyline.color = ContextCompat.getColor(this@MapActivity, R.color.colorPrimary)
                            polyline.width = 5f

                        }
                    } else {
                        Log.e("TAG", "errorMessage: ${body?.errorMessage}")
                    }
                } else {
                    clProgress.makeGone()
                    if (errorBody != null) {
                        val errorData = JSONObject(errorBody.string())
                        showWarning(errorData.getString("message"))
                    }
                }

            } catch (e: Throwable) {
                clProgress.makeGone()
                Log.d("TAG", "error: ${e.message}")
                if (e is IOException) {
                    showNetworkError()
                }
            }
        }
    }

    private fun geoCode(origin: String, destination: String, waypoints: String) {
//        showProgress()
        val service = RetrofitFactory.makeRetrofitServiceMaps()
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getDirectionsAsync(origin, destination, waypoints, API_KEY, REGION_KENYA)
            try {
                val body = request.await().body()
                val resCode = request.await().code()

                val errorBody = request.await().errorBody()
                if (resCode == 200) {
//                    hideProgress()

                    if (body?.status == STATUS_OK) {
                        Log.e("TAG", "start: ${body.routes?.get(0)?.legs?.get(0)?.startAddress}")
                        Log.e("TAG", "end: ${body.routes?.get(0)?.legs?.get(0)?.endAddress}")
                    } else {
                        Log.e("TAG", "errorMessage: ${body?.errorMessage}")
                    }
                } else {
//                    hideProgress()
                    if (errorBody != null) {
                        val errorData = JSONObject(errorBody.string())
                        showWarning(errorData.getString("message"))
                    }
                }

            } catch (e: Throwable) {
                Log.d("TAG", "error: ${e.message}")
                if (e is IOException) {
//                    hideProgress()
                    showNetworkError()
                }
            }
        }
    }
}
