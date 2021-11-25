package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.plan.PlanViewModel

class PlanViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val plan: Plan?,
    private val accessPlanType: AccessPlanType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PlanViewModel::class.java) ->
                    PlanViewModel(howYoRepository, plan, accessPlanType)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
