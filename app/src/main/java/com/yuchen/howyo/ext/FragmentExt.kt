package com.yuchen.howyo.ext

import androidx.fragment.app.Fragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.factory.DetailViewModelFactory
import com.yuchen.howyo.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(schedule: Schedule?): DetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return DetailViewModelFactory(repository, schedule)
}