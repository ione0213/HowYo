package com.yuchen.howyo.plan.groupmessage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentGroupMessageBinding
import com.yuchen.howyo.ext.getVmFactory

class GroupMessageFragment : Fragment() {

    private lateinit var binding: FragmentGroupMessageBinding
    private val viewModel by viewModels<GroupMessageViewModel> { getVmFactory(
        GroupMessageFragmentArgs.fromBundle(requireArguments()).plan
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerGroupMsg.adapter = GroupMessageAdapter()

        return binding.root
    }
}