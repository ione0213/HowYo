package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.companion.CompanionViewModel
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel
import com.yuchen.howyo.plan.detail.view.DetailViewModel

//class CompanionViewModelFactory(
//    private val howYoRepository: HowYoRepository,
//    private val user: User
////    private val plan: Plan
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>) =
//        with(modelClass) {
//            when {
//                isAssignableFrom(CompanionViewModel::class.java) ->
//                    CompanionViewModel(howYoRepository, user)
//                else ->
//                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//            }
//        } as T
//}