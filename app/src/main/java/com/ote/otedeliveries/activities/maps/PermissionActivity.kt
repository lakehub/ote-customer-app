package com.ote.otedeliveries.activities.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ote.otedeliveries.R
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.android.synthetic.main.permission_dialog.view.*
import java.util.*

class PermissionActivity : AppCompatActivity() {
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var longitude = 0.0
    private var latitude = 0.0
    private lateinit var locationCallback: LocationCallback
    private var requestingPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        ripple.startRippleAnimation()

        alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.permission_dialog, null)
        alertDialogBuilder.setView(dialogView)
        alertDialog = alertDialogBuilder.create()
        alertDialog.dismiss()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                alertDialog.dismiss()
                longitude = locationResult.lastLocation?.longitude!!
                latitude = locationResult.lastLocation?.latitude!!
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        goToMaps()
                    }
                }, 2000L)
            }
        }

        dialogView.tvExit.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        dialogView.tvGrant.setOnClickListener {
            alertDialog.dismiss()
            requestPermission()
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

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!requestingPermission) {
            requestingPermission = true
            requestPermission()
        }
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
                            val deniedPermissions = report?.deniedPermissionResponses
                            if (report?.areAllPermissionsGranted() == true) {
                                requestingPermission = false
                                requestLocationUpdates()
                            } else if (deniedPermissions != null && deniedPermissions.isNotEmpty()) {
                                alertDialog.show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                        ) {
                            requestingPermission = true
                            token?.continuePermissionRequest()
                        }

                    }).check()
        } else {
            requestingPermission = false
            requestLocationUpdates()
        }
    }

    private fun goToMaps() {
        finish()
        val myIntent = Intent(this, MapActivity::class.java)
        myIntent.putExtra("lat", latitude)
        myIntent.putExtra("lng", longitude)
        startActivity(myIntent)
    }
}
