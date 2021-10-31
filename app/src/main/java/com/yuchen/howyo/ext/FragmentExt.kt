package com.yuchen.howyo.ext

import androidx.fragment.app.Fragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.User
import com.yuchen.howyo.factory.*
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.profile.friends.FriendFilter

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(schedule: Schedule?): DetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return DetailViewModelFactory(repository, schedule)
}

fun Fragment.getVmFactory(schedule: Schedule?, planId: String?, dayId: String?): EditDetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return EditDetailViewModelFactory(repository, schedule, planId, dayId)
}

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

fun Fragment.getVmFactory(plan: Plan): PlanContentViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PlanContentViewModelFactory(repository, plan)
}

fun Fragment.getVmFactory(plan: Plan, accessPlanType: AccessPlanType): PlanViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PlanViewModelFactory(repository, plan, accessPlanType)
}

fun Fragment.getVmFactory(planId: String, mainType: String): CheckListViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return CheckListViewModelFactory(repository, planId, mainType)
}

fun Fragment.getVmFactory(payment: Payment?, plan: Plan): PaymentDetailViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return PaymentDetailViewModelFactory(repository, payment, plan)
}

fun Fragment.getVmFactory(friendType: FriendFilter): FriendItemViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return FriendItemViewModelFactory(repository, friendType)
}