package com.yuchen.howyo.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentFavoriteBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    val viewModel by viewModels<FavoriteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = FavoriteAdapter(
            FavoriteAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            },
            viewModel
        )
        binding.recyclerFavoritePlans.adapter = adapter

        viewModel.plans.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.setAuthorIdSet()
            }
        }

        viewModel.authorIds.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.fetchAuthorData()
            }
        }

        viewModel.authorDataList.observe(viewLifecycleOwner) {
            it?.let {
                val gridLayoutManager = GridLayoutManager(context, 2)

                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (viewModel.plans.value?.size) {
                            0 -> 2
                            else -> 1
                        }
                    }
                }

                binding.recyclerFavoritePlans.layoutManager = gridLayoutManager

                viewModel.setStatusDone()

                binding.viewModel = viewModel

                adapter.addPlanOrEmptyPage(viewModel.plans.value ?: listOf())
            }
        }

        viewModel.navigateToPlan.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPlanFragment(it, AccessPlanType.VIEW)
                )

                viewModel.onPlanNavigated()
            }
        }

        return binding.root
    }
}
