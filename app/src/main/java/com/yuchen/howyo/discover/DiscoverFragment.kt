package com.yuchen.howyo.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDiscoverBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType

class DiscoverFragment : Fragment() {

    private lateinit var binding: FragmentDiscoverBinding
    val viewModel by viewModels<DiscoverViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerDiscoverPlans.adapter = DiscoverAdapter(
            DiscoverAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }
        )

        viewModel.navigateToPlan.observe(viewLifecycleOwner, {

            it?.let {

                findNavController().navigate(
                    NavigationDirections.navToPlanFragment(
                        it,
                        AccessPlanType.VIEW
                    )
                )

                viewModel.onPlanNavigated()
            }
        })

        return binding.root
    }
}