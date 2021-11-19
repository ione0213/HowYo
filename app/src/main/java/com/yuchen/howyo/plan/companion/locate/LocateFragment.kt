package com.yuchen.howyo.plan.companion.locate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.REQUEST_ENABLE_GPS
import com.yuchen.howyo.util.REQUEST_LOCATION_PERMISSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import android.graphics.drawable.Drawable

import androidx.annotation.DrawableRes
import com.yuchen.howyo.databinding.LayoutAvatarInMapBinding
import android.graphics.PorterDuff

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import com.google.maps.android.ui.IconGenerator
import com.yuchen.howyo.data.User
import com.yuchen.howyo.util.Util.isInternetConnected


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

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLocateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        mContext = HowYoApplication.instance


        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            HowYoApplication.instance
        )

        if (isInternetConnected()) {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_locate_companion) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        viewModel.companions.observe(viewLifecycleOwner, {
            Logger.i("googleMap null? :${googleMap == null}")
            Logger.i("companions:${it.size}")
            it?.let {
                when {
                    it.isNotEmpty() -> locateCompanion()
                }
            }
        })

        Logger.i("~~~~~${googleMap == null}")

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        Logger.i("googleMap null onMapReady? :${googleMap == null}")
        this.googleMap = map
        Logger.i("googleMap null onMapReady2? :${googleMap == null}")
        locateCurrentUser()
    }

    private fun locateCurrentUser() {
        Logger.i("locateCurrentUser")
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
//        getDeviceLocation()
        googleMap?.isMyLocationEnabled = true
    }

    private fun locateCompanion() {
        Logger.i("locateCompanion")
        viewModel.companions.value?.forEach {

            lifecycleScope.launch {

                val bmp: Bitmap
                val options = BitmapFactory.Options()
                //Resize image as 1 / 5
                options.inSampleSize = 1

                withContext(Dispatchers.IO) {

                    //Load avatar as icon of map marker
                    val imageURLBase = "${it.avatar}"
                    val imageURL = URL(imageURLBase)
                    val connection: URLConnection = imageURL.openConnection()
                    val iconStream: InputStream = connection.getInputStream()
                    bmp = BitmapFactory.decodeStream(iconStream, null, options) as Bitmap
                }

                Logger.i("Location:${it.longitude}, ${it.latitude}")
                Logger.i("id:${it.id}")

                googleMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                it.latitude ?: 0.0,
                                it.longitude ?: 0.0
                            )
                        )
//                        .title(it.id)
//                        .title(it.name).icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .title(it.name).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getMarkerBitmapFromView(it, bmp)!!
                            )
                        )
                )
            }
        }
    }

    private fun checkGPSState() {
        Logger.i("checkGPSState")
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
            viewModel.getCompanionsData()
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
        Logger.i("getDeviceLocation")
        Logger.i("locationPermissionGranted:$locationPermissionGranted")
        try {
            if (locationPermissionGranted) {
                Logger.i("device innn")
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
                            Logger.i("requestLocationUpdates")
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

    private fun getMarkerBitmapFromView(user: User, bitmap: Bitmap): Bitmap? {
        val iconGenerator = IconGenerator(mContext)
        val markerView: View = LayoutInflater.from(mContext).inflate(R.layout.layout_avatar_in_map, null)
        val imgMarker = markerView.findViewById<ImageView>(R.id.img_map_user_avatar)
        imgMarker.setImageBitmap(bitmap)
//            .setImageResource()
        iconGenerator.setContentView(markerView)
        iconGenerator.setBackground(null)
        return iconGenerator.makeIcon("")
    }
}