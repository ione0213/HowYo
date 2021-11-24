package com.yuchen.howyo.plan.detail.edit.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.databinding.FragmentDetailEditImageBinding
import com.yuchen.howyo.ext.getVmFactory

class DetailEditImageFragment : Fragment() {
    private lateinit var binding: FragmentDetailEditImageBinding

    private val viewModel by viewModels<DetailEditImageViewModel> {
        getVmFactory(
            DetailEditImageFragmentArgs.fromBundle(requireArguments()).schedulePhoto,
            DetailEditImageFragmentArgs.fromBundle(requireArguments()).schedulePhotos
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailEditImageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.leaveEditImage.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        }

        return binding.root
    }
}
