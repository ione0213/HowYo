package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.PlanViewModel
import com.yuchen.howyo.plan.cover.PlanCoverViewModel
import com.yuchen.howyo.plan.findlocation.FindLocationViewModel
import com.yuchen.howyo.profile.ProfileViewModel

class ViewModelFactory constructor(
    private val howYoRepository: HowYoRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(howYoRepository)
                isAssignableFrom(PlanCoverViewModel::class.java) ->
                    PlanCoverViewModel(howYoRepository)
                isAssignableFrom(PlanViewModel::class.java) ->
                    PlanViewModel(howYoRepository)
                isAssignableFrom(FindLocationViewModel::class.java) ->
                    FindLocationViewModel(howYoRepository)
                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(howYoRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}