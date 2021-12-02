package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.discover.DiscoverViewModel
import com.yuchen.howyo.favorite.FavoriteViewModel
import com.yuchen.howyo.home.HomeViewModel
import com.yuchen.howyo.home.notification.NotificationViewModel
import com.yuchen.howyo.plan.findlocation.FindLocationViewModel
import com.yuchen.howyo.profile.setting.SettingViewModel
import com.yuchen.howyo.signin.SignInViewModel

class ViewModelFactory constructor(
    private val howYoRepository: HowYoRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(howYoRepository)
                isAssignableFrom(FindLocationViewModel::class.java) ->
                    FindLocationViewModel(howYoRepository)
                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(howYoRepository)
                isAssignableFrom(NotificationViewModel::class.java) ->
                    NotificationViewModel(howYoRepository)
                isAssignableFrom(DiscoverViewModel::class.java) ->
                    DiscoverViewModel(howYoRepository)
                isAssignableFrom(FavoriteViewModel::class.java) ->
                    FavoriteViewModel(howYoRepository)
                isAssignableFrom(SignInViewModel::class.java) ->
                    SignInViewModel(howYoRepository)
                isAssignableFrom(SettingViewModel::class.java) ->
                    SettingViewModel(howYoRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
