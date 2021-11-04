package com.yuchen.howyo.plan.detail.view.map

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private val viewModel by viewModels<MapViewModel> {
        getVmFactory(
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

        mContext = HowYoApplication.instance

        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            HowYoApplication.instance
        )
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        setDestination()
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
        googleMap?.uiSettings?.isScrollGesturesEnabled = false
    }
}