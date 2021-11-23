package com.yuchen.howyo.profile.setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentSettingBinding
import com.yuchen.howyo.ext.getVmFactory
import java.io.File

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    val viewModel by viewModels<SettingViewModel> { getVmFactory() }
    private val takePhoto = 0x00
    private val fromAlbum = 0x01
    lateinit var imageUri: Uri
    lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.setAvatarPhoto()
            }
        }

        viewModel.selectPhoto.observe(viewLifecycleOwner, {
            it?.let {
                selectPhoto()
                viewModel.onSelectedPhoto()
            }
        })

        viewModel.takePhoto.observe(viewLifecycleOwner, {
            it?.let {
                takePhoto()
                viewModel.onTookPhoto()
            }
        })

        viewModel.isAvatarPhotoReady.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.updateUser()
                    findNavController().popBackStack()
                }
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
        menu.findItem(R.id.save).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.save -> {
                viewModel.handleAvatar()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "image/*"

        startActivityForResult(intent, fromAlbum)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {

                    viewModel.setAvatarBitmap(imageUri)
                    binding.imgProfileSettingAvatar.setImageBitmap(
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

                        viewModel.setAvatarBitmap(uri)
                        binding.imgProfileSettingAvatar.setImageBitmap(getBitmapFromUri(uri))
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) =
        context?.contentResolver?.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
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
}
