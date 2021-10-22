package com.yuchen.howyo.plan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentPlanBinding
import com.yuchen.howyo.ext.getVmFactory

class PlanFragment : Fragment() {

    private lateinit var binding: FragmentPlanBinding
    val viewModel by viewModels<PlanViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerPlanDays.adapter = PlanDaysAdapter(viewModel)
        binding.recyclerPlanSchedules.adapter =
            ScheduleAdapter(viewModel, ScheduleAdapter.OnClickListener {
                viewModel.navigateToDetail(it)
            })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.navigateToMapMode.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToFindLocaitonFragment())
                viewModel.onMapModeNavigated()
            }
        })

        return binding.root
    }
}