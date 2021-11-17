package com.yuchen.howyo

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.DrawerToggleType
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*

class MainViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // According to current fragment to change different drawer toggle
    val currentDrawerToggleType: LiveData<DrawerToggleType> =
        Transformations.map(currentFragmentType) {
            when (it) {
                CurrentFragmentType.NOTIFICATION,
                CurrentFragmentType.GROUP_MESSAGE,
                CurrentFragmentType.SHOPPING_LIST,
                CurrentFragmentType.CHECK_OR_SHOPPING_LIST,
                CurrentFragmentType.COMPANION_LOCATE,
                CurrentFragmentType.PAYMENT,
                CurrentFragmentType.PAYMENT_DETAIL,
                CurrentFragmentType.FIND_LOCATION,
                CurrentFragmentType.FRIENDS,
                CurrentFragmentType.SETTING,
                CurrentFragmentType.COMMENT,
                CurrentFragmentType.AUTHOR_PROFILE -> DrawerToggleType.BACK
                else -> DrawerToggleType.NORMAL
            }
        }

    private val _sharedToolbarTitle = MutableLiveData<String>()

    val sharedToolbarTitle: LiveData<String>
        get() = _sharedToolbarTitle

    private val _resetToolbar = MutableLiveData<Boolean>()

    val resetToolbar: LiveData<Boolean>
        get() = _resetToolbar

    // Handle navigation to home by bottom nav directly which includes icon change
    private val _navigateToHomeByBottomNav = MutableLiveData<Boolean>()

    val navigateToHomeByBottomNav: LiveData<Boolean>
        get() = _navigateToHomeByBottomNav

    private val _userLocation = MutableLiveData<Location>()

    val userLocation: LiveData<Location>
        get() = _userLocation

    private val _isAccessAppFirstTime = MutableLiveData<Boolean>()

    val isAccessAppFirstTime: LiveData<Boolean>
        get() = _isAccessAppFirstTime

    private val _isUserLocateServiceReady = MutableLiveData<Boolean>()

    val isUserLocateServiceReady: LiveData<Boolean>
        get() = _isUserLocateServiceReady

    private val _isBroadcastUnRegistered = MutableLiveData<Boolean>()

    val isBroadcastUnRegistered: LiveData<Boolean>
        get() = _isBroadcastUnRegistered

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        _sharedToolbarTitle.value = ""
    }

    //For shared fragment title
    fun setSharedToolbarTitle(title: String) {
        _sharedToolbarTitle.value = title
    }

    fun resetSharedToolbarTitle() {
        _sharedToolbarTitle.value = ""
    }

    fun navigateToHomeByBottomNav() {
        _navigateToHomeByBottomNav.value = true
    }

    fun onHomeNavigated() {
        _navigateToHomeByBottomNav.value = null
    }

    fun resetToolbar() {
        _resetToolbar.value = true
    }

    fun onResetToolbar() {
        _resetToolbar.value = null
    }

    fun setUserLocateServiceStatus(userLocateServiceBound: Boolean) {
        _isUserLocateServiceReady.value = userLocateServiceBound
    }

    fun onSetUserLocateServiceStatus() {
        _isUserLocateServiceReady.value = null
    }

    fun setIsAccessAppFirstTime() {
        _isAccessAppFirstTime.value = true
    }

    fun onSetIsAccessAppFirstTime() {
        _isAccessAppFirstTime.value = null
    }

    fun setUserLocation(location: Location) {
        _userLocation.value = location
    }

    fun onSetUserLocation() {
        _userLocation.value = null
    }

    fun setBroadcastRegistered() {
        _isBroadcastUnRegistered.value = true
    }

    fun onsetBroadcastRegistered() {
        _isBroadcastUnRegistered.value = null
    }

    fun updateUser(location: Location) {

        coroutineScope.launch {

            var user: User?

            withContext(Dispatchers.IO) {
                user = getUserResult()
            }

            user?.apply {
                latitude = location.latitude
                longitude = location.longitude
            }

            withContext(Dispatchers.IO) {
                user?.let { howYoRepository.updateUser(it) }
            }

            onSetUserLocation()
        }
    }

    private suspend fun getUserResult(): User {

        var user = User()

        when (val result = UserManager.userId?.let { howYoRepository.getUser(it) }) {
            is Result.Success -> {
                user = result.data
            }
            else -> {
                howYoRepository.signOut()
                UserManager.clear()
            }
        }

        return user
    }
}