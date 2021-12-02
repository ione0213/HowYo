package com.yuchen.howyo.plan.detail.view.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.databinding.FragmentDetailViewImageBinding
import com.yuchen.howyo.ext.getVmFactory

class DetailViewImageFragment : Fragment() {
    private lateinit var binding: FragmentDetailViewImageBinding

    private val viewModel by viewModels<DetailViewImageViewModel> {
        getVmFactory(
            DetailViewImageFragmentArgs.fromBundle(requireArguments()).imageUrl
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailViewImageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.leaveViewImage.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    findNavController().popBackStack()
                }
            }
        }

        return binding.root
    }
}
