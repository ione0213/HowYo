package com.yuchen.howyo.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentHomeBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Logger

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = HomeAdapter(
            HomeAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }, viewModel
        )

        binding.recyclerHomePlans.adapter = adapter

        binding.layoutSwipeRefreshHome.setOnRefreshListener {
            viewModel.getPlansResult()
        }
        viewModel.refreshStatus.observe(
            viewLifecycleOwner, {
                it?.let {
                    binding.layoutSwipeRefreshHome.isRefreshing = it
                }
            }
        )

        viewModel.plans.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setAuthorIdSet()
            }
        })

        viewModel.authorIds.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getAuthorData()
            }
        }

        viewModel.authorDataList.observe(viewLifecycleOwner) {
            it?.let {
                it.forEach { user ->
                }
                viewModel.setStatusDone()
                binding.viewModel = viewModel
                adapter.submitList(viewModel.plans.value)
            }
        }

        viewModel.followingList.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    viewModel.getPlansResult()
                } else {
                    viewModel.setStatusDone()
                    if (!UserManager.isLoggedIn) {
                        val mainViewModel =
                            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
                        mainViewModel.navigateToHomeByBottomNav()
                    }
                }
            }
        })

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

        viewModel.navigateToNotification.observe(viewLifecycleOwner, {

            it?.let {

                findNavController().navigate(
                    NavigationDirections.navToHomeNotification()
                )

                viewModel.onNotificationNavigated()
            }

        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
        menu.findItem(R.id.notification).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notification -> {
                viewModel.navigateToNotification()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}