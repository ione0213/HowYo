package com.yuchen.howyo.plan.cover

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.DialogPlanCoverBinding
import com.yuchen.howyo.ext.closeKeyBoard
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.ext.setTouchDelegate
import com.yuchen.howyo.plan.AccessPlanType
import java.io.File

const val TAG = "DATE_PICKER"

class PlanCoverDialog : AppCompatDialogFragment() {
    private lateinit var binding: DialogPlanCoverBinding

    private val viewModel by viewModels<PlanCoverViewModel> {
        getVmFactory(
            PlanCoverDialogArgs.fromBundle(requireArguments()).plan
        )
    }

    private val takePhoto = 0x00
    private val fromAlbum = 0x01
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPlanCoverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.btnPlanCoverClose.setTouchDelegate()

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    this.dismiss()
                    viewModel.onLeaveCompleted()
                }
            }
        }

        viewModel.selectDate.observe(viewLifecycleOwner) {
            it?.let {
                when {
                    it -> showDateRangePicker()
                }
            }
        }

        viewModel.takePhoto.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    takePhoto()
                    viewModel.onTookPhoto()
                }
            }
        }

        viewModel.selectPhoto.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    selectPhoto()
                    viewModel.onSelectedPhoto()
                }
            }
        }

        viewModel.isSavePlan.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    binding.edittextPlanTitle.closeKeyBoard()
                    viewModel.handlePlanCover()
                    viewModel.onSavedPlan()
                }
            }
        }

        viewModel.isCoverPhotoReady.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    when (viewModel.plan.value?.id.isNullOrEmpty()) {
                        true -> viewModel.createPlan()
                        false -> viewModel.updatePlan()
                    }
                }
            }
        }

        viewModel.planId.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) viewModel.createRelatedCollection()
            }
        }

        viewModel.isPlanUpdated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) viewModel.updateRelatedCollection()
            }
        }

        viewModel.isAllDataReady.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    when (viewModel.isNewPlan.value) {
                        true -> {
                            findNavController().navigate(
                                NavigationDirections.navToPlanFragment(
                                    viewModel.plan.value,
                                    AccessPlanType.EDIT
                                )
                            )
                        }
                        false -> findNavController().popBackStack()
                    }
                }
            }
        }

        return binding.root
    }

    override fun dismiss() {
        binding.layoutPlanCover.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_slide_down
            )
        )

        Handler().postDelayed({ super.dismiss() }, 200)
    }

    private fun showDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_plan_duration))
                .setTheme(R.style.MaterialCalendarTheme)
                .build()

        dateRangePicker.show(childFragmentManager, TAG)

        dateRangePicker.addOnPositiveButtonClickListener {
            viewModel.apply {
                startDateFromUser.value = it.first
                endDateFromUser.value = it.second
                onSelectedDate()
            }
        }
    }

    private fun takePhoto() {
        outputImage = File(activity?.externalCacheDir, "output_image.jpg")

        if (outputImage.exists()) {
            outputImage.delete()
        }

        outputImage.createNewFile()

        imageUri =
            FileProvider.getUriForFile(
                HowYoApplication.instance,
                "com.yuchen.howyo.fileProvider",
                outputImage
            )

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, takePhoto)
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
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.setCoverBitmap(imageUri)

                    binding.imgPlanCover.setImageBitmap(
                        rotateIfRequired(
                            BitmapFactory.decodeStream(
                                activity?.contentResolver?.openInputStream(imageUri)
                            )
                        )
                    )
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        viewModel.setCoverBitmap(uri)
                        binding.imgPlanCover.setImageBitmap(getBitmapFromUri(uri))
                    }
                }
            }
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)

        return when (
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        bitmap.recycle()

        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) =
        context?.contentResolver?.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
}
