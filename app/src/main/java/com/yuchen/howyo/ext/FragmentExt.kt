package com.yuchen.howyo.ext

import androidx.fragment.app.Fragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.User
import com.yuchen.howyo.factory.CompanionViewModelFactory
import com.yuchen.howyo.factory.DetailViewModelFactory
import com.yuchen.howyo.factory.LocateViewModelFactory
import com.yuchen.howyo.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(schedule: Schedule?): DetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return DetailViewModelFactory(repository, schedule)
}
//
//fun Fragment.getVmFactory(days: List<Day>): FindLocationViewModelFactory {
//    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
//    return FindLocationViewModelFactory(repository, days)
//}

fun Fragment.getVmFactory(
    user: User
//                          , plan: Plan
): CompanionViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return CompanionViewModelFactory(repository, user)
}

fun Fragment.getVmFactory(plan: Plan): LocateViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return LocateViewModelFactory(repository, plan)
}