package com.yuchen.howyo.plan.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.databinding.FragmentCommentBinding
import com.yuchen.howyo.ext.closeKeyBoard
import com.yuchen.howyo.ext.getVmFactory

class CommentFragment : Fragment() {

    private lateinit var binding: FragmentCommentBinding
    private val viewModel by viewModels<CommentViewModel> {
        getVmFactory(
            CommentFragmentArgs.fromBundle(requireArguments()).plan
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCommentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = CommentAdapter()

        binding.recyclerCommentMsg.adapter = adapter

        viewModel.allComments.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.getUsersResult()
            }
        })

        viewModel.commentData.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.commentResult.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        viewModel.onSubmittedComment()
                        binding.edittextComment.closeKeyBoard()
                    }
                }
            }
        })

        return binding.root
    }
}
