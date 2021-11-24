package com.yuchen.howyo.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentProfileBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.signin.UserManager

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
            ProfileFragmentArgs.fromBundle(requireArguments()).userId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerProfilePlans.adapter = PlanAdapter(
            PlanAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }
        )

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.currentUser.observe(viewLifecycleOwner) {
            it?.let {
                mainViewModel.setSharedToolbarTitle(it.name ?: "")
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

        viewModel.navigateToSetting.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(NavigationDirections.navToSettingFragment())
                viewModel.onSettingNavigated()
            }
        }

        viewModel.navigateToFriends.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToFriendsFragment(it, UserManager.userId!!)
                )
                viewModel.onFriendNavigated()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)

        menu.findItem(R.id.setting).isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> viewModel.navigateToSetting()
        }

        return super.onOptionsItemSelected(item)
    }
}
