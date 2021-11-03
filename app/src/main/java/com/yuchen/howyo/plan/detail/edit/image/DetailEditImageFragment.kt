package com.yuchen.howyo.plan.detail.edit.image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDetailEditBinding
import com.yuchen.howyo.databinding.FragmentDetailEditImageBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.detail.edit.DetailEditFragmentArgs
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel

class DetailEditImageFragment : Fragment() {

    private lateinit var binding: FragmentDetailEditImageBinding
    private val viewModel by viewModels<DetailEditImageViewModel> {
        getVmFactory(
            DetailEditImageFragmentArgs.fromBundle(requireArguments()).schedulePhoto,
            DetailEditImageFragmentArgs.fromBundle(requireArguments()).schedulePhotos
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailEditImageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.deleteImage.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController()
                    .navigate(
                        DetailEditImageFragmentDirections.navigateToDetailEditFragment()
                            .setSchedulePhoto(it)
                    )
                viewModel.onDeletedImage()
            }
        })

        viewModel.leaveEditImage.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        })

        return binding.root
    }
}