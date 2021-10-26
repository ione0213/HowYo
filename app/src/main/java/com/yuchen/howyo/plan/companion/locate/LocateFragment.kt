package com.yuchen.howyo.plan.companion.locate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentLocateBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.util.REQUEST_ENABLE_GPS
import com.yuchen.howyo.util.REQUEST_LOCATION_PERMISSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class LocateFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentLocateBinding
    private val viewModel by viewModels<LocateViewModel> {
        getVmFactory(
            LocateFragmentArgs.fromBundle(requireArguments()).plan
        )
    }
    private var googleMap: GoogleMap? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLocateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        mContext = HowYoApplication.instance

        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            HowYoApplication.instance
        )
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_locate_companion) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it.isNotEmpty() -> locateCompanion()
                }
            }
        })

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        locateCurrentUser()
        viewModel.setMockUserData()
    }

    private fun locateCurrentUser() {
        googleMap?.clear()
        if (ActivityCompat.checkSelfPermission(
                HowYoApplication.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGPSState()
        } else {
            requestLocationPermission()
        }
        getDeviceLocation()
        googleMap?.isMyLocationEnabled = true
    }

    private fun locateCompanion() {

        viewModel.user.value?.forEach {

            lifecycleScope.launch {

                val bmp: Bitmap
                val options = BitmapFactory.Options()
                //Resize image as 1 / 5
                options.inSampleSize = 5

                withContext(Dispatchers.IO) {

                    //Load avatar as icon of map marker
                    val imageURLBase = "${it.avatar}"
                    val imageURL = URL(imageURLBase)
                    val connection: URLConnection = imageURL.openConnection()
                    val iconStream: InputStream = connection.getInputStream()
                    bmp = BitmapFactory.decodeStream(iconStream, null, options) as Bitmap
                }

                googleMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                it.longitude ?: 0.0,
                                it.latitude ?: 0.0
                            )
                        )
                        .title("${it.id}").icon(BitmapDescriptorFactory.fromBitmap(bmp))
                )
            }

//            googleMap?.addMarker(
//                MarkerOptions().position(
//                    LatLng(
//                        it.longitude ?: 0.0,
//                        it.latitude ?: 0.0
//                    )
//                ).title("${it.id}")
//                    .icon(BitmapDescriptor(BitmapDescriptorFactory.fromBitmap(bmp)))
//            )
        }
    }

    private fun checkGPSState() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(mContext)
                .setTitle("GPS 尚未開啟")
                .setMessage("使用此功能需要開啟 GSP 定位功能")
                .setPositiveButton("前往開啟",
                    DialogInterface.OnClickListener { _, _ ->
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
                        )
                    })
                .setNegativeButton("取消", null)
                .show()
        } else {
            getDeviceLocation()
            Toast.makeText(mContext, "已獲取到位置權限且GPS已開啟，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                mContext as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(mContext)
                .setMessage("此應用程式，需要位置權限才能正常使用")
                .setPositiveButton("確定") { _, _ ->
                    ActivityCompat.requestPermissions(
                        mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }
                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //已獲取到權限
                        locationPermissionGranted = true
                        checkGPSState()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                mContext as Activity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            //權限被永久拒絕
                            Toast.makeText(mContext, "位置權限已被關閉，功能將會無法正常使用", Toast.LENGTH_SHORT)
                                .show()

                            //Navigate to setting page
//                            AlertDialog.Builder(this)
//                                .setTitle("開啟位置權限")
//                                .setMessage("此應用程式，位置權限已被關閉，需開啟才能正常使用")
//                                .setPositiveButton("確定") { _, _ ->
//                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                                    startActivityForResult(intent, REQUEST_LOCATION_PERMISSION)
//                                }
//                                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
//                                .show()
                        } else {
                            //權限被拒絕
                            Toast.makeText(mContext, "位置權限被拒絕，功能將會無法正常使用", Toast.LENGTH_SHORT)
                                .show()
//                            requestLocationPermission()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                getLocationPermission()
            }
            REQUEST_ENABLE_GPS -> {
                checkGPSState()
            }
        }
    }

    //These function about getting location should be in a dependent class
    fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(mContext, "Get Permission", Toast.LENGTH_LONG).show()
            locationPermissionGranted = true
            checkGPSState()
        } else {
            requestLocationPermission()
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                //更新頻率
                locationRequest.interval = 6000
                //更新次數，若沒設定，會持續更新
                locationRequest.numUpdates = 1
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            Log.d(
                                "HKT",
                                "緯度:${locationResult.lastLocation.latitude} , 經度:${locationResult.lastLocation.longitude} "
                            )
                            val currentLocation =
                                LatLng(
                                    locationResult.lastLocation.latitude,
                                    locationResult.lastLocation.longitude
                                )
//                            googleMap?.addMarker(
//                                MarkerOptions().position(currentLocation).title("現在位置")
//                            )
                            googleMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation, 16f
                                )
                            )
                        }
                    },
                    null
                )

            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}