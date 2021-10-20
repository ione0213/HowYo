package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.detail.view.DetailViewModel

class DetailViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val schedule: Schedule?,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(howYoRepository, schedule)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}