package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.detail.view.image.DetailViewImageViewModel
import com.yuchen.howyo.profile.ProfileViewModel
import com.yuchen.howyo.profile.author.AuthorProfileViewModel

class StringViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val stringData: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewImageViewModel::class.java) ->
                    DetailViewImageViewModel(howYoRepository, stringData)
                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(howYoRepository, stringData)
                isAssignableFrom(AuthorProfileViewModel::class.java) ->
                    AuthorProfileViewModel(howYoRepository, stringData)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
