package com.yuchen.howyo.ext

import androidx.fragment.app.Fragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.data.*
import com.yuchen.howyo.factory.*
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.profile.friends.FriendFilter

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(
    plan: Plan?,
    day: Day?,
    schedule: Schedule?
): DetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return DetailViewModelFactory(repository, plan, day, schedule)
}

fun Fragment.getVmFactory(
    schedule: Schedule?,
    plan: Plan?,
    day: Day?
): EditDetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return EditDetailViewModelFactory(repository, schedule, plan, day)
}

//fun Fragment.getVmFactory(days: List<Day>): FindLocationViewModelFactory {
//    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
//    return FindLocationViewModelFactory(repository, days)
//}

fun Fragment.getVmFactory(plan: Plan?): PlanContentViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PlanContentViewModelFactory(repository, plan)
}

fun Fragment.getVmFactory(plan: Plan, accessPlanType: AccessPlanType): PlanViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PlanViewModelFactory(repository, plan, accessPlanType)
}

fun Fragment.getVmFactory(planId: String, mainType: MainItemType): CheckListViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return CheckListViewModelFactory(repository, planId, mainType)
}

fun Fragment.getVmFactory(payment: Payment?, plan: Plan): PaymentDetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PaymentDetailViewModelFactory(repository, payment, plan)
}

fun Fragment.getVmFactory(friendType: FriendFilter, userId: String): FriendItemViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return FriendItemViewModelFactory(repository, friendType, userId)
}

fun Fragment.getVmFactory(
    schedulePhoto: SchedulePhoto,
    schedulePhotos: SchedulePhotos
): DetailEditImageViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return DetailEditImageViewModelFactory(repository, schedulePhoto, schedulePhotos)
}

fun Fragment.getVmFactory(
    stringData: String
): StringViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return StringViewModelFactory(repository, stringData)
}