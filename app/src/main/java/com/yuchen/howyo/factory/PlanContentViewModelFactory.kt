package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.copyplan.CopyPlanViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.comment.CommentViewModel
import com.yuchen.howyo.plan.companion.CompanionViewModel
import com.yuchen.howyo.plan.companion.locate.LocateViewModel
import com.yuchen.howyo.plan.cover.PlanCoverViewModel
import com.yuchen.howyo.plan.groupmessage.GroupMessageViewModel
import com.yuchen.howyo.plan.payment.PaymentViewModel

class PlanContentViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val plan: Plan?,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(LocateViewModel::class.java) ->
                    LocateViewModel(howYoRepository, plan)
                isAssignableFrom(PaymentViewModel::class.java) ->
                    PaymentViewModel(howYoRepository, plan)
                isAssignableFrom(GroupMessageViewModel::class.java) ->
                    GroupMessageViewModel(howYoRepository, plan)
                isAssignableFrom(PlanCoverViewModel::class.java) ->
                    PlanCoverViewModel(howYoRepository, plan)
                isAssignableFrom(CopyPlanViewModel::class.java) ->
                    CopyPlanViewModel(howYoRepository, plan)
                isAssignableFrom(CommentViewModel::class.java) ->
                    CommentViewModel(howYoRepository, plan)
                isAssignableFrom(CompanionViewModel::class.java) ->
                    CompanionViewModel(howYoRepository, plan)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
