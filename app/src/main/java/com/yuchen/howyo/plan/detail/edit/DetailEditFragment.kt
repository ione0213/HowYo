package com.yuchen.howyo.plan.detail.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDetailEditBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.detail.view.DetailFragmentArgs

class DetailEditFragment : Fragment() {

    private lateinit var binding: FragmentDetailEditBinding
    private val viewModel by viewModels<DetailEditViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(requireArguments()).schedule
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerDetailEditImages.adapter = DetailEditImagesAdapter(viewModel)
        LinearSnapHelper().attachToRecyclerView(binding.recyclerDetailEditImages)

        binding.spinnerDetailEditType.adapter = DetailEditSpinnerAdapter(
            HowYoApplication.instance.resources.getStringArray(R.array.schedule_type_list)
        )

        return binding.root
    }
}