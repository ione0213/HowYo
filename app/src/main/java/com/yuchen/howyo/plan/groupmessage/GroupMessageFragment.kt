package com.yuchen.howyo.plan.groupmessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.databinding.FragmentGroupMessageBinding
import com.yuchen.howyo.ext.getVmFactory

class GroupMessageFragment : Fragment() {

    private lateinit var binding: FragmentGroupMessageBinding
    private val viewModel by viewModels<GroupMessageViewModel> {
        getVmFactory(
            GroupMessageFragmentArgs.fromBundle(requireArguments()).plan
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = GroupMessageAdapter()

        binding.recyclerGroupMsg.adapter = adapter

        viewModel.groupMsgResult.observe(viewLifecycleOwner) {
            it?.let {
                when {
                    it -> {
                        viewModel.onSubmittedComment()
                    }
                }
            }
        }

        viewModel.allGroupMessages.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getUsersResult()
            }
        }

        viewModel.groupMessages.observe(viewLifecycleOwner) {
            it?.let {
                adapter.checkMessageOwner(it)
//                adapter.notifyDataSetChanged()
                binding.recyclerGroupMsg.smoothScrollToPosition(it.size.plus(1))
            }
        }

        return binding.root
    }
}
