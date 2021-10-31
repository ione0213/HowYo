package com.yuchen.howyo.plan.detail.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDetailEditBinding
import com.yuchen.howyo.ext.getVmFactory

class DetailEditFragment : Fragment() {

    private lateinit var binding: FragmentDetailEditBinding
    private val viewModel by viewModels<DetailEditViewModel> {
        getVmFactory(
            DetailEditFragmentArgs.fromBundle(requireArguments()).schedule,
            DetailEditFragmentArgs.fromBundle(requireArguments()).planId,
            DetailEditFragmentArgs.fromBundle(requireArguments()).dayId
        )
    }
    private val fromAlbum = 0x00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = DetailEditImagesAdapter(viewModel)

        binding.recyclerDetailEditImages.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(binding.recyclerDetailEditImages)

        binding.spinnerDetailEditType.adapter = DetailEditSpinnerAdapter(
            HowYoApplication.instance.resources.getStringArray(R.array.schedule_type_list)
        )

        viewModel.leaveEditDetail.observe(viewLifecycleOwner, {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        viewModel.selectPhoto.observe(viewLifecycleOwner, {
            it?.let {
                selectPhoto()
                viewModel.onSelectedPhoto()
            }
        })

        viewModel.scheduleResult.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        findNavController().popBackStack()
                        viewModel.onBackToPlanPortal()
                    }
                }
            }
        })

        return binding.root
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "image/*"

        startActivityForResult(intent, fromAlbum)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    data.data?.let { uri ->

                        viewModel.setBitmap(uri)
                    }
                }
            }
        }
    }
}