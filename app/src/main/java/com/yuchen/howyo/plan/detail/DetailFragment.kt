package com.yuchen.howyo.plan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.yuchen.howyo.databinding.FragmentDetailBinding
import com.yuchen.howyo.ext.getVmFactory

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(requireArguments()).schedule
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerDetailImages.adapter = DetailImagesAdapter()

        LinearSnapHelper().attachToRecyclerView(binding.recyclerDetailImages)

        return binding.root
    }
}