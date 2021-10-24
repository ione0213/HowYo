package com.yuchen.howyo.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentFavoriteBinding
import com.yuchen.howyo.discover.DiscoverViewModel
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    val viewModel by viewModels<FavoriteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerFavoritePlans.adapter = FavoriteAdapter(
            FavoriteAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }
        )

        viewModel.navigateToPlan.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPlanFragment(
                        it,
                        AccessPlanType.VIEW
                    ))
                viewModel.onPlanNavigated()
            }
        })

        return binding.root
    }
}