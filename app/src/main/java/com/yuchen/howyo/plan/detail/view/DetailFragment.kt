package com.yuchen.howyo.plan.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDetailBinding
import com.yuchen.howyo.ext.getVmFactory


class DetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(requireArguments()).schedule
        )
    }

    private var googleMap: GoogleMap? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerDetailImages.adapter = DetailImagesAdapter()

        LinearSnapHelper().attachToRecyclerView(binding.recyclerDetailImages)

        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HowYoApplication.instance)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_detail_destination) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.navigateToEditSchedule.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToDetailEditFragment(it))
                viewModel.onEditScheduleNavigated()
            }
        })

        viewModel.navigateToViewMap.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToMapFragment(it))
                viewModel.onViewMapNavigated()
            }
        })

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        setDestination()
    }

    private fun setDestination() {

        val currentLocation =
            LatLng(
                viewModel.schedule.value?.longitude ?: 0.0,
                viewModel.schedule.value?.latitude ?: 0.0
            )

        googleMap?.addMarker(MarkerOptions().position(currentLocation).title("現在位置"))
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(currentLocation, 16F)
        )

        //Disable touch in scrollView
        googleMap?.uiSettings?.isScrollGesturesEnabled = false
    }
}