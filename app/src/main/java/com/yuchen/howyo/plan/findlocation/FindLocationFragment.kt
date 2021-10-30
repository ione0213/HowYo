package com.yuchen.howyo.plan.findlocation

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentFindLocationBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.PlanViewModel
import com.yuchen.howyo.util.Logger

class FindLocationFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: FragmentFindLocationBinding
    val viewModel by viewModels<FindLocationViewModel> { getVmFactory() }
//    private val viewModel by viewModels<FindLocationViewModel> {
//        getVmFactory(
////            FindLocationFragmentArgs.fromBundle(requireArguments()).days
//        )
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFindLocationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.recyclerFindLocationDays.adapter = FindLocationDaysAdapter(viewModel)

        binding.edittextFindLocationSearch.setOnEditorActionListener { v, actionId, event ->
            when {
                actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.KEYCODE_ENTER
                -> {
                    geoLocate()
                }
            }
            false
        }

        //Map
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            HowYoApplication.instance
        )
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_find_location_destination) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
    }

    private fun geoLocate() {
        val geocoder = Geocoder(context)

        val list: List<Address> =
            geocoder.getFromLocationName(binding.edittextFindLocationSearch.text.toString(), 1)
    }
}