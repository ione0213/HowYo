package com.yuchen.howyo.plan.detail.edit

import android.app.Activity
import android.app.TimePickerDialog
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
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.data.Photos
import com.yuchen.howyo.databinding.FragmentDetailEditBinding
import com.yuchen.howyo.ext.getVmFactory
import java.util.*

class DetailEditFragment : Fragment() {

    private lateinit var binding: FragmentDetailEditBinding
    private val viewModel by viewModels<DetailEditViewModel> {
        getVmFactory(
            DetailEditFragmentArgs.fromBundle(requireArguments()).schedule,
            DetailEditFragmentArgs.fromBundle(requireArguments()).plan,
            DetailEditFragmentArgs.fromBundle(requireArguments()).day
        )
    }
    private val fromAlbum = 0x00

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = DetailEditImagesAdapter(viewModel)

        binding.recyclerDetailEditImages.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(binding.recyclerDetailEditImages)

        val spinnerList =
            HowYoApplication.instance.resources.getStringArray(R.array.schedule_type_list)

        val spinnerAdapter = DetailEditSpinnerAdapter(spinnerList)

        binding.spinnerDetailEditType.adapter = spinnerAdapter

        viewModel.leaveEditDetail.observe(viewLifecycleOwner, {
            it?.let {
                if (it) findNavController().popBackStack()
                viewModel.onLeaveEditDetail()
            }
        })

        viewModel.selectPhoto.observe(viewLifecycleOwner, {
            it?.let {
                selectPhoto()
                viewModel.onSelectedPhoto()
            }
        })

        viewModel.setTime.observe(viewLifecycleOwner, {
            it?.let {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                c.timeInMillis = viewModel.plan.value?.startDate!!.plus(
                    (1000 * 60 * 60 * 24 * (viewModel.day.value?.position ?: 0))
                )

                val picker = TimePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                    { tp, sHour, sMinute ->

                        c.set(Calendar.HOUR_OF_DAY, sHour)
                        c.set(Calendar.MINUTE, sMinute)

                        when (it) {
                            getString(R.string.detail_edit_schedule_start_time) -> {
                                viewModel.setTimeValue(it, c.timeInMillis)
                            }
                            getString(R.string.detail_edit_schedule_end_time) -> {
                                viewModel.setTimeValue(it, c.timeInMillis)
                            }
                        }
                    },
                    hour,
                    minute,
                    true
                )
                picker.show()
                viewModel.onSetTime()
            }
        })

        viewModel.navigateToEditImage.observe(viewLifecycleOwner, {
            it?.let {
                val schedulePhotos = Photos()
                viewModel.photoDataList.value?.forEach { schedulePhoto ->
                    schedulePhotos.add(schedulePhoto)
                }

                findNavController().navigate(
                    NavigationDirections.navToDetailEditImageFragment(
                        it,
                        schedulePhotos
                    )
                )
                viewModel.onEditImageNavigated()
            }
        })

        viewModel.scheduleResult.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        findNavController().popBackStack()
                        viewModel.onBackToPreviousPage()
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
