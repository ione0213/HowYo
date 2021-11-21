package com.yuchen.howyo.plan.detail.view.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentLocateBinding
import com.yuchen.howyo.databinding.FragmentMapBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.companion.locate.LocateFragmentArgs
import com.yuchen.howyo.plan.companion.locate.LocateViewModel
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.REQUEST_ENABLE_GPS
import com.yuchen.howyo.util.REQUEST_LOCATION_PERMISSION

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private val viewModel by viewModels<MapViewModel> {
        getVmFactory(
            null,
            null,
            MapFragmentArgs.fromBundle(requireArguments()).schedule
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

        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        mContext = HowYoApplication.instance

        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            HowYoApplication.instance
        )
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.leaveMap.observe(viewLifecycleOwner, {
            it?.let {
                if (it) findNavController().popBackStack()
                viewModel.onLeaveMap()
            }
        })

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        locateCurrentUser()
    }

    private fun setDestination() {

        val currentLocation =
            LatLng(
                viewModel.schedule.value?.latitude ?: 0.0,
                viewModel.schedule.value?.longitude ?: 0.0
            )

        val marker = googleMap?.addMarker(
            MarkerOptions().position(currentLocation).title(viewModel.schedule.value?.title)
        )

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(currentLocation, 16F)
        )

        marker?.showInfoWindow()

        //Disable touch in scrollView
//        googleMap?.uiSettings?.isScrollGesturesEnabled = false
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

        googleMap?.isMyLocationEnabled = true
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
            setDestination()
        }
    }

    private fun requestLocationPermission() {
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

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            checkGPSState()
        } else {
            requestLocationPermission()
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                // update frequency
                locationRequest.interval = 6000
                // update times
                locationRequest.numUpdates = 1
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return

                            val currentLocation =
                                LatLng(
                                    locationResult.lastLocation.latitude,
                                    locationResult.lastLocation.longitude
                                )
                            googleMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation, 16f
                                )
                            )
                            viewModel.onLocateDone()
                        }
                    },
                    Looper.getMainLooper()
                )
            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}