package com.yuchen.howyo.discover

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentDiscoverBinding
import com.yuchen.howyo.ext.closeKeyBoard
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

        val adapter = DiscoverAdapter(
            DiscoverAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }, viewModel
        )
        binding.recyclerDiscoverPlans.adapter = adapter

        binding.layoutSwipeRefreshDiscover.setOnRefreshListener {
            viewModel.getPlansResult()
        }
        viewModel.refreshStatus.observe(
            viewLifecycleOwner, {
                it?.let {
                    binding.layoutSwipeRefreshDiscover.isRefreshing = it
                }
            }
        )

        binding.edittextDiscoverSearch.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.closeKeyBoard()
                viewModel.filter()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        viewModel.plans.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.setAuthorIdSet()
            }
        }

        viewModel.authorIds.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getAuthorData()
            }
        }

        viewModel.authorDataList.observe(viewLifecycleOwner) {
            it?.let {

                viewModel.setStatusDone()
                viewModel.setPlansForShow()
            }
        }

        viewModel.plansForShow.observe(viewLifecycleOwner) {

            adapter.submitList(it)
            binding.viewModel = viewModel
        }

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