package com.yuchen.howyo.plan.companion

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.databinding.FragmentCompanionBinding
import com.yuchen.howyo.ext.closeKeyBoard
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.ext.setTouchDelegate

class CompanionFragment : Fragment() {

    private lateinit var binding: FragmentCompanionBinding
    private val viewModel by viewModels<CompanionViewModel> {
        getVmFactory(
            CompanionFragmentArgs.fromBundle(requireArguments()).plan
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCompanionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = CompanionAdapter(viewModel)
        binding.recyclerCompanionFriends.adapter = adapter
        binding.btnCompanionClose.setTouchDelegate()

        binding.edittextCompanionFriend.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.closeKeyBoard()
                viewModel.filter()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        viewModel.plan.observe(viewLifecycleOwner) {

            it?.let {
                viewModel.getCurrentUser()
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {

            it?.let {
                viewModel.fetchFriendsData()
            }
        }

        viewModel.friends.observe(viewLifecycleOwner) {

            it?.let {
                viewModel.setStatusDone()
                viewModel.setFriendsForDisplay()
            }
        }

        viewModel.friendsForDisplay.observe(viewLifecycleOwner) {

            adapter.submitList(it)
            binding.viewModel = viewModel
        }

        viewModel.leave.observe(viewLifecycleOwner) {

            it?.let {
                findNavController().popBackStack()
                viewModel.onLeaveCompleted()
            }
        }

        return binding.root
    }
}
