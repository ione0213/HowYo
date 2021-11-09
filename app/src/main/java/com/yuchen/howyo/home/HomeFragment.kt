package com.yuchen.howyo.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentHomeBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType
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

        binding.recyclerHomePlans.adapter = HomeAdapter(
            HomeAdapter.OnClickListener {
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
        Logger.i("HOME onCreateOptionsMenu")
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