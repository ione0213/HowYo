package com.yuchen.howyo.profile.friends.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentFriendItemBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.profile.friends.FriendAdapter
import com.yuchen.howyo.profile.friends.FriendFilter
import kotlinx.coroutines.launch

class FriendItemFragment(private val friendType: FriendFilter) : Fragment() {

    private lateinit var binding: FragmentFriendItemBinding
    private val viewModel by viewModels<FriendItemViewModel> { getVmFactory(friendType) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFriendItemBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerFriends.adapter = FriendItemAdapter(viewModel)

//        viewModel.users.observe(viewLifecycleOwner, {
//
//            lifecycleScope.launch {
//
//                (binding.recyclerFriends.adapter as FriendItemAdapter).submitData(it)
//            }
//        })

        return binding.root
    }
}