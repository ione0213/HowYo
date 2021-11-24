package com.yuchen.howyo.profile.setting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.PhotoData
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

class SettingViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    var currentUser = MutableLiveData<User>()

    private val _avatarPhoto = MutableLiveData<PhotoData>()

    val avatarPhotoData: LiveData<PhotoData>
        get() = _avatarPhoto

    private val _selectPhoto = MutableLiveData<Boolean>()

    val selectPhoto: LiveData<Boolean>
        get() = _selectPhoto

    private val _takePhoto = MutableLiveData<Boolean>()

    val takePhoto: LiveData<Boolean>
        get() = _takePhoto

    private val _isAvatarPhotoReady = MutableLiveData<Boolean>()

    val isAvatarPhotoReady: LiveData<Boolean>
        get() = _isAvatarPhotoReady

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        fetchLiveCurrentUserResult()
    }

    private fun fetchLiveCurrentUserResult() {

        _status.value = LoadApiStatus.LOADING
        this.currentUser = howYoRepository.getLiveUser(UserManager.userId ?: "")
    }

    fun setAvatarPhoto() {

        _avatarPhoto.value = PhotoData(
            null,
            this.currentUser.value?.avatar,
            this.currentUser.value?.fileName
        )

        _status.value = LoadApiStatus.DONE
    }

    fun selectPhoto() {
        _selectPhoto.value = true
    }

    fun onSelectedPhoto() {
        _selectPhoto.value = null
    }

    fun takePhoto() {
        _takePhoto.value = true
    }

    fun onTookPhoto() {
        _takePhoto.value = null
    }

    fun setAvatarBitmap(photoUri: Uri?) {

        val newPlanPhoto = PhotoData(
            photoUri,
            null,
            avatarPhotoData.value?.fileName,
            true
        )

        _avatarPhoto.value = newPlanPhoto
    }

    fun handleAvatar() {

        val avatarPhotoResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                when (avatarPhotoData.value?.isDeleted) {
                    true -> {
                        when {
                            avatarPhotoData.value!!.fileName?.isNotEmpty() == true -> {
                                avatarPhotoResult.add(deletePhoto(avatarPhotoData.value!!.fileName!!))
                            }
                            else -> {
                            }
                        }

                        avatarPhotoResult.add(uploadAvatarImg())
                    }
                    else -> {
                    }
                }
            }
            when {
                !avatarPhotoResult.contains(false) -> {
                    _isAvatarPhotoReady.value = true
                }
            }
        }
    }

    private suspend fun deletePhoto(fileName: String): Boolean =
        when (val result = howYoRepository.deletePhoto(fileName)) {
            is Result.Success -> {
                result.data
            }
            else -> false
        }

    private suspend fun uploadAvatarImg(): Boolean {

        val uri = avatarPhotoData.value?.uri
        val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
        val fileName = "${UserManager.currentUserEmail}_${formatter.format(Date())}"
        var uploadResult = false

        this.currentUser.value?.fileName = fileName

        when (val result = uri?.let { howYoRepository.uploadPhoto(it, fileName) }) {
            is Result.Success -> {
                this.currentUser.value?.avatar = result.data
                uploadResult = true
            }
            else -> {
                uploadResult = true
            }
        }
        return uploadResult
    }

    fun updateUser() {

        coroutineScope.launch {

            this@SettingViewModel.currentUser.value?.let { howYoRepository.updateUser(it) }

            _status.value = LoadApiStatus.DONE
        }
    }
}
