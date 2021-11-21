package com.yuchen.howyo.profile.friends.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentFriendItemBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.profile.friends.FriendFilter

class FriendItemFragment(
    private val friendType: FriendFilter,
    private val userId: String
) : Fragment() {

    private lateinit var binding: FragmentFriendItemBinding
    private val viewModel by viewModels<FriendItemViewModel> { getVmFactory(friendType, userId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFriendItemBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = FriendItemAdapter(viewModel, friendType, userId)

        binding.recyclerFriends.adapter = adapter

        viewModel.userIdList.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getUserDataList()
            }
        }

        viewModel.userList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        viewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navToAuthorProfileFragment(it))
                viewModel.onUserProfileNavigated()
            }
        }

        return binding.root
    }
}