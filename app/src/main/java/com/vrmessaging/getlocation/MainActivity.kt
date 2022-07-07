package com.vrmessaging.getlocation

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.haseebazeem.sampleGif.GifImageView
import android.os.Bundle
import com.vrmessaging.getlocation.R
import com.bumptech.glide.Glide
import android.os.Build
import androidx.core.content.ContextCompat
import android.app.Activity
import android.content.pm.PackageManager
import android.content.Intent
import android.annotation.SuppressLint
import com.google.android.gms.tasks.OnCompleteListener
import androidx.annotation.RequiresApi
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var loc_txt: TextView
    lateinit var img_button: Button
    lateinit var thumbnail: ImageView
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loc_txt = findViewById(R.id.loc_txt)
        thumbnail = findViewById(R.id.thumbnail)
        img_button = findViewById(R.id.img_button)

        if (thumbnail != null) {
            Glide.with(this)
                .load(R.drawable.imageasset)
                .into(thumbnail!!)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    (this@MainActivity as Activity),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    (this@MainActivity as Activity),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            } else {
            }
        }

        img_button.setOnClickListener(this)
        loc_txt.setOnClickListener(this)
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                lastLocation
            } else {
                lastLocation
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_button -> lastLocation
            R.id.loc_txt -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(loc_txt!!.text.toString())
                startActivity(i)
            }
        }
    }// check if location is enabled

    // check if permissions are given
    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        private get() {
            // check if permissions are given
            if (checkPermissions()) {

                // check if location is enabled
                if (isLocationEnabled) {
                    mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            loc_txt!!.text =
                                "http://maps.google.com/maps?q=loc:" + location.latitude + "," + location.longitude
                        }
                    }
                } else {
                    Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermission()
            }
        }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private val isLocationEnabled: Boolean
        private get() {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var requestLocationUpdates: Task<Void> = mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            loc_txt!!.text =
                "http://maps.google.com/maps?q=loc:" + mLastLocation.latitude + "," + mLastLocation.longitude
        }
    }
}