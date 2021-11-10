package com.yuchen.howyo.profile.author

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentAuthorProfileBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType

class AuthorProfileFragment : Fragment() {

    private lateinit var binding: FragmentAuthorProfileBinding
    val viewModel by viewModels<AuthorProfileViewModel> { getVmFactory(
        AuthorProfileFragmentArgs.fromBundle(requireArguments()).userId
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthorProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerAuthorProfilePlans.adapter = AuthorProfilePlanAdapter(
            AuthorProfilePlanAdapter.OnClickListener {
                viewModel.navigateToPlan(it)
            }
        )

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                mainViewModel.setSharedToolbarTitle(it.id ?: "")
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

        viewModel.navigateToSetting.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        findNavController().navigate(NavigationDirections.navToSettingFragment())
                    }
                }
                viewModel.onSettingNavigated()
            }
        })

        viewModel.navigateToFriends.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        findNavController().navigate(NavigationDirections.navToFriendsFragment())
                    }
                }
                viewModel.onFriendNavigated()
            }
        })

        return binding.root
    }
}