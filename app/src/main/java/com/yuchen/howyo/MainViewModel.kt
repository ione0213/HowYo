package com.yuchen.howyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.DrawerToggleType

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
}